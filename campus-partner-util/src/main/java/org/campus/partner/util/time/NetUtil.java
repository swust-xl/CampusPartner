package org.campus.partner.util.time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.SystemUtil;

/**
 * 网络相关的工具类.
 * </p>
 *
 * @author zhd
 * @since 1.0.0
 */
public class NetUtil {
    private static final String LOOPBACK = "127.0.0.1";
    private static final int PING_TIMEOUT_MILLS = 3000;
    private static final int PING_TIMES = 5;

    private NetUtil() {}

    /**
     * 是否内网地址判断.
     *
     * @param netAddr
     *            IP地址/域名/URL等字符串
     * @return <code>true</code> 是内网地址; <code>false</code> 不是内网地址
     * @author zhd
     * @since 1.0.0
     */
    public static boolean isPrivateAddress(String netAddr) {
        InetAddress addr = parseHostAddress(netAddr);
        if (addr == null) {
            return false;
        }
        return isPrivateAddress(addr);
    }

    /**
     * 是否内网地址判断.
     *
     * @param netAddr
     *            IP地址或域名对象
     * @return <code>true</code> 是内网地址; <code>false</code> 不是内网地址
     * @author zhd
     * @since 1.0.0
     */
    public static boolean isPrivateAddress(InetAddress netAddr) {
        if (netAddr == null) {
            return false;
        }
        return netAddr.isSiteLocalAddress() || netAddr.isLoopbackAddress() || netAddr.isLinkLocalAddress()
                || netAddr.isAnyLocalAddress() || netAddr.isMCLinkLocal() || netAddr.isMCNodeLocal()
                || netAddr.isMCOrgLocal() || netAddr.isMCSiteLocal();
    }

    /**
     * 检测指定主机地址是否能连通（PING）.
     *
     * @param netAddr
     *            IP地址/域名/URL等字符串
     * @return <code>true</code> 能连通（PING）; <code>false</code> 无法连通（PING）
     * @author zhd
     * @since 1.0.0
     */
    public static boolean isPing(String netAddr) {
        InetAddress addr = parseHostAddress(netAddr);
        if (addr == null) {
            return false;
        }
        return isPing(addr);
    }

    /**
     * 检测指定主机地址是否能连通（PING）.
     *
     * @param netAddr
     *            IP地址或域名对象
     * @return <code>true</code> 能连通（PING）; <code>false</code> 无法连通（PING）
     * @author zhd
     * @since 1.0.0
     */
    public static boolean isPing(InetAddress netAddr) {
        if (netAddr == null) {
            throw new IllegalArgumentException("输入参数不合法!");
        }
        boolean tryPingWay = isPingByJava(netAddr);
        if (tryPingWay) {
            return tryPingWay;
        }
        return isPingByCMD(netAddr);
    }

    // java自带方式PING
    private static boolean isPingByJava(InetAddress netAddr) {
        try {
            return netAddr.isReachable(PING_TIMEOUT_MILLS);
        } catch (IOException e) {
            return false;
        }
    }

    // 调用CMD命令行方式PING
    private static boolean isPingByCMD(InetAddress netAddr) {
        BufferedReader in = null;
        Runtime r = Runtime.getRuntime();
        String pingCMD = null;
        String hostAddr = "0.0.0.0".equals(netAddr.getHostAddress()) ? "127.0.0.1" : netAddr.getHostAddress();
        if (SystemUtil.isWindowsOS()) {
            pingCMD = String.format("ping %s -n %s -w %s", hostAddr, PING_TIMES, PING_TIMEOUT_MILLS);
        } else {
            pingCMD = String.format("ping %s -c %s -w %s", hostAddr, PING_TIMES, PING_TIMEOUT_MILLS / 1000);
        }
        try {
            Process p = r.exec(pingCMD);
            if (p == null) {
                return false;
            }
            // 逐行检查输出,计算类似出现=41.3ms TTL=51字样的次数
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            int connectedCount = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                connectedCount += getCheckResult(line);
            }
            return connectedCount >= (PING_TIMES >> 1);
        } catch (Throwable ex) {
            return false;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                System.err.println("WARN: invoke local CMD error: " + ExceptionFormater.format(e));
            }
        }
    }

    // 若line含有ms和TTL=字样,说明已经ping通,返回1,否則返回0.
    private static int getCheckResult(String line) {
        if (line == null || line.trim()
                .isEmpty()) {
            return 0;
        }
        String formatLine = line.toLowerCase();
        if (formatLine.indexOf("ttl=") >= 0 && formatLine.indexOf("ms") >= 0) {
            return 1;
        }
        return 0;
    }

    /**
     * 解析输入的网络地址，支持：IP/域名/URL.
     *
     * @param netAddr
     *            IP/域名/URL
     * @return 解析后的主机名.若解析失败,则返回<code>null</code>.
     *         未指定地址<code>0.0.0.0</code>会自动解析为<code>127.0.0.1</code>
     * @author zhd
     * @since 1.0.0
     */
    public static String parseHostAddressAsString(String netAddr) {
        if (netAddr == null || netAddr.trim()
                .isEmpty()) {
            return null;
        }
        if (LOOPBACK.equals(netAddr) || "localhost".equals(netAddr) || "0.0.0.0".equals(netAddr)) {// 本地回环或未指定
            return LOOPBACK;
        }
        String tmpNetAddr = netAddr;
        int idx = tmpNetAddr.indexOf("//");
        if (idx >= 0) { // 自动识别为URL
            try {
                URL url = new URL(tmpNetAddr);
                tmpNetAddr = url.getHost();
            } catch (MalformedURLException e) {
                tmpNetAddr = tmpNetAddr.substring(idx + 2);
                idx = tmpNetAddr.indexOf(":");
                if (idx > 0) {
                    tmpNetAddr = tmpNetAddr.substring(0, idx)
                            .trim();
                } else {
                    idx = tmpNetAddr.indexOf("/");
                    if (idx > 0) {
                        tmpNetAddr = tmpNetAddr.substring(0, idx)
                                .trim();
                    } else {
                        tmpNetAddr = tmpNetAddr.trim();
                    }
                }
            }
        }
        return tmpNetAddr;
    }

    /**
     * 解析输入的网络地址，支持：IP/域名/URL.
     *
     * @param netAddr
     *            IP/域名/URL
     * @return 解析后的主机名对象{@link java.net.InetAddress}.若解析失败,则返回<code>null</code>.
     *         未指定地址<code>0.0.0.0</code>会自动解析为<code>127.0.0.1</code>对应的{@link java.net.InetAddress}
     * @author zhd
     * @since 1.0.0
     */
    public static InetAddress parseHostAddress(String netAddr) {
        String tmpNetAddr = parseHostAddressAsString(netAddr);
        if (tmpNetAddr == null) {
            return null;
        }
        try {
            if (LOOPBACK.equals(netAddr)) {
                return InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 });
            }
            return InetAddress.getByName(tmpNetAddr);
        } catch (Throwable e) {
            System.err.println("WARN: parse net address: '" + tmpNetAddr + "' error: " + ExceptionFormater.format(e));
            return null;
        }
    }
}