package org.campus.partner.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.campus.partner.util.validate.Sorts;
import org.campus.partner.util.validator.SortsValidator;

/**
 * 字段排序注解对应的检测类
 *
 * @author xl
 * @since 1.3.5
 */
public class SortsConstraintValidator implements ConstraintValidator<Sorts, String> {

    private SortsValidator sortsValidator;

    /**
     * 字段排序注解对应的检测类的初始化方法
     *
     * @param constraintAnnotation
     *            初始化时注解实例
     * @author xl
     * @since 1.3.5
     */
    @Override
    public void initialize(Sorts constraintAnnotation) {
        sortsValidator = new SortsValidator();
    }

    /**
     * 校验字段排序的函数
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
        return sortsValidator.verify(value);
    }

}
