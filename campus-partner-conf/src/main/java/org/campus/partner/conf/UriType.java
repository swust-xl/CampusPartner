package org.campus.partner.conf;

import java.util.regex.Pattern;

import org.campus.partner.conf.cons.RestJsonPath;

/**
 * 
 * URI类型枚举
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public enum UriType {

    UNKNOWN(-1, ""),
    ROOM_OPERATION(0, RestJsonPath.COMPANION_ROOM),
    ROOMS_OPERATION(1, RestJsonPath.COMPANION_ROOMS),
    REDIS_ROOM_OPERATION(2, RestJsonPath.COMPANION_ROOM_REDIS),
    CLOSE_COMPANION_ROOM(3, RestJsonPath.CLOSE_COMPANION_ROOM),
    JOIN_COMPANION_ROOM(4, RestJsonPath.JOIN_COMPANION_ROOM),
    USER_OPERATION(5, RestJsonPath.USER),
    USERS_OPERATION(6, RestJsonPath.USERS),
    WECHAT_LOGIN(7, RestJsonPath.WECHAT_LOGIN),
    USER_ROOMS(8, RestJsonPath.USER_ROOMS);
    // 新增需要校验的接口在此处添加

    private Integer code;// 类型编码
    private String uri;// uri地址
    private String uriRegex;// uri正则匹配字符串,
    private Pattern pattern;// 正则表达式编译后的pattern

    private UriType(Integer code, String uri) {
        this.code = code;
        this.uri = uri;
        // 根据路径，将"{}"表示的路径变量替换为可以匹配的正则表达式，便于之后匹配路径类型
        // 如"/tests/{test_id}"对应的 uriRegex为"[\w-/]*/tests/[^/\s]+(\?[^/\s]*|)"
        this.uriRegex = "";
        String regEx = "\\{[\\w-]+\\}";
        if (code != -1) {
            this.uriRegex = "[\\w-/]*".concat(uri.replaceAll(regEx, "[^/\\\\s]+"))
                    .concat("(\\?[^/\\s]*|)");
        }
        this.pattern = Pattern.compile(this.uriRegex);
    }

    /**
     * 根据枚举状态码解析枚举类型.
     *
     * @param code
     *            枚举状态码
     * @return 对应的枚举类型
     * @author liuqinghua
     * @since 1.0.4
     */
    public static UriType parse(Integer code) {
        for (UriType val : values()) {
            if (val.code == code) {
                return val;
            }
        }
        return UriType.UNKNOWN;
    }

    /**
     * 根据枚举名称解析枚举类型（不区分大小写）.
     *
     * @param name
     *            枚举名称
     * @return 对应的枚举类型
     * @author liuqinghua
     * @since 1.0.4
     */
    public static UriType parse(String name) {
        for (UriType val : values()) {
            if (val.name()
                    .equalsIgnoreCase(name)) {
                return val;
            }
        }
        return UriType.UNKNOWN;
    }

    /**
     * 查找字符串匹配的枚举类型.
     *
     * @param uri
     *            要匹配的uri字符串
     * @return 对应的枚举类型
     * @author liuqinghua
     * @since 1.0.4
     */
    public static UriType matches(String uri) {
        if (uri == null) {
            return UriType.UNKNOWN;
        }
        for (UriType val : values()) {
            if (val.pattern.matcher(uri)
                    .matches()) {
                return val;
            }
        }
        return UriType.UNKNOWN;
    }

    /**
     * 获取code属性字段的值.
     *
     * @return 类型为Integer的code属性字段的值.
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取uri属性字段的值.
     *
     * @return 类型为String的uri属性字段的值.
     */
    public String getUri() {
        return uri;
    }

    /**
     * 获取uriRegex属性字段的值.
     *
     * @return 类型为String的uriRegex属性字段的值.
     */
    public String getUriRegex() {
        return uriRegex;
    }

    /**
     * 获取pattern属性字段的值.
     *
     * @return 类型为Pattern的pattern属性字段的值.
     */
    public Pattern getPattern() {
        return pattern;
    }

    public static void main(String[] args) {
        System.out.println(UriType.matches("/v1/api/resources/companion-rooms"));
    }
}
