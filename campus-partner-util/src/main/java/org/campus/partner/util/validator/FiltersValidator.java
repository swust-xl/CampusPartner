package org.campus.partner.util.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条件过滤校验器
 * </p>
 *
 * @author xl
 * @since 1.3.6
 */
public class FiltersValidator {
    private static final Pattern PATTERN_OF_SNAKE_CASE = Pattern.compile("[a-z]+(_[a-z]+)*");
    private static final Pattern PATTERN_OF_CAMEL_CASE = Pattern.compile("[a-z][a-zA-Z]*");

    // 比较符号校验正则
    private static final Pattern PATTERN_OF_COMPARISON_OPERATORS = Pattern.compile("=|>=?|<=?|!=");

    // value部分

    /**
     * 条件过滤校验方法
     * </p>
     * 接受null,空串(""),单条件语句(a=b,a>b,a<b,a>=b,a<=b,a!=b)(其中a为有效的字段名,支持蛇形和驼峰命名法),多个单条件语句用逗号连接
     * </p>
     * ,号分隔表示与,暂不支持或运算和in之类的运算
     *
     * @param filters
     *            条件过滤字符串
     * @return 条件过滤校验结果
     * @author xl
     * @since 1.3.6
     */
    public boolean verify(String filters) {
        if (filters == null) {
            return true;
        }
        if (filters.isEmpty()) {
            return true;
        }

        String[] filtersArr = filters.split(",");

        for (String filterString : filtersArr) {
            String filter = filterString.trim();

            // 为空不判断
            if (filter.isEmpty()) {
                continue;
            }

            // 判断key
            Matcher matcherOfSnakeCase = PATTERN_OF_SNAKE_CASE.matcher(filter);
            Matcher matcherOfCamelCase = PATTERN_OF_CAMEL_CASE.matcher(filter);
            int cutPosition = 0;

            // 优先蛇形,因为驼峰可能会匹配到蛇形的前一部分
            if (matcherOfSnakeCase.lookingAt()) {
                cutPosition = Math.max(matcherOfSnakeCase.end(), cutPosition);
            }

            if (matcherOfCamelCase.lookingAt()) {
                cutPosition = Math.max(matcherOfCamelCase.end(), cutPosition);
            }

            if (cutPosition == 0) {
                return false;
            }

            filter = filter.substring(cutPosition).trim();

            //查找操作符
            if (!PATTERN_OF_COMPARISON_OPERATORS.matcher(filter).lookingAt()) {
                return false;
            }
            // 后序内容都接受
        }

        return true;
    }
}
