/*
 * Copyright © 2019 signit.cn. All rights reserved.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.campus.partner.util.validate;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.campus.partner.util.validation.FieldsConstraintValidator;

/**
 * 数据库字段选择注解
 * </p>
 * 
 * @author xl
 * @since 1.3.5
 */
@Documented
@Retention(RUNTIME)
@Target({ PARAMETER, FIELD })
@Constraint(validatedBy = FieldsConstraintValidator.class)
public @interface Fields {
    /**
     * 默认的错误信息
     *
     * @return 空
     * @author xl
     * @since 1.3.5
     */
    String message() default "";

    /**
     * 用于指定目标的包
     *
     * @return 空
     * @author xl
     * @since 1.3.5
     */
    Class<?>[] groups() default {};

    /**
     * 用于扩展内容
     *
     * @return 空
     * @author xl
     * @since 1.3.5
     */
    Class<? extends Payload>[] payload() default {};
}
