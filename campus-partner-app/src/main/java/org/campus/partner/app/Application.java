package org.campus.partner.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * springboot启动类
 *
 * @author xuLiang
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "org.campus.partner")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
