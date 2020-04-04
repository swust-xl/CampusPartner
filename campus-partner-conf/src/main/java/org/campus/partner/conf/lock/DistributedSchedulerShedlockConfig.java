package org.campus.partner.conf.lock;

import java.time.Duration;

import org.campus.partner.conf.LogTemplate;
import org.campus.partner.conf.redis.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import net.javacrumbs.shedlock.spring.ScheduledLockConfiguration;
import net.javacrumbs.shedlock.spring.ScheduledLockConfigurationBuilder;

/**
 * 
 * 基于ShedLock的定时任务分布式锁配置
 * </p>
 * 
 * @author xuLiang
 * @since 1.3.2
 */
@Import({ RedisConfig.class })
@EnableScheduling
@Configuration
public class DistributedSchedulerShedlockConfig {

    public static final Logger LOG = LoggerFactory.getLogger(DistributedSchedulerShedlockConfig.class);
    public static final String DEFAULT_SCHEDULE_CRON = "0 0/5 * * * ?"; // 每隔5分钟检查一次
    @Autowired
    private DistributedSchedulerShedlockProperties shedlockProperties;

    public DistributedSchedulerShedlockConfig() {
        LOG.info(String.format(LogTemplate.CONFIG_TEMPLATE, "启用基于ShedLock的定时任务分布式锁配置"),
                DistributedSchedulerShedlockConfig.class);
    }

    /**
     * 
     * 基于Redis方式提供分布式锁
     *
     * @param connectionFactory
     *            链接工厂
     * @return 分布式锁提供者对象
     * @author xuLiang
     * @since 1.3.2
     */
    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory) {
        return new RedisLockProvider(connectionFactory);
    }

    /**
     * 
     * 分布式定时锁配置
     *
     * @param lockProvider
     *            分布式锁提供者对象
     * @return 配置完成的锁
     * @author xuLiang
     * @since 1.3.2
     */
    @Bean
    public ScheduledLockConfiguration taskScheduler(LockProvider lockProvider) {
        return ScheduledLockConfigurationBuilder.withLockProvider(lockProvider)
                .withPoolSize(shedlockProperties.getPoolSize() <= 0 ? 5 : shedlockProperties.getPoolSize())
                .withDefaultLockAtMostFor(
                        Duration.ofMillis(shedlockProperties.getDefaultLockAtMostForMillis() <= 0 ? 10 * 60 * 1000
                                : shedlockProperties.getDefaultLockAtMostForMillis()))
                .build();
    }

    /**
     * ShedLock的定时任务分布式锁配置属性.
     * 
     * @author xl
     * @since 1.3.4
     */
    @Component
    @ConfigurationProperties("org.shedlock")
    public static class DistributedSchedulerShedlockProperties {
        /**
         * 分布式锁线程池大小.
         *
         * @since 1.3.4
         */
        private int poolSize;
        /**
         * 分布式锁默认锁定最长毫秒数.
         *
         * @since 1.3.4
         */
        private int defaultLockAtMostForMillis;

        public int getPoolSize() {
            return poolSize;
        }

        public void setPoolSize(int poolSize) {
            this.poolSize = poolSize;
        }

        public int getDefaultLockAtMostForMillis() {
            return defaultLockAtMostForMillis;
        }

        public void setDefaultLockAtMostForMillis(int defaultLockAtMostForMillis) {
            this.defaultLockAtMostForMillis = defaultLockAtMostForMillis;
        }
    }
}
