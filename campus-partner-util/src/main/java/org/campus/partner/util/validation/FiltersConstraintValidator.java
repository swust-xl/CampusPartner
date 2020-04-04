package org.campus.partner.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.campus.partner.util.validate.Filters;
import org.campus.partner.util.validator.FiltersValidator;

/**
 * 条件过滤注解对应的检测类
 *
 * @author xl
 * @since 1.3.5
 */
public class FiltersConstraintValidator implements ConstraintValidator<Filters, String> {

    FiltersValidator filtersValidator;

    /**
     * 条件过滤注解对应的检测类的初始化方法
     *
     * @param constraintAnnotation
     *            初始化时注解实例
     * @author xl
     * @since 1.3.5
     */
    @Override
    public void initialize(Filters constraintAnnotation) {
        filtersValidator = new FiltersValidator();
    }

    /**
     * 校验条件过滤的函数
     *
     * @param value
     *            校验内容
     * @param context
     *            校验上下文
     * @return 校验结果
     * @author xl
     * @since 1.3.5
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return filtersValidator.verify(value);
    }

}
