package org.campus.partner.conf.filter;

import org.campus.partner.conf.BaseConfig;
import org.campus.partner.conf.LogTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * HTTP命名风格过滤器配置.
 * 
 * @author xl
 * @since 1.2.3
 */
@Configuration
@Import(BaseConfig.class)
// spring boot 1.5.x，多个@ServletComponentScan时，如果不指定basePackages 或
// basePackageClass会有java.lang.UnsupportedOperationException:
// null的bug，参见：https://github.com/spring-projects/spring-boot/pull/12715
@ServletComponentScan(basePackageClasses = HttpNamingStyleFilter.class)
public class HttpNamingStyleFilterConfig {
    public static final Logger LOG = LoggerFactory.getLogger(HttpNamingStyleFilterConfig.class);
    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    public HttpNamingStyleFilterConfig() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "启用HTTP命名风格过滤器配置"), HttpNamingStyleFilterConfig.class);
    }

    /**
     * 注册HTTP请求的的命名风格的过滤器.
     *
     * @return 过滤器注册对象
     * @author xl
     * @since 1.2.3
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpNamingStyleFilter(mappingJackson2HttpMessageConverter));
        registration.addUrlPatterns("/*");
        registration.setName("httpNamingStyleFilter");
        registration.setOrder(998);
        return registration;
    }
}
