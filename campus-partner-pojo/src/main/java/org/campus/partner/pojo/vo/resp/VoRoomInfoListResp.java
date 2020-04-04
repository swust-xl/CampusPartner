package org.campus.partner.pojo.vo.resp;

import java.util.List;

import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.util.enums.UserCode;

/**
 * 
 * 房间信息列表响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class VoRoomInfoListResp extends CommonResp {

    private List<BoRoomInfo> roomInfos;

    public VoRoomInfoListResp() {}

    public VoRoomInfoListResp(UserCode userCode) {
        super(userCode);
    }

    public List<BoRoomInfo> getRoomInfos() {
        return roomInfos;
    }

    public void setRoomInfos(List<BoRoomInfo> roomInfos) {
        this.roomInfos = roomInfos;
    }

}
