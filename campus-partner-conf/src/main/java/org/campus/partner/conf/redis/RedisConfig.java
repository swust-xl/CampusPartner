package org.campus.partner.conf.redis;

import java.util.List;

import org.campus.partner.conf.redis.RedisConfig.CustomRedisProperties;
import org.campus.partner.conf.BaseConfig;
import org.campus.partner.conf.LogTemplate;
import org.campus.partner.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scripting.support.ResourceScriptSource;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * redis非关系型数据库配置类.
 * </p>
 * 
 * @author xl
 * @since 1.0.0
 */
@Configuration
@Import(BaseConfig.class)
@ComponentScan("org.campus.partner.**.redis*")
@EnableConfigurationProperties(CustomRedisProperties.class)
public class RedisConfig extends CachingConfigurerSupport {
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

    @Lazy
    @Autowired
    private RedisProperties properties;

    @Lazy
    @Autowired
    private CustomRedisProperties cacheProperties;

    /**
     * 初始化redis非关系型数据库配置类.
     *
     */
    RedisConfig() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "启用Redis数据库配置"), RedisConfig.class);
    }

    /**
     * 配置redis连接工厂.
     *
     * @return 配置完成的连接工厂对象
     * @author xl
     * @since 1.0.0
     */
    @Bean
    @Primary
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setDatabase(properties.getDatabase());
        connectionFactory.setHostName(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setTimeout(properties.getTimeout());
        if (!Validator.isEmpty(properties.getPassword())) {
            connectionFactory.setPassword(properties.getPassword());
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        Pool props = this.properties.getPool();
        poolConfig.setMaxTotal(props.getMaxActive());
        poolConfig.setMaxIdle(props.getMaxIdle());
        poolConfig.setMinIdle(props.getMinIdle());
        poolConfig.setMaxWaitMillis((long) props.getMaxWait());
        connectionFactory.setPoolConfig(poolConfig);
        return connectionFactory;
    }

    /**
     * 
     * 配置RedisTemplate.
     *
     * @return 返回配置完成的RedisTemplate
     * @author xl
     * @since 1.0.0
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return redisTemplate;
    }

    /**
     * 
     * 配置原始JSON处理的RedisTemplate.
     *
     * @return 返回配置完成的RedisTemplate
     * @author xl
     * @since 1.3.1
     */
    @Bean
    public RedisTemplate<String, String> rawJsonStringRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * 配置缓存redis的manager.
     *
     * @return 配置缓存redis的缓存manager
     * @author xl
     * @since 1.0.0
     */
    @Bean
    @Override
    public CacheManager cacheManager() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.setSerializationInclusion(Include.NON_NULL);
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        om.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        CustomizeRedisSerializer jackson2json = new CustomizeRedisSerializer(om);
        RedisTemplate<String, Object> template = cacheRedisTemplate();
        template.setValueSerializer(jackson2json);
        template.setHashValueSerializer(jackson2json);
        template.afterPropertiesSet();
        CustomRedisCacheManager redisCacheManager = new CustomRedisCacheManager(template);
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "配置以Redis做缓存的管理类"), RedisCacheManager.class);
        redisCacheManager.setLoadRemoteCachesOnStartup(true);
        redisCacheManager.setUsePrefix(true);
        redisCacheManager.setDefaultExpiration(cacheProperties.getExpiretime());
        return redisCacheManager;
    }

    /**
     * 
     * 生成缓存专用的连接工厂Bean,此连接是指向缓存数据库的.
     *
     * @return JedisConnectionFactory实例
     * @author xl
     * @since 1.0.0
     */
    @Bean("cacheUseonlyJedisConnectionFactory")
    public JedisConnectionFactory cacheRedisConnectionFactory() {
        // JedisPoolConfig poolConfig = this.properties.getPool() != null ?
        // jedisPoolConfig() : new JedisPoolConfig();
        // JedisConnectionFactory connectionFactory = new
        // JedisConnectionFactory(poolConfig);
        // configureConnection(connectionFactory);
        // connectionFactory.setDatabase(cacheProperties.getCachedatabase());
        // connectionFactory.setTimeout(this.properties.getTimeout());
        // return connectionFactory;

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
        connectionFactory.setDatabase(cacheProperties.getCachedatabase());
        connectionFactory.setHostName(properties.getHost());
        connectionFactory.setPort(properties.getPort());
        connectionFactory.setTimeout(properties.getTimeout());
        if (!Validator.isEmpty(properties.getPassword())) {
            connectionFactory.setPassword(properties.getPassword());
        }
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        Pool props = this.properties.getPool();
        poolConfig.setMaxTotal(props.getMaxActive());
        poolConfig.setMaxIdle(props.getMaxIdle());
        poolConfig.setMinIdle(props.getMinIdle());
        poolConfig.setMaxWaitMillis((long) props.getMaxWait());
        connectionFactory.setPoolConfig(poolConfig);
        return connectionFactory;
    }

    /**
     * 
     * 生成缓存专用的redistemplate Bean。
     *
     * @return 缓存专用的redistemplate Bean.
     * @author xl
     * @since 1.0.0
     */
    @Bean("cacheRedisTemplate")
    public RedisTemplate<String, Object> cacheRedisTemplate() {
        // RedisTemplate<String, Object> template = new
        // RedisTemplate<String, Object>();
        // // template.setKeySerializer(new StringRedisSerializer());
        // RedisSerializer<String> stringSerializer = new
        // StringRedisSerializer();
        // template.setKeySerializer(stringSerializer);
        // // template.setValueSerializer(stringSerializer);
        // template.setHashKeySerializer(stringSerializer);
        // // template.setHashValueSerializer(stringSerializer);
        // template.setConnectionFactory(cacheRedisConnectionFactory());
        // // template.setHashKeySerializer(hashKeySerializer);
        // return template;
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,
        // "配置redis数据操作模板类"), RedisTemplate.class);
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,"配置redis数据库连接"));
        redisTemplate.setConnectionFactory(cacheRedisConnectionFactory());
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,"配置以json方式实现对象value的序列化"));
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,"配置以String方式实现key的序列化"));
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,"配置hash类型以String方式实现key的序列化"));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        // LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE,"配置hash类型以json方式实现对象value的序列化"));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return redisTemplate;
    }

    /**
     * 
     * 用redis scan方式查询所有key的脚本.
     *
     * @return 脚本
     * @author xl
     * @since 1.2.7
     */
    @Bean
    @SuppressWarnings("rawtypes")
    public RedisScript<List> script() {
        DefaultRedisScript<List> redisScript = new DefaultRedisScript<List>();
        redisScript
                .setScriptSource(new ResourceScriptSource(new ClassPathResource("META-INF/scripts/patternKeys.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    /**
     * 
     * 自定义缓存库配置文件.
     * </p>
     *
     * @author xl
     * @since 1.0.0
     */
    @ConfigurationProperties(prefix = "spring.redis")
    public class CustomRedisProperties {
        private int cachedatabase = 1;
        private long expiretime = 300L;
        private String caffeinespec;
        private boolean hashed = true;

        public int getCachedatabase() {
            return cachedatabase;
        }

        public void setCachedatabase(int cachedatabase) {
            this.cachedatabase = cachedatabase;
        }

        public long getExpiretime() {
            return expiretime;
        }

        // 过期事件，单位为秒
        public void setExpiretime(long expiretime) {
            this.expiretime = expiretime;
        }

        public String getCaffeinespec() {
            return caffeinespec;
        }

        public void setCaffeinespec(String caffeinespec) {
            this.caffeinespec = caffeinespec;
        }

        public boolean isHashed() {
            return hashed;
        }

        public void setHashed(boolean hashed) {
            this.hashed = hashed;
        }

    }
}
