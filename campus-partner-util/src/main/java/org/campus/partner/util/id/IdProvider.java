package org.campus.partner.util.id;

/**
 * ID产生的提供者
 * <p/>
 * 用于产生以时间戳(Timestamp)和序列号(SequenceNumber)为基础的ID生成接口
 *
 * @author xl
 * @since 1.0.0
 */
public interface IdProvider {

    /**
     * 以原始二进制数据来表示时间戳和序列号为基础生成的ID
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 二进制表示的ID原始数据
     * @author xl
     * @since 1.0.0
     */
    byte[] encodeAsBytes(long time, int sequence);

    /**
     * 以长整形数据来表示时间戳和序列号为基础生成的ID
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 长整形表示ID数据
     * @author xl
     * @since 1.0.0
     */
    long encodeAsLong(long time, int sequence);

    /**
     * 以字符串数据来表示时间戳和序列号为基础生成的ID
     *
     * @param time
     *            时间戳
     * @param sequence
     *            序列号
     * @return 字符串表示ID数据（通常采用16进制编码的字符串表示）
     * @author xl
     * @since 1.0.0
     */
    String encodeAsString(long time, int sequence);

    /**
     * 获取允许传入的最大序列号值
     *
     * @return 允许的最大序列号值
     * @author xl
     * @since 1.0.0
     */
    int maxSequence();
}
