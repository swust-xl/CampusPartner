package org.campus.partner.util.id;

import static java.lang.Long.toHexString;

import java.nio.ByteBuffer;

import org.campus.partner.util.SystemUtil;

/**
 * 基于Boundary flake（Snowflake的一个变种）实现的分布式唯一ID的生成, 这样做的目的是用更多的 bits 实现更小的冲突概率,
 * 这样就支持更多的 Worker 同时工作, 同时, 每毫秒能分配出更多的 ID.
 * 
 * <pre>
 *  Boundary flake结构如下（每部分用-分隔）：
 *  <code>
 *  |1|<---------------------------63bits--------------------------->|<---------------------48bits------------------->|<----16bits--->|
 *  0-000000000000000000000000000000000000000000000000000000000000000-000000000000000000000000000000000000000000000000-0000000000000000
 *  </code>
 *
 *  1位符号位：由于long类型在java中带符号的，最高位为符号位，正数为0，负数为1，且实际系统中所使用的ID一般都是正数，所以最高位为0
 *  
 *  63位时间戳（毫秒级）：需要注意的是此处的63位时间戳采用当前时间的时间戳，所以63位毫秒时间戳最多可以使用 (1 << 63) / (1000x60x60x24x365) = 292471208.6775年
 *                     
 *  48位数据机器位：这48位决定了分布式系统中最多可以部署1 << 48 = 281474976710656个节点，超过这个数量，
 *                生成的ID就有可能会冲突,另外，机器位和Mac地址一样长，这样启动时不需要和 Zookeeper 通讯获取 Worker ID, 做到了完全的去中心化
 *                
 *  16位毫秒内的序列号：这16位计数支持每个节点每毫秒（同一台机器，同一时刻）最多生成1 << 16 = 65536个ID
 *  
 *  4部分加起来总共128位.
 *  
 *  注：此时刚好2个Long型的表示范围，单个Long型来表示ID的方式将失效.
 * </pre>
 *
 * @see <a href=
 *      "https://github.com/boundary/flake">https://github.com/boundary/flake</a>
 * @author xl
 * @since 1.0.0
 */
public class FlakeIdProvider implements IdProvider {

    /**
     * 格式化后的机器号.
     */
    private long shiftedMachineId;

    /**
     * 使用默认机器号构建flake分布式唯一ID的生成的提供者
     *
     */
    public FlakeIdProvider() {
        this(SystemUtil.macAddressLong());
    }

    /**
     * 指定机器号构建flake分布式唯一ID的生成的提供者
     *
     * @param machineId
     *            机器号
     */
    public FlakeIdProvider(long machineId) {
        shiftedMachineId = (0x0000FFFFFFFFFFFFL & machineId) << 16;
    }

    /**
     * 以原始二进制数据来表示时间戳和序列号为基础生成的ID
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 二进制表示的ID原始数据
     * @see cn.signit.wesign.lib.common.id.IdProvider#encodeAsBytes(long, int)
     * @author xl
     * @since 1.0.0
     */
    @Override
    public byte[] encodeAsBytes(long time, int sequence) {
        byte[] buffer = new byte[16];
        buffer[0] = (byte) (time >>> 56);
        buffer[1] = (byte) (time >>> 48);
        buffer[2] = (byte) (time >>> 40);
        buffer[3] = (byte) (time >>> 32);
        buffer[4] = (byte) (time >>> 24);
        buffer[5] = (byte) (time >>> 16);
        buffer[6] = (byte) (time >>> 8);
        buffer[7] = (byte) time;

        long rest = shiftedMachineId | (0x0000FFFF & sequence);
        buffer[8] = (byte) (rest >>> 56);
        buffer[9] = (byte) (rest >>> 48);
        buffer[10] = (byte) (rest >>> 40);
        buffer[11] = (byte) (rest >>> 32);
        buffer[12] = (byte) (rest >>> 24);
        buffer[13] = (byte) (rest >>> 16);
        buffer[14] = (byte) (rest >>> 8);
        buffer[15] = (byte) rest;

        return buffer;
    }

    /**
     * 已超出Long型的表示范围4bytes，所以该方法无法实现
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 无法表示该ID将抛出异常
     * @throws UnsupportedOperationException
     *             已超出Long型的表示范围
     * @see cn.signit.wesign.lib.common.id.IdProvider#encodeAsLong(long, int)
     * @author xl
     * @since 1.0.0
     */
    @Override
    public long encodeAsLong(long time, int sequence) {
        throw new UnsupportedOperationException("Long value not supported");
    }

    /**
     * 以字符串数据来表示时间戳和序列号为基础生成的ID
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 字符串表示ID数据（通常采用16进制编码的字符串表示）
     * @see cn.signit.wesign.lib.common.id.IdProvider#encodeAsString(long, int)
     * @author xl
     * @since 1.0.0
     */
    @Override
    public String encodeAsString(long time, int sequence) {
        StringBuilder s = new StringBuilder(32);
        ByteBuffer bb = ByteBuffer.wrap(encodeAsBytes(time, sequence));
        // 若位数不够，高位用0填充
        s.append(StringUtil.leftPad(toHexString(bb.getLong()), 16, '0'));
        s.append(StringUtil.leftPad(toHexString(bb.getLong()), 16, '0'));
        return s.toString();
    }

    /**
     * 获取允许传入的最大序列号值
     *
     * @return 允许的最大序列号值
     * @see cn.signit.wesign.lib.common.id.IdProvider#maxSequence()
     * @author xl
     * @since 1.0.0
     */
    @Override
    public int maxSequence() {
        // 2^16
        return 65536;
    }

    /**
     * 字符串处理工具
     * <p/>
     * 代码片段部分拷贝了Apache commons-lang 2.6的实现
     *
     * @see <a href=
     *      "http://commons.apache.org/proper/commons-lang/javadocs/api-2.6/src-html/org/apache/commons/lang/StringUtils.html#line.4891">http://commons.apache.org/proper/commons-lang/javadocs/api-2.6/src-html/org/apache/commons/lang/StringUtils.html#line.4891</a>
     * @author xl
     * @since 1.0.0
     */
    public static class StringUtil {
        /**
         * <p/>
         * The maximum size to which the padding constant(s) can expand.
         * <p/>
         */
        private static final int PAD_LIMIT = 8192;

        /**
         * <p/>
         * Checks if a String is empty ("") or null.
         * <p/>
         *
         * <pre>
         * StringUtils.isEmpty(null)      = true
         * StringUtils.isEmpty("")        = true
         * StringUtils.isEmpty(" ")       = false
         * StringUtils.isEmpty("bob")     = false
         * StringUtils.isEmpty("  bob  ") = false
         * </pre>
         *
         * <p/>
         * NOTE: This method changed in Lang version 2.0. It no longer trims the
         * String. That functionality is available in isBlank().
         * <p/>
         *
         * @param str
         *            the String to check, may be null
         * @return <code>true</code> if the String is empty or null
         */
        public static boolean isEmpty(String str) {
            return str == null || str.length() == 0;
        }

        /**
         * <p/>
         * Left pad a String with spaces (' ').
         * <p/>
         *
         * <p/>
         * The String is padded to the size of <code>size</code>.
         * <p/>
         *
         * <pre>
         * StringUtils.leftPad(null, *)   = null
         * StringUtils.leftPad("", 3)     = "   "
         * StringUtils.leftPad("bat", 3)  = "bat"
         * StringUtils.leftPad("bat", 5)  = "  bat"
         * StringUtils.leftPad("bat", 1)  = "bat"
         * StringUtils.leftPad("bat", -1) = "bat"
         * </pre>
         *
         * @param str
         *            the String to pad out, may be null
         * @param size
         *            the size to pad to
         * @return left padded String or original String if no padding is
         *         necessary, <code>null</code> if null String input
         */
        public static String leftPad(String str, int size) {
            return leftPad(str, size, ' ');
        }

        /**
         * <p/>
         * Left pad a String with a specified character.
         * <p/>
         *
         * <p/>
         * Pad to a size of <code>size</code>.
         * <p/>
         *
         * <pre>
         * StringUtils.leftPad(null, *, *)     = null
         * StringUtils.leftPad("", 3, 'z')     = "zzz"
         * StringUtils.leftPad("bat", 3, 'z')  = "bat"
         * StringUtils.leftPad("bat", 5, 'z')  = "zzbat"
         * StringUtils.leftPad("bat", 1, 'z')  = "bat"
         * StringUtils.leftPad("bat", -1, 'z') = "bat"
         * </pre>
         *
         * @param str
         *            the String to pad out, may be null
         * @param size
         *            the size to pad to
         * @param padChar
         *            the character to pad with
         * @return left padded String or original String if no padding is
         *         necessary, <code>null</code> if null String input
         * @since 2.0
         */
        public static String leftPad(String str, int size, char padChar) {
            if (str == null) {
                return null;
            }
            int pads = size - str.length();
            if (pads <= 0) {
                return str; // returns original String when possible
            }
            if (pads > PAD_LIMIT) {
                return leftPad(str, size, String.valueOf(padChar));
            }
            return padding(pads, padChar).concat(str);
        }

        /**
         * <p/>
         * Left pad a String with a specified String.
         * <p/>
         *
         * <p/>
         * Pad to a size of <code>size</code>.
         * <p/>
         *
         * <pre>
         * StringUtils.leftPad(null, *, *)      = null
         * StringUtils.leftPad("", 3, "z")      = "zzz"
         * StringUtils.leftPad("bat", 3, "yz")  = "bat"
         * StringUtils.leftPad("bat", 5, "yz")  = "yzbat"
         * StringUtils.leftPad("bat", 8, "yz")  = "yzyzybat"
         * StringUtils.leftPad("bat", 1, "yz")  = "bat"
         * StringUtils.leftPad("bat", -1, "yz") = "bat"
         * StringUtils.leftPad("bat", 5, null)  = "  bat"
         * StringUtils.leftPad("bat", 5, "")    = "  bat"
         * </pre>
         *
         * @param str
         *            the String to pad out, may be null
         * @param size
         *            the size to pad to
         * @param padStr
         *            the String to pad with, null or empty treated as single
         *            space
         * @return left padded String or original String if no padding is
         *         necessary, <code>null</code> if null String input
         */
        public static String leftPad(String str, int size, String padStr) {
            if (str == null) {
                return null;
            }
            String newPadstr = padStr;
            if (isEmpty(newPadstr)) {
                newPadstr = " ";
            }
            int padLen = newPadstr.length();
            int strLen = str.length();
            int pads = size - strLen;
            if (pads <= 0) {
                return str; // returns original String when possible
            }
            if (padLen == 1 && pads <= PAD_LIMIT) {
                return leftPad(str, size, newPadstr.charAt(0));
            }

            if (pads == padLen) {
                return newPadstr.concat(str);
            } else if (pads < padLen) {
                return newPadstr.substring(0, pads)
                        .concat(str);
            } else {
                char[] padding = new char[pads];
                char[] padChars = newPadstr.toCharArray();
                for (int i = 0; i < pads; i++) {
                    padding[i] = padChars[i % padLen];
                }
                return new String(padding).concat(str);
            }
        }

        /**
         * <p/>
         * Returns padding using the specified delimiter repeated to a given
         * length.
         * <p/>
         *
         * <pre>
         * StringUtils.padding(0, 'e')  = ""
         * StringUtils.padding(3, 'e')  = "eee"
         * StringUtils.padding(-2, 'e') = IndexOutOfBoundsException
         * </pre>
         *
         * <p/>
         * Note: this method doesn't not support padding with <a href=
         * "http://www.unicode.org/glossary/#supplementary_character">Unicode
         * Supplementary Characters</a> as they require a pair of
         * <code>char</code>s to be represented. If you are needing to support
         * full I18N of your applications consider using
         * {@link #repeat(String, int)} instead.
         * <p/>
         *
         * @param repeat
         *            number of times to repeat delim
         * @param padChar
         *            character to repeat
         * @return String with repeated character
         * @throws IndexOutOfBoundsException
         *             if <code>repeat &lt; 0</code>
         * @see #repeat(String, int)
         */
        public static String padding(int repeat, char padChar) throws IndexOutOfBoundsException {
            if (repeat < 0) {
                throw new IndexOutOfBoundsException("Cannot pad a negative amount: " + repeat);
            }
            final char[] buf = new char[repeat];
            for (int i = 0; i < buf.length; i++) {
                buf[i] = padChar;
            }
            return new String(buf);
        }
    }
}