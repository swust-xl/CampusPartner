package org.campus.partner.app.aop.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于用户访问接口时进行检查,被标记的接口访问时必须带有请求头 {@code open-id }和 {@code session-id },
 * 且操作用户必须已经登录
 * 
 * @author xl
 * @since 1.0.0
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CheckUserAccess {

}
