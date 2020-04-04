package org.campus.partner.util.map;

import java.util.Map;

/**
 * 允许设置过期时间的Map接口.
 * 
 * @param <K>
 *            '键'类型
 * @param <V>
 *            '值'类型
 * @author xl
 * @since 1.0.1
 */
public interface SelfExpiringMap<K, V> extends Map<K, V> {
    /**
     * 更新指定的<code>key</code>，使其对应的<code>value</code>的有效期置为初始值.
     *
     * @param key
     *            待更新的<code>key</code>
     * @return true - 指定更新的<code>key</code>找到; false - 指定更新的<code>key</code>未找到
     */
    public boolean renewKey(K key);

    /**
     * 放入一个与<code>key</code>关联的<code>value</code>,并设置有效过期的毫秒数 .
     * 
     * @param key
     *            待放入的'键'
     * @param value
     *            待放入的'值'
     * @param expireMillis
     *            过期的毫秒数
     * @return 之前已经放入的<code>key</code>对应的<code>value</code>（如果存在的话）
     */
    public V put(K key, V value, long expireMillis);
}
