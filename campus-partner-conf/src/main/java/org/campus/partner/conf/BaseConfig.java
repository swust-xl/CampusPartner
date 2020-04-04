package org.campus.partner.conf;

import org.campus.partner.util.id.IdGenerator;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.time.StandardTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * 基础公共配置.
 * 
 * @author xl
 * @since 1.0.0
 */
@Configuration
public class BaseConfig {
    public static final Logger LOG = LoggerFactory.getLogger(BaseConfig.class);

    /**
     * 自定义Jackson转Object对象配置.
     * 
     * @return 返回配置好的Jackson
     * @author zhd
     * @since 1.0.0
     */
    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "使用自定义Jackson转Object对象配置"), JsonConverter.class);
        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.configure(JsonConverter.getObjectMapper());
        return b;
    }

    /**
     * 自定义Jackson转Http消息配置.
     * 
     * @return 返回配置好的Jackson
     * @author xl
     * @since 1.0.0
     */
    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "使用自定义Jackson转Http消息配置"), JsonConverter.class);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(JsonConverter.getObjectMapper());
        return jsonConverter;
    }

    /**
     * 
     * id生成器.
     *
     * @return wsid生成器
     * @author xl
     * @since 1.0.0
     */
    @Bean
    public IdGenerator wsidGenerator() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "注入全局ID生成器"), IdGenerator.class);
        return new IdGenerator();
    }

    /**
     * 
     * 标准时间生成器.
     *
     * @return 标准时间生成器
     * @author xl
     * @since 1.0.0
     */
    @Bean
    public StandardTimes standardTimes() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "注入标准时间生成器"), StandardTimes.class);
        return new StandardTimes();
    }
}
