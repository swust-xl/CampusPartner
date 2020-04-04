package org.campus.partner.pojo.bo;

public class BoRoomMember {

    private String objectId;
    private String userId;
    private String roomId;
    private Long createddatetime;
    private Long modifieddatetime;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Long getCreateddatetime() {
        return createddatetime;
    }

    public void setCreateddatetime(Long createddatetime) {
        this.createddatetime = createddatetime;
    }

    public Long getModifieddatetime() {
        return modifieddatetime;
    }

    public void setModifieddatetime(Long modifieddatetime) {
        this.modifieddatetime = modifieddatetime;
    }

}
