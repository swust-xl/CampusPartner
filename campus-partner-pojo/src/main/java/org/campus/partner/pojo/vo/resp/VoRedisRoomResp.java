package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.util.enums.UserCode;

/**
 * 
 * redis房间信息响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class VoRedisRoomResp extends CommonResp {

    private RedisRoomInfo roomInfo;

    public VoRedisRoomResp() {}

    public VoRedisRoomResp(UserCode userCode) {
        super(userCode);
    }

    public RedisRoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(RedisRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

}
