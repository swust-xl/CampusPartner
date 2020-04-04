package org.campus.partner.util.string;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.campus.partner.util.Validator;
import org.campus.partner.util.type.NamingStyle;

/**
 * Http相关命名法格式化工具.
 * 
 * @author xl
 * @since 1.0.0
 */
public class HttpCaseFormater {
    private HttpCaseFormater() {}

    private static final String KW_FIELDS = "fields";

    private static final String KW_FILTERS = "filters";

    private static final String KW_SORTS = "sorts";

    public static final String T_R_N_S_REGEX = "[\\t\\r\\n\\s]";
    public static final Pattern T_R_N_S_PATTERN = Pattern.compile(T_R_N_S_REGEX);
    public static final String QUEOTES_REGEX = "[`'\"]";
    public static final String NOT_OP_REGEX = "(" + T_R_N_S_REGEX + "+(?i)NOT" + T_R_N_S_REGEX + "*)";
    /**
     * 支持:
     * 
     * <pre>
     * id, 
     * `id`, 
     * 'id', 
     * "id"
     * </pre>
     */
    private static final String KEY_FIELD_REGEX = QUEOTES_REGEX + "?\\w+" + QUEOTES_REGEX + "?";

    /**
     * 支持:
     * 
     * <pre>
     * IN(xx), in(xx,xx,...), IN('xx','xx',...),
     * IN(xx^xx), IN(xx^xx,xx,...),
     * NOT IN(xx,xx), NOT IN ( xx , xx ),
     * \t\r\n IN \t\r\n(\t\r\n xxx \t\r\n,\t\r\n xx \t\r\n,...)
     * </pre>
     */
    public static final String IN_OP_REGEX = NOT_OP_REGEX + "?" + T_R_N_S_REGEX + "+(?i)IN" + T_R_N_S_REGEX + "*\\("
            + T_R_N_S_REGEX + "*(?![,^])[\\s\\S]+?" + T_R_N_S_REGEX + "*\\)";
    public static final Pattern IN_OP_PATTERN = Pattern.compile(IN_OP_REGEX);
    /**
     * 支持:
     * 
     * <pre>
     * KEY_FIELD_REGEX + IN_OP_REGEX
     * </pre>
     */
    public static final String IN_OP_WITH_KEY_REGEX = KEY_FIELD_REGEX + IN_OP_REGEX;
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_REGEX}
     *      规则
     */
    public static final String LIKE_OP_REGEX = NOT_OP_REGEX + "?" + T_R_N_S_REGEX + "+(?i)LIKE" + T_R_N_S_REGEX
            + "+['\"]([%_])?[\\s\\S]+?([%_])?['\"]";
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_WITH_KEY_REGEX}
     *      规则
     */
    public static final String LIKE_OP_WITH_KEY_REGEX = KEY_FIELD_REGEX + LIKE_OP_REGEX;
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_REGEX}
     *      规则
     */
    public static final String BETWEEN_AND_OR_OP_REGEX = NOT_OP_REGEX + "?" + T_R_N_S_REGEX + "+(?i)BETWEEN"
            + T_R_N_S_REGEX + "+[^,&]+?" + T_R_N_S_REGEX + "+(?i)(AND|OR)" + T_R_N_S_REGEX + "+[^,&]+";

    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_WITH_KEY_REGEX}
     *      规则
     */
    public static final String BETWEEN_AND_OR_WITH_KEY_OP_REGEX = KEY_FIELD_REGEX + BETWEEN_AND_OR_OP_REGEX;
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_REGEX}
     *      规则
     */
    private static final String NULL_OP_REGEX = T_R_N_S_REGEX + "+(?i)IS" + NOT_OP_REGEX + "?" + T_R_N_S_REGEX
            + "+(?i)NULL";
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_WITH_KEY_REGEX}
     *      规则
     */
    public static final String NULL_OP_WITH_KEY_REGEX = KEY_FIELD_REGEX + NULL_OP_REGEX;

    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_REGEX}
     *      规则
     */
    public static final String REGEXP_OP_REGEX = NOT_OP_REGEX + "?" + T_R_N_S_REGEX + "+(?i)REGEXP" + T_R_N_S_REGEX
            + "+['\"][\\s\\S]+?['\"]";
    /**
     * @see 同
     *      {@link org.campus.partner.util.string.HttpCaseFormater.IN_OP_WITH_KEY_REGEX}
     *      规则
     */
    public static final String REGEXP_OP_WITH_KEY_REGEX = KEY_FIELD_REGEX + REGEXP_OP_REGEX;

    @SuppressWarnings("unused")
    @Deprecated
    private static final String CONDITION_OP_REGEX = "(?:>|>=|<|<=|=|!=|<>|<=>|::|" + IN_OP_REGEX + "|"
            + BETWEEN_AND_OR_OP_REGEX + "|" + LIKE_OP_REGEX + "|" + NULL_OP_REGEX + "|" + REGEXP_OP_REGEX
            + ")([\\s\\S]*)";
    /**
     * 抽取KEY关键字的正则表达式
     */
    private static final String CONDITION_EXTRACT_KEY_REGEX = "(?:" + IN_OP_REGEX + "|" + BETWEEN_AND_OR_OP_REGEX + "|"
            + LIKE_OP_REGEX + "|" + NULL_OP_REGEX + "|" + REGEXP_OP_REGEX + "|(>|>=|<|<=|=|!=|<>|<=>|::)"
            + T_R_N_S_REGEX + "*[^,&]+" + "|,)";

    private static final Pattern CONDITION_EXTRACT_KEY_PATTERN = Pattern.compile(CONDITION_EXTRACT_KEY_REGEX);

    /**
     * 格式化路径参数字符串变量.<br/>
     * 默认格式化成 <code>NamingStyle.CAMEL</code> 路径
     *
     * @param path
     *            url路径，如：<code>/users/123/profiles</code>
     * @return 格式化后的url路径.若<code>path</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatPath(String path) {
        return formatPath(path, NamingStyle.CAMEL);
    }

    /**
     * 指定命名风格格式化路径参数字符串变量.
     *
     * @param path
     *            url路径，如：<code>/users/123/profiles</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @return 格式化后的url路径.若<code>path</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatPath(String path, NamingStyle formatToNamingStyle) {
        if (path == null) {
            return null;
        }
        if (formatToNamingStyle == null) {
            return formatPath(path);
        }
        StringTokenizer st = new StringTokenizer(path, "/");
        StringBuilder sb = new StringBuilder(" ");
        if (path.startsWith("/")) {
            sb.append("/");
        }
        while (st.hasMoreElements()) {
            sb.append(Case.to(st.nextToken(), formatToNamingStyle))
                    .append("/");
        }
        return sb.deleteCharAt(sb.length() - 1)
                .toString()
                .trim();
    }

    /**
     * 格式化http请求对象变量.<br/>
     * 默认格式化成 <code>NamingStyle.CAMEL</code> 路径
     *
     * @param request
     *            http请求对象
     * @return 格式化后的url路径.若<code>request</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatPath(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return formatPath(request, NamingStyle.CAMEL);
    }

    /**
     * 格式化http请求对象变量.<br/>
     * 默认格式化成 <code>NamingStyle.CAMEL</code> 路径
     *
     * @param request
     *            http请求对象
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @return 格式化后的url路径.若<code>request</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatPath(HttpServletRequest request, NamingStyle formatToNamingStyle) {
        return formatPath(request.getRequestURI(), formatToNamingStyle);
    }

    /**
     * 格式化查询参数字符串变量.<br/>
     * 默认格式化成 <code>NamingStyle.CAMEL</code> 路径
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatQuery(String queryString) {
        return formatQuery(queryString, NamingStyle.CAMEL);
    }

    /**
     * 格式化查询参数字符串变量.<br/>
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatQuery(String queryString, NamingStyle formatToNamingStyle) {
        return formatQuery(queryString, formatToNamingStyle, null);
    }

    /**
     * 格式化查询参数字符串变量,先后针对与逻辑，或逻辑进行处理.<br/>
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @param attachPrefix
     *            附加统一前缀
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @author pxy
     * @since 1.0.0
     */
    public static String formatQuery(String queryString, NamingStyle formatToNamingStyle, String attachPrefix) {
        String formatedQueryString = formatQueryForAnd(queryString, formatToNamingStyle, attachPrefix);

        if (formatedQueryString != null && formatedQueryString.contains("|")) {
            formatedQueryString = formatQueryForOr(formatedQueryString, formatToNamingStyle, null);
        }
        return formatedQueryString;
    }

    /**
     * 格式化查询参数字符串变量,只针对与逻辑进行处理.<br/>
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @param attachPrefix
     *            附加统一前缀
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @author pxy
     * @since 1.0.0
     */
    private static String formatQueryForAnd(String queryString, NamingStyle formatToNamingStyle, String attachPrefix) {
        return formatQuery(queryString, formatToNamingStyle, attachPrefix, "&");
    }

    /**
     * 格式化查询参数字符串变量,只针对或逻辑进行处理.<br/>
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @param attachPrefix
     *            附加统一前缀
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @author pxy
     * @since 1.0.0
     */
    private static String formatQueryForOr(String queryString, NamingStyle formatToNamingStyle, String attachPrefix) {
        return formatQuery(queryString, formatToNamingStyle, attachPrefix, "|");
    }

    /**
     * 格式化查询参数字符串变量,只针对指定的逻辑符号进行处理.<br/>
     *
     * @param queryString
     *            查询参数，如：<code>wsid=xxx&filters=id>0,age<50&error</code>
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @param attachPrefix
     *            附加统一前缀
     * @param logicMark
     *            逻辑分隔符，一般是"&"或者"|"
     * @return 格式化后的queryString路径.若<code>queryString</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @author pxy
     * @since 1.0.0
     */
    private static String formatQuery(String queryString, NamingStyle formatToNamingStyle, String attachPrefix,
            String logicMark) {
        if (queryString == null) {
            return null;
        }
        if (formatToNamingStyle == null) {
            return formatQuery(queryString);
        }
        StringTokenizer st = new StringTokenizer(queryString, logicMark);
        StringBuilder sb = new StringBuilder(" ");
        // "?error=xxx&fields=name,age,real_name&filters=id>6,id<=100,age=10,name!=xl&sorts=+age,-id&offset=0&limit=20"
        while (st.hasMoreElements()) {
            String val = st.nextToken()
                    .trim();
            if (val.startsWith(KW_FIELDS)) {
                // "?fields=name,age,real_name"
                sb.append(formatFieldsParam(val, formatToNamingStyle, attachPrefix))
                        .append(logicMark);
            } else if (val.startsWith(KW_FILTERS)) {
                // "?filters=id>6,id<=100,age=10,name!=xl"
                sb.append(formatFiltersParam(val, formatToNamingStyle, attachPrefix))
                        .append(logicMark);
            } else if (val.startsWith(KW_SORTS)) {
                // "?sorts=+age,-id"
                sb.append(formatSortsParam(val, formatToNamingStyle, attachPrefix))
                        .append(logicMark);
            } else {
                // "?error="
                // "?offset=0&limit=20"
                // "?id>5,name!=xl,age=20&offset=0&limit=20"
                sb.append(formatNormalParam(val, formatToNamingStyle, attachPrefix))
                        .append(logicMark);
            }
        }
        return sb.deleteCharAt(sb.length() - 1)
                .toString()
                .trim();
    }

    /**
     * 格式化ttp请求对象包含的查询参数.<br/>
     * 默认格式化成 <code>NamingStyle.CAMEL</code> 命名风格
     *
     * @param request
     *            http请求对象
     * @return 格式化后的request对象包含的查询参数.若<code>request</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatQuery(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        return formatQuery(request, NamingStyle.CAMEL);
    }

    /**
     * 格式化ttp请求对象包含的查询参数.<br/>
     *
     * @param request
     *            http请求对象
     * @param formatToNamingStyle
     *            将要格式化后的命名风格
     * @return 格式化后的request对象包含的查询参数.若<code>request</code>为<code>null</code>,
     *         则返回<code>null</code>
     * @author xl
     * @since 1.0.0
     */
    public static String formatQuery(HttpServletRequest request, NamingStyle formatToNamingStyle) {
        if (request == null) {
            return null;
        }
        return formatQuery(request.getQueryString(), formatToNamingStyle);
    }

    /**
     * 格式化 <code>KW_FIELDS</code>参数
     */
    private static String formatFieldsParam(String fieldsKVStr, NamingStyle formatToNamingStyle, String attachPrefix) {
        int idx = fieldsKVStr.indexOf("=");
        if (idx < 0) {
            return formatNormalParam(fieldsKVStr, formatToNamingStyle, attachPrefix);
        }
        StringBuilder sb = new StringBuilder(" ");
        String key = fieldsKVStr.substring(0, idx);
        return sb.append(Case.to(key, formatToNamingStyle))
                .append("=")
                .append(formatCsvString(fieldsKVStr.substring(idx + 1), formatToNamingStyle, attachPrefix))
                .toString()
                .trim();
    }

    /**
     * 格式化 <code>KW_FILTERS</code>参数
     */
    private static String formatFiltersParam(String filtersKVStr, NamingStyle formatToNamingStyle,
            String attachPrefix) {
        int idx = filtersKVStr.indexOf("=");
        if (idx < 0) {
            return formatNormalParam(filtersKVStr, formatToNamingStyle, attachPrefix);
        }
        StringBuilder sb = new StringBuilder(" ");
        String key = filtersKVStr.substring(0, idx);
        return sb.append(Case.to(key, formatToNamingStyle))
                .append("=")
                .append(formatCsvString(filtersKVStr.substring(idx + 1), formatToNamingStyle, attachPrefix))
                .toString()
                .trim();
    }

    /**
     * 格式化 <code>KW_SORTS</code>参数
     */
    private static String formatSortsParam(String sortsKStr, NamingStyle formatToNamingStyle, String attachPrefix) {
        int idx = sortsKStr.indexOf("=");
        if (idx < 0) {
            return formatNormalParam(sortsKStr, formatToNamingStyle, attachPrefix);
        }
        StringBuilder sb = new StringBuilder(" ");
        String key = sortsKStr.substring(0, idx);
        sb.append(Case.to(key, formatToNamingStyle))
                .append("=");
        String[] multiVals = sortsKStr.substring(idx + 1)
                .split(",");
        for (String val : multiVals) {
            if (val.length() < 1) {
                return formatNormalParam(sortsKStr, formatToNamingStyle, attachPrefix);
            }
            String fPrefix = val.substring(0, 1);
            String fields = val.substring(1);
            sb.append(fPrefix)
                    .append(Case.to(fields, formatToNamingStyle))
                    .append(",");
        }
        return sb.deleteCharAt(sb.length() - 1)
                .toString()
                .trim();
    }

    /**
     * 格式化正常参数
     */
    private static String formatNormalParam(String normalKVStr, NamingStyle formatToNamingStyle, String attachPrefix) {
        return formatCsvString(normalKVStr, formatToNamingStyle, attachPrefix);
    }

    /**
     * 格式化CSV字符串Key值，且支持<code>CONDITION_EXTRACT_KEY_REGEX</code>表达式,并允许给Key添加统一前缀
     */
    private static String formatCsvString(String csvStr, NamingStyle formatToNamingStyle, String attachPrefix) {
        String prefix = attachPrefix;
        if (prefix == null) {
            prefix = "";
        }
        String[] keys = CONDITION_EXTRACT_KEY_PATTERN.split(csvStr);
        // 保证KEY的索引第一个找到且没有歧义
        StringBuilder tempCsvSB = new StringBuilder(csvStr);
        // 根据tempCsvSB来动态替换目标索引的KEY
        StringBuilder formatedCsvSB = new StringBuilder(csvStr);
        int increaseLen = 0;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            key = T_R_N_S_PATTERN.matcher(key)
                    .replaceAll("")
                    .trim();
            if (Validator.isEmpty(key)) {
                continue;
            }
            int idx = tempCsvSB.indexOf(key);
            if (idx < 0) {
                // System.err.println("[" + key + "]在[" + tempCsvSB + "]中不存在！");
                continue;
            }
            String newKey = prefix.concat(Case.to(key, formatToNamingStyle));
            int needStartIdx = idx + increaseLen;
            increaseLen += newKey.length() - key.length();
            int keyEndIdx = idx + key.length();
            tempCsvSB.replace(idx, keyEndIdx, getFixedSameStr(" ", key.length()));
            int needEndIdx = needStartIdx + key.length();
            formatedCsvSB.replace(needStartIdx, needEndIdx, newKey);
        }
        return formatedCsvSB.toString()
                .trim();
    }

    /**
     * 
     * 获取指定数量相同的字符串.
     *
     * @param str
     *            待获取的基础字符串
     * @param num
     *            str个数
     * @return num个str字符串
     * @author xl
     * @since 1.0.0
     */
    private static String getFixedSameStr(String str, int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

}
