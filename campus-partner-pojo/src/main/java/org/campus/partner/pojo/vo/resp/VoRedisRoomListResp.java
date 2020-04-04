package org.campus.partner.pojo.vo.resp;

import java.util.List;

import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.util.enums.UserCode;

/**
 * 
 * redis房间信息列表响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class VoRedisRoomListResp extends CommonResp {

    private List<RedisRoomInfo> roomInfos;

    public VoRedisRoomListResp() {}

    public VoRedisRoomListResp(UserCode userCode) {
        super(userCode);
    }

    public List<RedisRoomInfo> getRoomInfos() {
        return roomInfos;
    }

    public void setRoomInfos(List<RedisRoomInfo> roomInfos) {
        this.roomInfos = roomInfos;
    }

}
