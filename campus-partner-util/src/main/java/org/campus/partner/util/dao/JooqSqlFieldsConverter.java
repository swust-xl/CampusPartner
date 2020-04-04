package org.campus.partner.util.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SortField;
import org.jooq.Table;
import org.jooq.impl.DSL;

/**
 * 字符串转jooq的数据库查询结果的工具类
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public abstract class JooqSqlFieldsConverter {
    // 驼峰和蛇形命名校验正则
    private static final Pattern PATTERN_OF_SNAKE_CASE = Pattern.compile("[a-z]+(_[a-z]+)*");
    private static final Pattern PATTERN_OF_CAMEL_CASE = Pattern.compile("[a-z][a-zA-Z]*");

    // 比较符号校验正则
    private static final Pattern PATTERN_OF_COMPARISON_OPERATORS = Pattern.compile("=|>=?|<=?|!=");

    // 内容校验正则
    private static final Pattern PATTERN_OF_CONTENT_SIDE = Pattern.compile("'(.*)'|\"(.*)\"");

    // 用于转换命名的符号
    private static final int CODE_POINT_OF_A = 65;
    private static final int CODE_POINT_OF_Z = 90;

    /**
     * 驼峰命名转蛇形命名
     *
     * @param str
     *            驼峰命名的字符串
     * @return 蛇形命名的字符串
     * @author xl
     * @since 1.0.0
     */
    private static String camelCaseToSnakeCase(String str) {
        int length = str.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);

            int code = (int) c;
            if (code >= CODE_POINT_OF_A && code <= CODE_POINT_OF_Z) {
                stringBuilder.append('_');
                stringBuilder.append((char) (code + 32));
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 查找字符串开头的字段
     *
     * @param str
     *            需要被查找的字符串
     * @return 匹配到的字段, 如果没有匹配到返回null
     * @author xl
     * @since 1.0.0
     */
    private static String lookingAtField(String str) {
        // 获取需要判断的字段
        Matcher matcherOfSnakeCase = PATTERN_OF_SNAKE_CASE.matcher(str);
        Matcher matcherOfCamelCase = PATTERN_OF_CAMEL_CASE.matcher(str);

        int cutPosition = 0;
        if (matcherOfSnakeCase.lookingAt()) {
            cutPosition = Math.max(matcherOfSnakeCase.end(), cutPosition);
        }
        if (matcherOfCamelCase.lookingAt()) {
            cutPosition = Math.max(matcherOfCamelCase.end(), cutPosition);
        }

        if (cutPosition == 0) {
            return null;
        }

        return str.substring(0, cutPosition);
    }

    /**
     * 将字段名称转换为蛇形命名,暂时只支持从驼峰转为蛇形
     *
     * @param name
     *            现在的命名
     * @return 返回蛇形命名,如果不能转换则返回null
     * @author xl
     * @since 1.0.0
     */
    private static String getSnakeNamedAuto(String name) {
        if (PATTERN_OF_SNAKE_CASE.matcher(name)
                .matches()) {
            // 不作处理
            return name;
        } else if (PATTERN_OF_CAMEL_CASE.matcher(name)
                .matches()) {
            return camelCaseToSnakeCase(name);
        } else { // 未知的命名
            return null;
        }
    }

    /**
     * 查找字符串开头的比较符
     *
     * @param str
     *            需要被查找的字符串
     * @return 匹配到的操作符, 如果没有匹配到返回null
     * @author xl
     * @since 1.0.0
     */
    private static String lookingAtComparisonOperators(String str) {
        // 获取操作符号
        Matcher matcherOfComparisonOperators = PATTERN_OF_COMPARISON_OPERATORS.matcher(str);

        if (matcherOfComparisonOperators.lookingAt()) {
            return matcherOfComparisonOperators.group();
        }

        return null;
    }

    /**
     * 对过滤内容进行编码
     *
     * @param content
     *            过滤
     * @return 编码后的内容
     * @author xl
     * @since 1.0.0
     */
    private static String encodeFilterContent(String content) {
        // 去除内容两边的'和"
        String newContent = content;
        Matcher matcherOfContentSide = PATTERN_OF_CONTENT_SIDE.matcher(content);
        if (matcherOfContentSide.matches()) {
            newContent = matcherOfContentSide.group(1);
            if (newContent == null) {
                newContent = matcherOfContentSide.group(2);
            }
        }
        newContent = newContent.replace("'", "\\'");
        newContent = "'" + newContent + "'";
        return newContent;
    }

    /**
     * 解析字段选择字符串获取字段数组
     * </p>
     * 接受null和空串 接受"a,b"的形式,字段不能被空格分割,其他允许
     *
     * @param table
     *            字段选择对应的表
     * @param str
     *            段选择字符串
     * @return 对应数据库的字段列表,可能为空列表
     * @throws NullPointerException
     *             必须传入辅助的表 table
     * @throws RuntimeException
     *             字段名称不符合命名规范(蛇形/驼峰)
     * @author xl
     * @since 1.0.0
     */
    public static List<Field<?>> convertToFields(Table<?> table, String str) {
        if (table == null) {
            throw new NullPointerException("未传入对应的表对象");
        }

        List<Field<?>> fields = new ArrayList<Field<?>>();

        if (str == null || str.isEmpty()) {
            return fields;
        }

        String[] fieldsArr = str.trim()
                .split(",");
        for (String fieldString : fieldsArr) {
            String field = fieldString.trim();
            if (field.isEmpty()) {
                continue;
            }
            Matcher matcherOfSnakeCase = PATTERN_OF_SNAKE_CASE.matcher(field);
            Matcher matcherOfCamelCase = PATTERN_OF_CAMEL_CASE.matcher(field);
            if (matcherOfSnakeCase.matches()) {
                // 不做处理
            } else if (matcherOfCamelCase.matches()) {
                field = camelCaseToSnakeCase(field);
            } else {
                throw new RuntimeException(String.format("%s的字段命名格式无效", field));
            }

            Field<?> tableField = table.field(field);
            if (tableField != null) {
                fields.add(tableField);
            }
        }

        return fields;
    }

    /**
     * 解析排序字符串获取排序数组
     * </p>
     * 接受null和空串 接受"+a,-b"的形式,字段和排序符号不能被空格分割,其他允许
     *
     * @param table
     *            排序对应的表
     * @param str
     *            排序字符串
     * @return 排序结果数组
     * @throws NullPointerException
     *             必须传入辅助的表 table
     * @throws RuntimeException
     *             排序字段必须以+或-开头
     * @throws RuntimeException
     *             字段名称不符合规范
     * @author xl
     * @since 1.0.0
     */
    public static List<SortField<?>> convertToSorts(Table<?> table, String str) {
        if (table == null) {
            throw new NullPointerException("未传入对应的表对象");
        }

        List<SortField<?>> sorts = new ArrayList<SortField<?>>();

        if (str == null || str.isEmpty()) {
            return sorts;
        }

        String[] sortsArr = str.trim()
                .split(",");
        for (String sortString : sortsArr) {
            String sort = sortString.trim();
            if (sort.isEmpty()) {
                continue;
            }

            // 判断+/-
            boolean aes; // 是否为正序
            if (sort.charAt(0) == '+') {
                aes = true;
            } else if (sort.charAt(0) == '-') {
                aes = false;
            } else {
                throw new RuntimeException(String.format("%s的没有以+或-开头", sort));
            }

            String field = sort.substring(1);
            field = getSnakeNamedAuto(field);
            if (field == null) {
                throw new RuntimeException(String.format("%s的字段名称不符合规范", sort));
                // 不做处理
            }

            Field<?> tableField = table.field(field);
            if (tableField == null) {
                continue;
            }

            if (aes == true) {
                sorts.add(tableField.asc());
            } else {
                sorts.add(tableField.desc());
            }
        }

        return sorts;
    }

    /**
     * 解析过滤条件字符串获取过滤条件对象
     * </p>
     * 接受null和空串 接受=,!=,>,<,>=,<=构成的单条件语句形式为:<field><compare
     * symbol><content>,例如:wsid=xxxxx
     * </p>
     * 多个语句使用逗号连接,表示与的关系
     * </p>
     * 判断为null或在不为null,使用x=null,x!=null,这个是特殊的,如果需要匹配字符串"null",请使用x='null'或者x="null"
     * 
     * @param table
     *            过滤对应的表
     * @param str
     *            过滤字符串
     * @return 过滤的条件,可能为null,但是null可以表示无过滤条件
     * @throws NullPointerException
     *             必须传入辅助的表 table
     * @throws RuntimeException
     *             没有匹配到有效的字段名称
     * @throws RuntimeException
     *             没有匹配到有效的比较符
     * @author xl
     * @since 1.0.0
     */
    public static Condition convertToCondition(Table<?> table, String str) {
        if (table == null) {
            throw new NullPointerException("未传入对应的表对象");
        }

        if (str == null || str.isEmpty()) {
            return null;
        }

        List<String> conditions = new ArrayList<String>();
        String[] filterStrings = str.split(",");
        for (String filterString : filterStrings) {
            String filter = filterString.trim();
            if (filter.isEmpty()) {
                continue;
            }

            // 获取需要判断的字段
            String field = lookingAtField(filter);
            if (field == null) {
                throw new RuntimeException(String.format("%s中没有匹配到有效的字段名称", filter));
            }
            String filterNext = filter.substring(field.length())
                    .trim();

            // 转换名称
            field = getSnakeNamedAuto(field);

            // 获取操作符号
            String comparisonOperator = lookingAtComparisonOperators(filterNext);
            if (comparisonOperator == null) {
                throw new RuntimeException(String.format("%s中没有匹配到有效的比较符", filter));
            }
            String content = filterNext.substring(comparisonOperator.length())
                    .trim();

            Field<?> tableField = table.field(field);
            // 无效的字段
            if (tableField == null) {
                continue;
            }

            String conditon = "(";
            conditon += tableField.toString()
                    .replace('"', '`');
            // 如果内容是null
            if (content.equals("null") && comparisonOperator.equals("=")) {
                conditon += " is null";
            } else if (content.equals("null") && comparisonOperator.equals("!=")) {
                conditon += " is not null";
            } else {
                conditon += " " + comparisonOperator + " ";
                // 编码内容
                conditon += encodeFilterContent(content);
            }
            conditon += ")";

            conditions.add(conditon);
        }

        if (conditions.size() == 0) {
            return null;
        }
        return DSL.condition(String.join(" AND ", conditions));
    }
}
