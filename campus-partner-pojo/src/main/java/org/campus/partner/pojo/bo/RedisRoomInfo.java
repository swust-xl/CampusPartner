package org.campus.partner.pojo.bo;

import java.util.List;

import org.campus.partner.util.enums.ContactType;
import org.campus.partner.util.enums.RoomStatus;

/**
 * 
 * redis的房间信息
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class RedisRoomInfo {

    private String roomId;
    private String ownerId;
    private List<String> members;
    private Integer maxMemberNum;
    private ContactType requiredContactType;
    private RoomStatus roomStatus;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public Integer getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(Integer maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    public ContactType getRequiredContactType() {
        return requiredContactType;
    }

    public void setRequiredContactType(ContactType requiredContactType) {
        this.requiredContactType = requiredContactType;
    }

}
