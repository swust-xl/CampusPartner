package org.campus.partner.conf.redis;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisOperations;

/**
 * 
 * 自定义redisCacheManager.
 * </p>
 *
 * @author xl
 * @since 1.2.7
 */
public class CustomRedisCacheManager extends RedisCacheManager {
    public CustomRedisCacheManager(RedisOperations<?, ?> redisOperations) {
        super(redisOperations);
    }

    /**
     * 
     * 使其生成自定义的RedisCache.
     *
     * @param cacheName
     *            缓存目录
     * @return RedisCache
     * @author xl
     * @since 1.2.7
     */
    @SuppressWarnings("unchecked")
    @Override
    protected RedisCache createCache(String cacheName) {
        long expiration = computeExpiration(cacheName);
        return new CustomRedisCache(cacheName, generateCacheNamePrefix(cacheName).getBytes(), getRedisOperations(),
                expiration, false);
    }

    /**
     * 根据cacheName生成前缀. </br>
     * 
     * 
     * @param cacheName
     * @return 带cacheName的前缀
     * @author zhangbo
     * @since 1.0.0
     */
    public static String generateCacheNamePrefix(String cacheName) {
        return cacheName + ":";
    }

}