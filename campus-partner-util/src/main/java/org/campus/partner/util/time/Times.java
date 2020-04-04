package org.campus.partner.util.time;

import java.util.Date;
import java.util.Queue;

/**
 * 时间源获取接口.
 * </p>
 * 针对{@code WAN}, {@code LAN}, {@code Local}方式获取时间的接口.
 *
 * @author xl
 * @since 1.0.0
 */
public interface Times {
    /**
     * 通过外网获取时间.
     *
     * @param wanTimeServerPool
     *            外网时间源服务器地址池
     * @return 网络时间源标准时间
     * @author xl
     * @since 1.0.0
     */
    Date getWANNetDate(Queue<TimeServerAddress> wanTimeServerPool);

    /**
     * 通过局域网获取时间.
     *
     * @param extTimeServerPool
     *            局域网时间源服务器地址池
     * @return 局域网时间源标准时间
     * @author xl
     * @since 1.0.0
     */
    Date getLANNetDate(Queue<TimeServerAddress> wanTimeServerPool);

    /**
     * 获取本地日期.
     * 
     * @return 本地时间源时间
     * @author xl
     * @since 1.0.0
     */
    Date getLocalDate();
}