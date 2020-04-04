package org.campus.partner.util.map;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 并发支持的允许设置过期时间的Map接口的实现.
 * 
 * @param <K>
 *            '键'类型
 * @param <V>
 *            '值'类型
 * @author xl
 * @since 1.0.1
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class SelfExpiringConcurrentHashMap<K, V> implements SelfExpiringMap<K, V> {
    private final Map<K, V> internalMap;

    private final Map<K, ExpiringKey<K>> expiringKeys;

    /**
     * 使用延迟队列持有设置了过期毫秒数的Map的<code>key</code>.
     */

    private final DelayQueue<ExpiringKey> delayQueue = new DelayQueue<ExpiringKey>();

    /**
     * 默认最大过期毫秒数.
     */
    private final long maxExpireMillis;

    /**
     * 默认构造方法.<br/>
     * 
     * 缺省：过期毫秒数为 {@link java.lang.Long.MAX_VALUE}.
     *
     */
    public SelfExpiringConcurrentHashMap() {
        internalMap = new ConcurrentHashMap<K, V>();
        expiringKeys = new WeakHashMap<K, ExpiringKey<K>>();
        this.maxExpireMillis = Long.MAX_VALUE;
    }

    /**
     * 指定一个默认最大过期毫秒数的构造方法.
     *
     * @param defaultMaxExpireMillis
     *            设置默认最大过期毫秒数
     */
    public SelfExpiringConcurrentHashMap(long defaultMaxExpireMillis) {
        internalMap = new ConcurrentHashMap<K, V>();
        expiringKeys = new WeakHashMap<K, ExpiringKey<K>>();
        this.maxExpireMillis = defaultMaxExpireMillis;
    }

    /**
     * 指定一个默认最大过期毫秒数和默认初始化容量大小的构造方法.
     *
     * @param defaultMaxExpireMillis
     *            设置默认最大过期毫秒数
     * @param initialCapacity
     *            设置默认容量大小
     */
    public SelfExpiringConcurrentHashMap(long defaultMaxExpireMillis, int initialCapacity) {
        internalMap = new ConcurrentHashMap<K, V>(initialCapacity);
        expiringKeys = new WeakHashMap<K, ExpiringKey<K>>(initialCapacity);
        this.maxExpireMillis = defaultMaxExpireMillis;
    }

    /**
     * 指定一个默认最大过期毫秒数、默认初始化容量大小、加载因子的构造方法.
     *
     * @param defaultMaxExpireMillis
     *            设置默认最大过期毫秒数
     * @param initialCapacity
     *            设置默认容量大小
     * @param loadFactor
     *            设置默认加载因子大小
     */
    public SelfExpiringConcurrentHashMap(long defaultMaxExpireMillis, int initialCapacity, float loadFactor) {
        internalMap = new ConcurrentHashMap<K, V>(initialCapacity, loadFactor);
        expiringKeys = new WeakHashMap<K, ExpiringKey<K>>(initialCapacity, loadFactor);
        this.maxExpireMillis = defaultMaxExpireMillis;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        cleanup();
        return internalMap.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        cleanup();
        return internalMap.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        cleanup();
        return internalMap.containsKey((K) key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        cleanup();
        return internalMap.containsValue((V) value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V get(Object key) {
        cleanup();
        renewKey((K) key);
        return internalMap.get((K) key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value) {
        return this.put(key, value, maxExpireMillis);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V put(K key, V value, long expireMillis) {
        cleanup();
        ExpiringKey delayedKey = new ExpiringKey(key, expireMillis);
        ExpiringKey oldKey = expiringKeys.put((K) key, delayedKey);
        if (oldKey != null) {
            expireKey(oldKey);
            expiringKeys.put((K) key, delayedKey);
        }
        delayQueue.offer(delayedKey);
        return internalMap.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V remove(Object key) {
        V removedValue = internalMap.remove((K) key);
        expireKey(expiringKeys.remove((K) key));
        return removedValue;
    }

    /**
     * Not supported.
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean renewKey(K key) {
        ExpiringKey<K> delayedKey = expiringKeys.get((K) key);
        if (delayedKey != null) {
            delayedKey.renew();
            return true;
        }
        return false;
    }

    private void expireKey(ExpiringKey<K> delayedKey) {
        if (delayedKey != null) {
            delayedKey.expire();
            cleanup();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        delayQueue.clear();
        expiringKeys.clear();
        internalMap.clear();
    }

    /**
     * Not supported.
     */
    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException();
    }

    /**
     * Not supported.
     */
    @Override
    public Set<Entry<K, V>> entrySet() {
        throw new UnsupportedOperationException();
    }

    private void cleanup() {
        ExpiringKey<K> delayedKey = delayQueue.poll();
        while (delayedKey != null) {
            internalMap.remove(delayedKey.getKey());
            expiringKeys.remove(delayedKey.getKey());
            delayedKey = delayQueue.poll();
        }
    }

    /**
     * 过期<code>key</code>的延时队列实现.
     * 
     * @param <K>
     *            Map中的<code>key</code>对象
     * @author xl
     * @since 1.0.1
     */
    @SuppressWarnings("hiding")
    private class ExpiringKey<K> implements Delayed {

        private long startTime = System.currentTimeMillis();
        private final long maxExpireMillis;
        private final K key;

        public ExpiringKey(K key, long maxExpireMillis) {
            this.maxExpireMillis = maxExpireMillis;
            this.key = key;
        }

        public K getKey() {
            return key;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ExpiringKey<K> other = (ExpiringKey<K>) obj;
            if (this.key != other.key && (this.key == null || !this.key.equals(other.key))) {
                return false;
            }
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 31 * hash + (this.key != null ? this.key.hashCode() : 0);
            return hash;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(getDelayMillis(), TimeUnit.MILLISECONDS);
        }

        private long getDelayMillis() {
            return (startTime + maxExpireMillis) - System.currentTimeMillis();
        }

        public void renew() {
            startTime = System.currentTimeMillis();
        }

        public void expire() {
            startTime = Long.MIN_VALUE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(Delayed that) {
            long x = this.getDelayMillis();
            long y = ((ExpiringKey) that).getDelayMillis();
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    }
}
