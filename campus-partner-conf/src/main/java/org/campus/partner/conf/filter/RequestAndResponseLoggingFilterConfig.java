package org.campus.partner.conf.filter;

import org.campus.partner.conf.LogTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * HTTP请求和响应日志输出过滤器配置.
 * 
 * @author xl
 * @since 1.3.5
 */
@Configuration
// spring boot 1.5.x，多个@ServletComponentScan时，如果不指定basePackages 或
// basePackageClass会有java.lang.UnsupportedOperationException:
// null的bug，参见：https://github.com/spring-projects/spring-boot/pull/12715
@ServletComponentScan(basePackageClasses = RequestAndResponseLoggingFilter.class)
public class RequestAndResponseLoggingFilterConfig {
    public static final Logger LOG = LoggerFactory.getLogger(RequestAndResponseLoggingFilterConfig.class);
    @Autowired
    private RequestAndResponseLoggingFilterProperties filterProperties;

    public RequestAndResponseLoggingFilterConfig() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "启用HTTP请求和响应日志输出过滤器配置"),
                RequestAndResponseLoggingFilterConfig.class);
    }

    /**
     * 注册HTTP请求和响应日志输出过滤器配置的过滤器.
     *
     * @return 过滤器注册对象
     * @author xl
     * @since 1.3.5
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new RequestAndResponseLoggingFilter(filterProperties.isDisableRequestLogging(),
                filterProperties.isDisableResponseLogging(), filterProperties.isDisableMultipartContentLogging(),
                filterProperties.getMaxRequestContentLogging(), filterProperties.getMaxResponseContentLogging()));
        registration.addUrlPatterns("/*");
        registration.setName("requestAndResponseLoggingFilter");
        registration.setOrder(999);
        return registration;
    }

    /**
     * 
     * 自定HTTP请求和响应日志输出过滤器配置.
     * </p>
     *
     * @author xl
     * @since 1.3.5
     */
    @Component
    @ConfigurationProperties(prefix = "org.campus.partner.servlet.filter.request-and-response-logging-filter")
    @RefreshScope
    public class RequestAndResponseLoggingFilterProperties {
        /**
         * 是否禁用请求日志输出.<br/>
         * 默认：false
         * 
         * @since 1.3.5
         */
        private boolean disableRequestLogging;
        /**
         * 是否禁用响应日志输出.<br/>
         * 默认：false
         * 
         * @since 1.3.5
         */
        private boolean disableResponseLogging;
        /**
         * 是否禁用Multipart日志输出.<br/>
         * 默认：true
         *
         * @since 1.3.5
         */
        private boolean disableMultipartContentLogging = true;
        /**
         * 最大允许请求数据内容日志输出，单位：字节.<br/>
         * 默认：500KB
         *
         * @since 1.3.5
         */
        private int maxRequestContentLogging = 500 * 1024;
        /**
         * 最大允许响应数据内容日志输出，单位：字节.<br/>
         * 默认：1024KB=1MB
         *
         * @since 1.3.5
         */
        private int maxResponseContentLogging = 1024 * 1024;

        /**
         * 获取disableRequestLogging属性字段的值.
         *
         * @return 类型为boolean的disableRequestLogging属性字段的值.
         */
        public boolean isDisableRequestLogging() {
            return disableRequestLogging;
        }

        /**
         * 设置disableRequestLogging属性字段的值.
         *
         * @param disableRequestLogging
         *            待设置类型为boolean的disableRequestLogging属性字段的值.
         */
        public void setDisableRequestLogging(boolean disableRequestLogging) {
            this.disableRequestLogging = disableRequestLogging;
        }

        /**
         * 获取disableResponseLogging属性字段的值.
         *
         * @return 类型为boolean的disableResponseLogging属性字段的值.
         */
        public boolean isDisableResponseLogging() {
            return disableResponseLogging;
        }

        /**
         * 设置disableResponseLogging属性字段的值.
         *
         * @param disableResponseLogging
         *            待设置类型为boolean的disableResponseLogging属性字段的值.
         */
        public void setDisableResponseLogging(boolean disableResponseLogging) {
            this.disableResponseLogging = disableResponseLogging;
        }

        /**
         * 获取disableMultipartContentLogging属性字段的值.
         *
         * @return 类型为boolean的disableMultipartContentLogging属性字段的值.
         */
        public boolean isDisableMultipartContentLogging() {
            return disableMultipartContentLogging;
        }

        /**
         * 设置disableMultipartContentLogging属性字段的值.
         *
         * @param disableMultipartContentLogging
         *            待设置类型为boolean的disableMultipartContentLogging属性字段的值.
         */
        public void setDisableMultipartContentLogging(boolean disableMultipartContentLogging) {
            this.disableMultipartContentLogging = disableMultipartContentLogging;
        }

        /**
         * 获取maxRequestContentLogging属性字段的值.
         *
         * @return 类型为int的maxRequestContentLogging属性字段的值.
         */
        public int getMaxRequestContentLogging() {
            return maxRequestContentLogging;
        }

        /**
         * 设置maxRequestContentLogging属性字段的值.
         *
         * @param maxRequestContentLogging
         *            待设置类型为int的maxRequestContentLogging属性字段的值.
         */
        public void setMaxRequestContentLogging(int maxRequestContentLogging) {
            this.maxRequestContentLogging = maxRequestContentLogging;
        }

        /**
         * 获取maxResponseContentLogging属性字段的值.
         *
         * @return 类型为int的maxResponseContentLogging属性字段的值.
         */
        public int getMaxResponseContentLogging() {
            return maxResponseContentLogging;
        }

        /**
         * 设置maxResponseContentLogging属性字段的值.
         *
         * @param maxResponseContentLogging
         *            待设置类型为int的maxResponseContentLogging属性字段的值.
         */
        public void setMaxResponseContentLogging(int maxResponseContentLogging) {
            this.maxResponseContentLogging = maxResponseContentLogging;
        }
    }
}
