package org.campus.partner.util.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段选择验证类
 * </p>
 * 
 * @author xl
 * @since 1.3.5
 */
public class FieldsValidator {
    // 驼峰和蛇形命名校验正则
    private static final Pattern PATTERN_OF_KEY = Pattern.compile("([a-z][a-zA-Z]*)|([a-z]+(_[a-z]+)*)");

    /**
     * 字段选择验证方法
     * </p>
     * 接受null,空串(""),驼峰命名(nameOfTest),蛇形命名(name_of_test),内容使用逗号分隔(name,id),支持重复命名
     * </p>
     * 兼容无意义的空格和逗号
     * </p>
     * 不接受的形式有:蛇形带大写(name_Of_test),字段含有非字母的字符(name_of_test2),蛇形多了个_(name_of_test_)
     *
     * @param fields
     *            字段选择
     * @return 验证结果
     * @author xl
     * @since 1.3.5
     */
    public boolean verify(String fields) {
        if (fields == null) {
            return true;
        }
        if (fields.isEmpty()) {
            return true;
        }

        String[] fieldsArr = fields.trim()
                .split(",");
        for (String fieldString : fieldsArr) {
            String field = fieldString.trim();
            if (field.isEmpty()) {
                continue;
            }
            Matcher matcher = PATTERN_OF_KEY.matcher(field);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }
}
