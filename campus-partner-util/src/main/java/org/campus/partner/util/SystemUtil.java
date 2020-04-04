package org.campus.partner.util;

import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;

import javax.xml.bind.DatatypeConverter;

import static java.net.NetworkInterface.getNetworkInterfaces;

/**
 * 系统操作工具类.
 * <p/>
 * 获取JavaVM或操作系统相关的属性信息.
 *
 * @author xl
 * @since 1.0.0
 */
public class SystemUtil {
    /**
     * 用于getOverride()方法重写MAC地址(16进制表示)的系统属性名.
     */
    public static final String OVERRIDE_MAC_HEX_PROP = "override.hex.mac";

    /**
     * 用于getOverride()方法重写MAC地址（Long型表示）的系统属性名.
     */
    public static final String OVERRIDE_MAC_LONG_PROP = "override.long.mac";

    /**
     * 可重写的IVM的PID系统属性名.
     */
    public static final String OVERRIDE_PID_PROP = "override.pid";

    /**
     * 当前操作系统的描述名称.
     */
    public static final String OS = System.getProperty("os.name")
            .toLowerCase();

    private SystemUtil() {}

    /**
     * 如果系统属性OVERRIDE_MAC_PROP已经被设置了，则直接 返回重写的MAC地址， 如果没有设置或设置的MAC地址不合法，则返回nul.
     *
     * @return 被重写的MAC地址
     * @author xl
     * @since 1.0.0
     */
    private static byte[] getOverride() {
        String overrideMac = System.getProperty(OVERRIDE_MAC_HEX_PROP);
        byte[] macBytes = null;
        if (overrideMac != null) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String[] rawBytes = overrideMac.split(":");
            if (rawBytes.length == 6) {
                try {
                    for (String b : rawBytes) {
                        out.write(Integer.parseInt(b, 16));
                    }
                    macBytes = out.toByteArray();
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
        if (macBytes == null) {
            overrideMac = System.getProperty(OVERRIDE_MAC_LONG_PROP);
            if (overrideMac != null) {
                macBytes = getBytes(Long.parseLong(overrideMac));
            }
        }
        return macBytes;
    }

    /**
     * 返回第一个找到的网卡对应的6个字节数组的MAC地址，
     * 如果没有找到网卡则抛出{@link UnsupportedOperationException}异常.
     *
     * @return 第一个网卡的硬件地址，6个字节数组
     * @throws UnsupportedOperationException
     *             未找到网卡异常
     * @author xl
     * @since 1.0.0
     */
    private static byte[] realMacAddress() {
        byte[] mac = null;
        try {
            Enumeration<NetworkInterface> networkInterfaces = getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface n = networkInterfaces.nextElement();
                byte[] possibleMac = n.getHardwareAddress();
                if (possibleMac != null && possibleMac.length == 6) {
                    mac = possibleMac;
                    break;
                }
            }
            if (mac == null) {
                throw new UnsupportedOperationException(
                        "Could not retrieve hardware MAC address, no MAC addresses detected");
            }
            System.setProperty(OVERRIDE_MAC_LONG_PROP, String.valueOf(getLong(mac)));
        } catch (SocketException e) {
            throw new UnsupportedOperationException("Could not retrieve hardware MAC address, SocketException occurred",
                    e);
        }
        return mac;
    }

    // long -> byte[]
    private static byte[] getBytes(long x) {
        byte[] bb = new byte[6];
        bb[0] = (byte) (x >> 40);
        bb[1] = (byte) (x >> 32);
        bb[2] = (byte) (x >> 24);
        bb[3] = (byte) (x >> 16);
        bb[4] = (byte) (x >> 8);
        bb[5] = (byte) (x >> 0);
        return bb;
    }

    // byte[] -> long
    private static long getLong(byte[] bb) {
        return (((long) bb[0] & 0xff) << 40) | (((long) bb[1] & 0xff) << 32) | (((long) bb[2] & 0xff) << 24)
                | (((long) bb[3] & 0xff) << 16) | (((long) bb[4] & 0xff) << 8) | (((long) bb[5] & 0xff) << 0);
    }

    /**
     * 返回6个字节的代表当前主机上找到的第一个可用网卡的硬件MAC地址，
     * 如果已设置了系统属性OVERRIDE_MAC_PROP，则采用此MAC地址值返回，否则每次实时获取.
     * 如果没有找到网卡则抛出{@link UnsupportedOperationException}异常.
     * 
     * @return 基于网卡的硬件地址字节数组（6个byte数组，未格式化）表示形式
     * @author xl
     * @since 1.0.0
     */
    public static byte[] macAddress() {
        byte[] override = getOverride();
        return override != null ? override : realMacAddress();
    }

    /**
     * 返回Long型(基于6个字节数组，剩余2个字节高位全补0)的代表当前主机上找到的第一个可用网卡的硬件MAC地址，
     * 如果已设置了系统属性OVERRIDE_MAC_PROP，则采用此MAC地址值返回，否则每次实时获取.
     * 如果没有找到网卡则抛出{@link UnsupportedOperationException}异常.
     * 
     * @return 基于网卡的硬件地址Long型(基于6个字节数组)表示形式
     * @author xl
     * @since 1.0.0
     */
    public static long macAddressLong() {
        byte[] bb = macAddress();
        return getLong(bb);
    }

    /**
     * 以16进制方式返回格式化后的MAC地址， 默认每个byte的16进制表示形间":"分隔，且采用大写表示形式.
     *
     * @return 16进制表示的MAC地址
     * @author xl
     * @since 1.0.0
     */
    public static String macAddressHex() {
        return macAddressHex(":", false);
    }

    /**
     * 以16进制方式返回格式化后的MAC地址， 默认每个byte的16进制表示形式间使用separator分隔，且采用大写表示形式.
     * 
     * @param separator
     *            每个byte的16进制表示形式间使用的分隔符
     * @return 16进制表示的MAC地址
     * @author xl
     * @since 1.0.0
     */
    public static String macAddressHex(String separator) {
        return macAddressHex(separator, false);
    }

    /**
     * 以16进制方式返回格式化后的MAC地址， 默认每个byte的16进制表示形式间":"分隔，且采用isLowerCase表示大小写形式.
     *
     * @return 16进制表示的MAC地址
     * @author xl
     * @since 1.0.0
     */
    public static String macAddressHex(boolean isLowerCase) {
        return macAddressHex(":", isLowerCase);
    }

    /**
     * 以16进制方式返回格式化后的MAC地址.
     *
     * @param separator
     *            每个byte的16进制表示形式间使用的分隔符
     * @param isLowerCase
     *            是否使用小写表示形式
     * @return 16进制表示的MAC地址
     * @author xl
     * @since 1.0.0
     */
    public static String macAddressHex(String separator, boolean isLowerCase) {
        if (separator == null || separator.isEmpty()) {
            return isLowerCase ? DatatypeConverter.printHexBinary(macAddress())
                    .toLowerCase()
                    : DatatypeConverter.printHexBinary(macAddress())
                            .toUpperCase();
        } else {
            StringBuilder macHexSB = new StringBuilder(DatatypeConverter.printHexBinary(macAddress()));
            if (macHexSB.length() == 12) {
                for (int i = 0; i < 5; i++) {
                    macHexSB.insert(2 * i + 2 + i, separator);
                }
                String targetMacHex = isLowerCase ? macHexSB.toString()
                        .toLowerCase()
                        : macHexSB.toString()
                                .toUpperCase();
                if (":".equals(separator)) {
                    System.setProperty(OVERRIDE_MAC_HEX_PROP, targetMacHex);
                }
                return targetMacHex;
            } else {
                return macAddressHex(isLowerCase);
            }
        }
    }

    /**
     * 返回当前Java虚拟机运行的进程PID，如果未能获取到，则返回0. 在*nix上的JVM可以正常获取到.
     * 
     * @return 当前Java虚拟机运行的进程PID
     * @throws UnsupportedOperationException
     *             未能正常获取PID
     * @author xl
     * @since 1.0.0
     */
    public static int pid() {
        String overridePid = System.getProperty(OVERRIDE_PID_PROP);
        if (overridePid != null) {
            return Integer.parseInt(overridePid);
        }
        int localPID = 0;
        try {
            String name = ManagementFactory.getRuntimeMXBean()
                    .getName();
            String[] nameSplit = name.split("@");
            if (nameSplit.length > 1) {
                localPID = Integer.parseInt(nameSplit[0]);
                System.setProperty(OVERRIDE_PID_PROP, nameSplit[0]);
            }
            return localPID;
        } catch (Throwable t) {
            throw new UnsupportedOperationException("An error occurred while getting the PID: " + t.getMessage());
        }
    }

    /**
     * 当前系统时间自1970年1月1日0时0分0秒的毫秒数
     *
     * @return 当前系统时间毫秒数
     * @see {@link java.lang.System#currentTimeMillis}
     * @author xl
     * @since 1.0.0
     */
    public static long systemTimeAsMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 当前系统时间自1970年1月1日0时0分0秒的秒数
     *
     * @return 当前系统时间距离1970年1月1日0时0分0秒的秒数
     * @author xl
     * @since 1.0.0
     */
    public static long systemTimeAsSeconds() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * 当前系统时间自1970年1月1日0时0分0秒的分钟数
     *
     * @return 当前系统时间距离1970年1月1日0时0分0秒的分钟数
     * @author xl
     * @since 1.0.0
     */
    public static long systemTimeAsMinutes() {
        return System.currentTimeMillis() / 60000L;
    }

    /**
     * 当前系统时间自1970年1月1日0时0分0秒的小时数
     *
     * @return 当前系统时间距离1970年1月1日0时0分0秒的小时数
     * @author xl
     * @since 1.0.0
     */
    public static long systemTimeAsHours() {
        return System.currentTimeMillis() / 3600000L;
    }

    /**
     * 当前系统时间自1970年1月1日0时0分0秒的天数
     *
     * @return 当前系统时间距离1970年1月1日0时0分0秒的天数
     * @author xl
     * @since 1.0.0
     */
    public static long systemTimeAsDays() {
        return System.currentTimeMillis() / 86400000L;
    }

    /**
     * 当前系统时间对应的日期
     *
     * @return 日期对象
     * @author xl
     * @since 1.0.0
     */
    public static Date systemTimeAsDate() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * 获取当前操作系统类型.
     *
     * @return 操作系统类型的枚举.
     * @author xl
     * @since 1.0.0
     */
    public static OSType currentOSType() {
        return OSType.current();
    }

    /**
     * 判断当前操作系统是否为Windows.
     *
     * @return <code>true</code> 当前系统为<code>Windows</code>; <code>false</code>
     *         当前系统不是<code>Windows</code>
     * @author xl
     * @since 1.0.0
     */
    public static boolean isWindowsOS() {
        return currentOSType() == OSType.WINDOWS;
    }

    /**
     * 判断当前操作系统是否为Unix(包括Linux).
     *
     * @return <code>true</code> 当前系统为<code>Unix(包括Linux)</code>;
     *         <code>false</code> 当前系统不是<code>Unix(包括Linux)</code>
     * @author xl
     * @since 1.0.0
     */
    public static boolean isUnixOS() {
        return currentOSType() == OSType.UNIX;
    }

    /**
     * 判断当前操作系统是否为Mac.
     *
     * @return <code>true</code> 当前系统为<code>Mac</code>; <code>false</code>
     *         当前系统不是<code>Mac</code>
     * @author xl
     * @since 1.0.0
     */
    public static boolean isMacOS() {
        return currentOSType() == OSType.MAC;
    }

    /**
     * 判断当前操作系统是否为Solaris.
     *
     * @return <code>true</code> 当前系统为<code>Solaris</code>; <code>false</code>
     *         当前系统不是<code>Solaris</code>
     * @author xl
     * @since 1.0.0
     */
    public static boolean isSolarisOS() {
        return currentOSType() == OSType.SOLARIS;
    }

    /**
     * 操作系统类型的枚举.
     *
     * @author xl
     * @since 1.0.0
     */
    public static enum OSType {
        /**
         * 未知或不支持.
         *
         * @since 1.0.0
         */
        UNKNOWN("unknown"),
        /**
         * UNIX.
         *
         * @since 1.0.0
         */
        UNIX("nux|nix|aix"),
        /**
         * WINDOWS.
         *
         * @since 1.0.0
         */
        WINDOWS("win"),
        /**
         * MAC.
         *
         * @since 1.0.0
         */
        MAC("mac"),
        /**
         * SOLARIS.
         *
         * @since 1.0.0
         */
        SOLARIS("sunos");
        private String flag;

        OSType(String flag) {
            this.flag = flag;
        }

        /**
         * 获取操作系统类型标识.
         *
         * @return 操作系统类型标识字符串
         * @author xl
         * @since 1.0.0
         */
        public String getFlag() {
            return flag;
        }

        /**
         * 获取当前操作系统类型的枚举.
         *
         * @return 操作系统类型的枚举
         * @author xl
         * @since 1.0.0
         */
        public static OSType current() {
            for (OSType os : values()) {
                String[] items = os.getFlag()
                        .split("\\|");
                for (String item : items) {
                    if (OS.indexOf(item) >= 0) {
                        return os;
                    }
                }
            }
            return OSType.UNKNOWN;
        }
    }
}
