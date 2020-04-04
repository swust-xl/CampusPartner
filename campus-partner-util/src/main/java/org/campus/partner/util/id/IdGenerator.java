package org.campus.partner.util.id;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.campus.partner.util.SystemUtil;

/**
 * 分布式全局唯一ID-"ID"生成器
 * <p/>
 * 基于Boundary flake（Snowflake的一个变种）实现的分布式唯一ID的生成, 支持多线程操作，此类的实现是线程安全的.
 *
 * @author xl
 * @since 1.0.0
 */
public class IdGenerator implements Generator {
    /**
     * 默认ID分隔符
     */
    private static volatile String DEFAULT_ID_SEPARATOR = "_";

    /**
     * 默认ID开始字符
     */
    private static volatile String DEFAULT_ID_START = "ID";

    /**
     * 当前序列号(支持原子操作)
     */
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    /**
     * 上一毫秒使用的时间戳(支持原子操作)
     */
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong(System.currentTimeMillis());

    /**
     * 最大序列号
     */
    private final int maxSequence;

    /**
     * 真正的分布式唯一ID提供者
     */
    private IdProvider idProvider;

    /**
     * 默认采用 {@link org.campus.partner.util.id.FlakeIdProvider}
     * 作为提供者方式生成全局唯一ID的构造方法.
     */
    public IdGenerator() {
        this.idProvider = new FlakeIdProvider(SystemUtil.macAddressLong());
        this.maxSequence = this.idProvider.maxSequence();
    }

    /**
     * 指定一个{@link org.campus.partner.util.id.IdProvider} 作为生成全局唯一ID提供者的构造方法.
     */
    public IdGenerator(IdProvider idProvider) {
        this.idProvider = idProvider;
        this.maxSequence = this.idProvider.maxSequence();
    }

    /**
     * 自定义生成的ID的分隔符. 默认："_".
     *
     * @param wsidSeparator
     *            分隔符
     * @return 当前对象
     * @author xl
     * @since 1.0.0
     */
    public IdGenerator wsidSeparator(String wsidSeparator) {
        DEFAULT_ID_SEPARATOR = wsidSeparator;
        return this;
    }

    /**
     * 自定义生成的ID的开始标识头. 默认："ID".
     *
     * @param wsidStart
     *            开始标识头
     * @return 当前对象
     * @author xl
     * @since 1.0.0
     */
    public IdGenerator wsidStart(String wsidStart) {
        DEFAULT_ID_START = wsidStart;
        return this;
    }

    /**
     * 生成全局唯一ID.
     *
     * @param maxWaitMillis
     *            生成ID需要等待的最大毫秒数
     * @return 生成的全局ID对象
     * @throws InterruptedException
     *             抛出线程中断的异常，一般发生在超过最大等待毫秒数或者倒退设置了新时间时抛出该异常
     * @throws RuntimeException
     *             产生ID的速度大于最大允许序列号值抛出该异常
     * @see org.campus.partner.util.id.Generator#id(int)
     * @author xl
     * @since 1.0.0
     */
    @Override
    public synchronized Id id(int maxWaitMillis) {
        if (SEQUENCE.get() > maxSequence) {
            try {
                Thread.sleep(maxWaitMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException("generate ID too fast!");
            }
        }
        final long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp != LAST_TIMESTAMP.get()) {
            LAST_TIMESTAMP.set(currentTimestamp);
            SEQUENCE.set(0);
        }
        final int seq = SEQUENCE.incrementAndGet();
        Id wsid = new Id() {
            @Override
            public String asString() {
                return idProvider.encodeAsString(currentTimestamp, seq);
            }

            @Override
            public long asLong() {
                return idProvider.encodeAsLong(currentTimestamp, seq);
            }

            @Override
            public byte[] asBytes() {
                return idProvider.encodeAsBytes(currentTimestamp, seq);
            }
        };
        return wsid;
    }

    /**
     * 获取当前最新的序列号.
     *
     * @return 最新序列号值
     * @author xl
     * @since 1.0.0
     */
    public int getCurrentSequence() {
        return SEQUENCE.get();
    }

    /**
     * 获取允许的最大的序列号.
     *
     * @return 最大序列号值
     * @author xl
     * @since 1.0.0
     */
    public int getMaxSequence() {
        return this.maxSequence;
    }

    /**
     * 获取上一毫秒使用的时间戳.
     *
     * @return 上一毫秒时间戳
     * @author xl
     * @since 1.0.0
     */
    public long getLastTimestamp() {
        return LAST_TIMESTAMP.get();
    }

    /**
     * 获取分布式全局唯一ID（WSID）. <br/>
     * 默认方式生成ID的字符串表示形式. eg:0000016ce3113ec3005056c000080001
     * 
     * @return 应用全局唯一ID(WSID)的字符串表示
     * @author xl
     * @since 1.0.0
     */
    public String getWsid() {
        return id(DEFAULT_WAIT_MILLIS).asString();
    }

    /**
     * 获取分布式全局唯一ID（WSID）. <br/>
     * 指定一个前缀的方式生成ID的字符串表示形式. eg:XXXX_0000016ce3113ec3005056c000080001
     * 
     * @param prefix
     *            为ID指定一个前缀
     * @return 应用全局唯一ID(WSID)的字符串表示
     * @author xl
     * @since 1.0.0
     */
    public String getId(String prefix) {
        return prefix == null ? getWsid()
                : prefix.concat(DEFAULT_ID_SEPARATOR)
                        .concat(getWsid());
    }

    /**
     * 获取分布式全局唯一ID（WSID）. <br/>
     * 默认方式生成ID的原始二进制表示形式.
     *
     * @return 应用全局唯一ID(WSID)的原始二进制表示
     * @author xl
     * @since 1.0.0
     */
    public byte[] getWsidBytes() {
        return getWsid().getBytes();
    }

    /**
     * 获取分布式全局唯一ID（WSID）. <br/>
     * 指定一个前缀的方式生成ID的原始二进制表示形式.
     * 
     * @param prefix
     *            为ID指定一个前缀
     * @return 应用全局唯一ID(WSID)的原始二进制表示
     * @author xl
     * @since 1.0.0
     */
    public byte[] getWsidBytes(String prefix) {
        return getId(prefix).getBytes();
    }

    /**
     * 获取分布式全局唯一ID. <br/>
     * 默认方式 eg:ID_xxxxxxxxxxxxxxxxxxxxx
     * 
     * @return 应用全局唯一ID的字符串表示
     * @author xl
     * @since 1.0.0
     */
    public String getId() {
        return getId(DEFAULT_ID_START.concat(DEFAULT_ID_SEPARATOR));
    }

    public static void main(String[] args) {
        IdGenerator wsidGenerator = new IdGenerator();
        System.out.println(wsidGenerator.getId("UID"));
    }
}