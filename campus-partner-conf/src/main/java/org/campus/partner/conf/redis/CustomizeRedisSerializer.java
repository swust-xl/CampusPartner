package org.campus.partner.conf.redis;

import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 可以处理"{}"的反序列化自定义类.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public class CustomizeRedisSerializer extends GenericJackson2JsonRedisSerializer {

    public CustomizeRedisSerializer(ObjectMapper mapper) {
        super(mapper);
    }

    /**
     * 
     * 如果是"{}"直接返回null.
     *
     * @param source
     *            要被反序列化的json数据
     * @param type
     *            被反序列化成的java类型
     * @param <T>
     *            任何java类型
     * @return java类
     * @throws SerializationException
     *             序列化错误
     * @see org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer#deserialize(byte[],
     *      java.lang.Class).
     * @author xl
     * @since 1.0.0
     */
    @Override
    public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
        if (source == null || "{}".equals(new String(source))) {
            return null;
        }
        return super.deserialize(source, type);
    }
}
