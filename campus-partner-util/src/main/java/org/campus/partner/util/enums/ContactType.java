package org.campus.partner.util.enums;

/**
 * 
 * 用户联系方式类型枚举
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
public enum ContactType {

    UNKNOWN(-1, "未知"),
    QQ(0, "QQ"),
    WECHAT(1, "微信"),
    PHONE(2, "电话");

    private Integer code;

    private String description;

    private ContactType(Integer code, String description) {
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
    public static ContactType getContactType(Integer code) {
        for (ContactType level : values()) {
            if (code != null && level.getCode() == code) {
                return level;
            }
        }
        return ContactType.UNKNOWN;
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
    public static ContactType getContactType(String name) {
        for (ContactType level : values()) {
            if (level.name()
                    .equalsIgnoreCase(name)) {
                return level;
            }
        }
        return ContactType.UNKNOWN;
    }
}
