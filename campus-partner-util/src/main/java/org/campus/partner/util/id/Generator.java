package org.campus.partner.util.id;

/**
 * 全局唯一ID生成接口
 * <p/>
 *
 * @author xl
 * @since 1.0.0
 */
public interface Generator {
    public static final int DEFAULT_WAIT_MILLIS = 10;

    /**
     * 生成全局唯一ID
     *
     * @param maxWaitMillis
     *            生成ID需要等待的最大毫秒数
     * @return 生成的全局ID对象
     * @author xl
     * @since 1.0.0
     */
    Id id(int maxWaitMillis);
}
