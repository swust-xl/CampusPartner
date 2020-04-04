package org.campus.partner.util.enums;

/**
 * 
 * 房间状态枚举
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
public enum RoomStatus {

    UNKNOWN(-1, "未知"),
    OPEN(0, "开放"),
    CLOSED(1, "关闭");

    private Integer code;

    private String description;

    private RoomStatus(Integer code, String description) {
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
    public static RoomStatus getRoomStatus(Integer code) {
        for (RoomStatus level : values()) {
            if (code != null && level.getCode() == code) {
                return level;
            }
        }
        return RoomStatus.UNKNOWN;
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
    public static RoomStatus getRoomStatus(String name) {
        for (RoomStatus level : values()) {
            if (level.name()
                    .equalsIgnoreCase(name)) {
                return level;
            }
        }
        return RoomStatus.UNKNOWN;
    }
}
