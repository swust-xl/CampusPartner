package org.campus.partner.util.time;

import java.net.InetAddress;

/**
 * 时间服务器地址的封装.
 * 
 * @author xl
 * @since 1.0.0
 */
public class TimeServerAddress {
    private InetAddress host;
    private int port;

    /**
     * 通过地址和端口构造.
     *
     * @param host
     *            主机地址对象
     * @param port
     *            主机端口
     */
    public TimeServerAddress(InetAddress host, int port) {
        super();
        this.host = host;
        this.port = port;
    }

    /**
     * 通过地址构造.<br/>
     * 默认端口: {@link org.apache.commons.net.ntp.NTPUDPClient.DEFAULT_PORT}=123
     *
     * @param host
     *            主机地址对象
     */
    public TimeServerAddress(InetAddress host) {
        super();
        this.host = host;
        this.port = 123;
    }

    /**
     * 获取host属性字段的值.
     *
     * @return 类型为InetAddress的host属性字段的值.
     */
    public InetAddress getHost() {
        return host;
    }

    /**
     * 设置host属性字段的值.
     *
     * @param host
     *            待设置类型为InetAddress的host属性字段的值.
     */
    public void setHost(InetAddress host) {
        this.host = host;
    }

    /**
     * 获取port属性字段的值.
     *
     * @return 类型为int的port属性字段的值.
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置port属性字段的值.
     *
     * @param port
     *            待设置类型为int的port属性字段的值.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * 服务器地址和端口转换成字符串.
     *
     * @return 地址和端口转换成的字符串
     * @see java.lang.Object#toString().
     * @author xl
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return "TimeServerAddress [host=" + host + ", port=" + port + "]";
    }
}