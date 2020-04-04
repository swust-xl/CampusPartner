package org.campus.partner.conf.redis;

import java.util.concurrent.TimeUnit;

import org.campus.partner.conf.redis.RedisConfig.CustomRedisProperties;
import org.campus.partner.util.type.BeanUtils;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheElement;
import org.springframework.data.redis.cache.RedisCacheKey;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 
 * 自定义redisCache.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public class CustomRedisCache extends RedisCache {

    private static RedisTemplate<String, Object> template;

    private static CustomRedisProperties cacheProperties;

    static {
        cacheProperties = BeanUtils.getBean(CustomRedisProperties.class);
        template = BeanUtils.getBean(RedisTemplate.class, "cacheRedisTemplate");
    }

    public CustomRedisCache(String name, byte[] prefix,
            RedisOperations<? extends Object, ? extends Object> redisOperations, long expiration,
            boolean allowNullValues) {
        super(name, prefix, redisOperations, expiration, allowNullValues);
    }

    /**
     * 
     * 自定义RedisCache的get方法复写(分为一步，直接get而不是源码中的先判断在不在又get，会在并发情况下，get的时候缓存已经被清除的情况发生).
     *
     * @param cacheKey
     *            redisKey
     * @return RedisCacheElement
     * @author xl
     * @since 1.0.0
     */
    @Override
    public RedisCacheElement get(RedisCacheKey cacheKey) {
        RedisCacheElement elemnt = new RedisCacheElement(cacheKey, fromStoreValue(lookup(cacheKey)));
        if (elemnt.get() == null) {
            return null;
        }
        template.expire(CustomRedisCacheManager.generateCacheNamePrefix(getName()) + cacheKey.getKeyElement(),
                cacheProperties.getExpiretime(), TimeUnit.SECONDS);
        return elemnt;
    }

}
