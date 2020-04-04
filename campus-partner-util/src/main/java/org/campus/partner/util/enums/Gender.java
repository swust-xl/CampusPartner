package org.campus.partner.util.enums;

/**
 * 
 * 用户性别枚举
 *
 *
 * @author xl
 * @since 1.0.0
 */
public enum Gender {

    UNKNOWN(-1, "未知"),
    MALE(0, "男"),
    FEMALE(1, "女");

    private Integer code;

    private String description;

    private Gender(Integer code, String description) {
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
    public static Gender getGender(Integer code) {
        for (Gender level : values()) {
            if (code != null && level.getCode() == code) {
                return level;
            }
        }
        return Gender.UNKNOWN;
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
    public static Gender getGender(String name) {
        for (Gender level : values()) {
            if (level.name()
                    .equalsIgnoreCase(name)) {
                return level;
            }
        }
        return Gender.UNKNOWN;
    }

}
