package org.campus.partner.util.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.campus.partner.util.validate.Fields;
import org.campus.partner.util.validator.FieldsValidator;

/**
 * 字段选择的检测类
 *
 * @author xl
 * @since 1.3.5
 */
public class FieldsConstraintValidator implements ConstraintValidator<Fields, String> {

    private FieldsValidator fieldsValidator;

    /**
     * 初始化函数
     *
     * @param constraintAnnotation
     *            获取当前的注解实例
     * @author xl
     * @since 1.3.5
     */
    @Override
    public void initialize(Fields constraintAnnotation) {
        fieldsValidator = new FieldsValidator();
    }

    /**
     * 校验字段选择的函数
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
        return fieldsValidator.verify(value);
    }

}
