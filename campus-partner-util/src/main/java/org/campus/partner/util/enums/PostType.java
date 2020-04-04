package org.campus.partner.util.enums;

/**
 * 
 * 结伴类型枚举
 *
 *
 * @author xl
 * @since 1.0.0
 */
public enum PostType {

    UNKNOWN(-1, "未知"),
    SPORTS(0, "运动"),
    TRAVEL(1, "旅行"),
    TRANSPORT(2, "出行"),
    STUDY(3, "学习");

    private Integer code;

    private String description;

    private PostType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * 根据枚举code获取枚举类型.
     *
     * @param code
     *            枚举整型code
     * @return 枚举具体类型
     * @author xl
     * @since 1.0.0
     */
    public static PostType getPostType(Integer code) {
        for (PostType level : values()) {
            if (code != null && level.getCode() == code) {
                return level;
            }
        }
        return PostType.UNKNOWN;
    }

    /**
     * 根据枚举状态名获取枚举类型.
     *
     * @param name
     *            枚举状态名
     * @return 枚举具体类型
     * @author xl
     * @since 1.0.0
     */
    public static PostType getPostType(String name) {
        for (PostType level : values()) {
            if (level.name()
                    .equalsIgnoreCase(name)) {
                return level;
            }
        }
        return PostType.UNKNOWN;
    }
}
