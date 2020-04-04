package org.campus.partner.util.id;

/**
 * 全局唯一ID的统一接口
 *
 * @author xl
 * @since 1.0.0
 */
public interface Id {

    /**
     * 以长整型方式返回全局唯一ID
     *
     * @return 全局唯一ID的长整型表示形式
     * @author xl
     * @since 1.0.0
     */
    long asLong();

    /**
     * 以二进制方式返回全局唯一ID
     *
     * @return 全局唯一ID的二进制表示形式
     * @author xl
     * @since 1.0.0
     */
    byte[] asBytes();

    /**
     * 以字符串方式返回全局唯一ID
     *
     * @return 全局唯一ID的字符串表示形式
     * @author xl
     * @since 1.0.0
     */
    String asString();
}
