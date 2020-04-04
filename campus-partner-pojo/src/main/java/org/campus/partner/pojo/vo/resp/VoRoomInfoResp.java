package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.util.enums.UserCode;

/**
 * 
 * 房间信息响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class VoRoomInfoResp extends CommonResp {

    private BoRoomInfo roomInfo;

    public VoRoomInfoResp() {}

    public VoRoomInfoResp(UserCode userCode) {
        super(userCode);
    }

    public BoRoomInfo getRoomInfo() {
        return roomInfo;
    }

    public void setRoomInfo(BoRoomInfo roomInfo) {
        this.roomInfo = roomInfo;
    }

}
