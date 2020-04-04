package org.campus.partner.util.time;

import java.net.InetAddress;
import java.util.Date;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.SystemUtil;

/**
 * 当前标准时间.<br>
 * 基于NTP的标准时间，如果NTP服务列表为空或所有服务不可达，则默认使用当前系统时间.
 * </p>
 * 实现思路参见：<br>
 * <a href=
 * 'https://www.javatips.net/api/TNT4J-master/src/main/java/com/jkoolcloud/tnt4j/utils/TimeService.java'>
 * {@link https://www.javatips.net/api/TNT4J-master/src/main/java/com/jkoolcloud/tnt4j/utils/TimeService.java}
 * </a>
 *
 * @see {@link cn.signit.wesign.lib.common.time.AbstractTimes}.
 * @author xl
 * @since 1.0.0
 */
public class StandardTimes extends AbstractTimes {
    protected static NTPUDPClient NTP_UDP_CLIENT = new NTPUDPClient();
    private static AtomicLong ADJUSTMENT = new AtomicLong(0L);
    protected static ScheduledExecutorService SCHEDULED = null;

    static {
        updateTime();
        scheduleUpdatesService();
    }

    private static void updateTime() {
        if (NTP_UDP_CLIENT == null) {
            NTP_UDP_CLIENT = new NTPUDPClient();
        }
        NTP_UDP_CLIENT.setDefaultTimeout(DEFAULT_TIMEOUT_MILLIS);
        int size = TIME_SERVER_POOL.size();
        boolean updated = false;
        for (int i = 0; i < size; i++) {
            TimeServerAddress tsa = TIME_SERVER_POOL.peek();
            try {
                TimeInfo timeInfo = NTP_UDP_CLIENT.getTime(tsa.getHost(), tsa.getPort());
                timeInfo.computeDetails();
                ADJUSTMENT.set(timeInfo.getOffset() == null ? 0
                        : timeInfo.getOffset()
                                .longValue());
                updated = true;
                break;
            } catch (Throwable e) {
                TIME_SERVER_POOL.offer(TIME_SERVER_POOL.poll());
            }
        }
        if (!updated) {
            System.err.println("Warning: All NTP servers are not available,"
                    + "will auto invoke System.currentTimeMillis() method to update time");
            ADJUSTMENT.set(0);
        }
    }

    // 新开守护线程定时更新NTP时间服务
    private static void scheduleUpdatesService() {
        if (SCHEDULED == null) {
            // 注册一个NTP时间定时同步的线程池
            SCHEDULED = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                int count = 0;

                @Override
                public Thread newThread(Runnable r) {
                    Thread task = new Thread(r, "ntp-sync-" + count++);
                    task.setDaemon(true);
                    return task;
                }
            });
            // 以NTP为准的时间漂移监控任务
            SCHEDULED.submit(new Runnable() {
                private static final long TIME_CHECK_DRIFT_INTERNAL_MILLIS = 60000L;
                private static final long TIME_CHECK_DRIFT_MILLIS_LIMIT = 1L;
                private static final long ONE_M = 1000000L;
                /**
                 * 时间的漂移量
                 */
                long drift = 0L;

                @Override
                public void run() {
                    long start = System.nanoTime();
                    long base = System.currentTimeMillis() - (start / ONE_M);
                    while (true) {
                        try {
                            Thread.sleep(TIME_CHECK_DRIFT_INTERNAL_MILLIS);
                            long now = System.nanoTime();
                            drift = System.currentTimeMillis() - (now / ONE_M) - base;
                            if (Math.abs(drift) >= TIME_CHECK_DRIFT_MILLIS_LIMIT) {
                                syncNTP();
                                start = System.nanoTime();
                                base = System.currentTimeMillis() - (start / ONE_M);
                            }
                        } catch (InterruptedException e) {
                            System.err.println("SLEEP当前NTP时间同步监控服务线程失败：" + ExceptionFormater.format(e));
                        }
                    }
                }

                // 同步NTP时间
                private void syncNTP() {
                    StandardTimes.updateTime();
                }
            });
        }
    }

    /**
     * 通过默认时间服务器地址和端口初始化构造方法.
     *
     */
    public StandardTimes() {
        super(null);
    }

    /**
     * 通过指定时间服务器地址和默认端口初始化构造方法.
     * 
     * @param timeServerAddrs
     *            指定时间服务器地址数组，默认采用端口:
     *            <code>{@link org.apache.commons.net.ntp.NTPUDPClient.DEFAULT_PORT}</code>
     *
     */
    public StandardTimes(InetAddress... timeServerAddrs) {
        for (InetAddress inetAddress : timeServerAddrs) {
            TIME_SERVER_POOL.add(new TimeServerAddress(inetAddress));
        }
    }

    /**
     * 通过指定时间服务器地址和默认端口初始化构造方法.
     * 
     * @param timeServerAddrs
     *            指定时间服务器地址数组，默认采用端口:
     *            <code>{@link org.apache.commons.net.ntp.NTPUDPClient.DEFAULT_PORT}</code>
     *
     */
    public StandardTimes(String... timeServerAddrs) {
        for (String inetAddress : timeServerAddrs) {
            TIME_SERVER_POOL.add(new TimeServerAddress(NetUtil.parseHostAddress(inetAddress)));
        }
    }

    /**
     * 通过 <code>host</code> 和 <code>port</code> 初始化构造方法.
     *
     * @param timeServerPool
     *            指定时间服务器地址和端口的<code>Map</code>
     */
    public StandardTimes(Map<InetAddress, Integer> timeServerPool) {
        super(timeServerPool);
    }

    /**
     * 通过外网获取日期
     * 
     * @param timeServerPool
     *            时间服务池
     * @return 当前外网标准时间
     */
    @Override
    public Date getWANNetDate(Queue<TimeServerAddress> timeServerPool) {
        TIME_SERVER_POOL.addAll(timeServerPool);
        return getStandardDate();
    }

    /**
     * 通过局域网获取日期
     * 
     * @param timeServerPool
     *            时间服务池
     * @return 当前局域网标准时间
     */
    @Override
    public Date getLANNetDate(Queue<TimeServerAddress> timeServerPool) {
        TIME_SERVER_POOL.addAll(timeServerPool);
        return getStandardDate();
    }

    /**
     * 通过本机获取时间
     * 
     * @return 当前本机的系统标准时间
     */
    @Override
    public Date getLocalDate() {
        return SystemUtil.systemTimeAsDate();
    }

    /**
     * 获取当前标准时间（优先级：网络时间>局域网时间>本地时间）
     * 
     * @return 当前标准时间
     */
    @Override
    public Date getStandardDate() {
        return new Date(System.currentTimeMillis() + ADJUSTMENT.get());
    }
}
