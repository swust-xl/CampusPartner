package org.campus.partner.util.time;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.net.ntp.NTPUDPClient;

/**
 * 抽象的时间源获取接口.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public abstract class AbstractTimes implements Times {
    protected static final Queue<TimeServerAddress> TIME_SERVER_POOL = new ConcurrentLinkedQueue<TimeServerAddress>();
    protected static final int DEFAULT_TIMEOUT_MILLIS = 200;
    public static final int DEFAULT_PORT = NTPUDPClient.DEFAULT_PORT;

    static {
        useDefaultTimeServerPool();
    }

    protected AbstractTimes() {}

    /**
     * 通过 <code>host</code> 和 <code>port</code> 初始化构造方法.
     *
     * @param timeServerPool
     *            指定时间服务器地址和端口的<code>Map</code>. 如果<code>timeServerPool</code>
     *            为<code>null</code>, 则使用默认时间服务器地址和端口.
     */
    public AbstractTimes(Map<InetAddress, Integer> timeServerPool) {
        if (timeServerPool != null && !timeServerPool.isEmpty()) {
            for (Map.Entry<InetAddress, Integer> entry : timeServerPool.entrySet()) {
                InetAddress addr = entry.getKey();
                Integer port = entry.getValue();
                if (addr == null || port == null || port <= 0 || port > 65535) {
                    continue;
                }
                TIME_SERVER_POOL.add(new TimeServerAddress(addr, port));
            }
        }
    }

    /**
     * 获取可用的标准时间.<br/>
     * 优先级：网络时间>局域网时间>本地时间.
     *
     * @return 可用的标准时间
     * @author xl
     * @since 1.0.0
     */
    public abstract Date getStandardDate();

    /**
     * 获取已有的时间服务器队列池.
     *
     * @return 已有的时间服务器队列池
     * @author xl
     * @since 1.0.0
     */
    public Queue<TimeServerAddress> getTimeServerPool() {
        return TIME_SERVER_POOL;
    }

    /**
     * 构建默认时间服务器池.
     * 
     * @author xl
     * @since 1.0.0
     */
    protected static void useDefaultTimeServerPool() {
        // 添加默认NTP服务器
        addAliyunNTP4LAN();
        addAliyunNTP4WAN();
        addOthersNTP4WAN();
        addNTP4WAN();
    }

    private static Queue<TimeServerAddress> addNTP(Queue<TimeServerAddress> timeServerPool, String... ipsOrNames) {
        for (String ipOrName : ipsOrNames) {
            InetAddress addr = NetUtil.parseHostAddress(ipOrName);
            if (addr != null) {
                timeServerPool.add(new TimeServerAddress(addr));
            }
        }
        return timeServerPool;
    }

    // 内网-阿里云NTP
    private static void addAliyunNTP4LAN() {
        addNTP(TIME_SERVER_POOL, "10.143.33.50", "10.143.33.51", "10.143.33.49", "10.143.0.44", "10.143.0.45",
                "10.143.0.46");
    }

    // 公网-阿里云NTP
    private static void addAliyunNTP4WAN() {
        addNTP(TIME_SERVER_POOL, "182.92.12.11", "120.25.108.11", "115.28.122.198");
    }

    // 公网-others NTP
    private static void addOthersNTP4WAN() {
        addNTP(TIME_SERVER_POOL, "202.112.0.7", "202.112.7.13", "cn.ntp.org.cn", "edu.ntp.org.cn");
    }

    // 公网-ntp.org NTP
    private static void addNTP4WAN() {
        addNTP(TIME_SERVER_POOL, "0.pool.ntp.org", "1.pool.ntp.org", "2.pool.ntp.org", "3.pool.ntp.org");
    }

}