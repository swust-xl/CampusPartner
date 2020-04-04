package org.campus.partner.pojo.bo.req;

import org.campus.partner.util.enums.ContactType;
import org.campus.partner.util.enums.PostType;
import org.campus.partner.util.enums.RoomStatus;

public class BoCreateRoomReq {

    private String objectId;
    private Integer maxMemberNum;
    private RoomStatus status;
    private ContactType contactType;
    private String comment;
    private Long postTime;
    private String ownerId;
    private String ownerPhone;
    private String content;
    private String startLocation;
    private String endLocation;
    private PostType tag;
    private Long createdDatetime;
    private Long modifiedDatetime;
    private String searchText;

    public Integer getMaxMemberNum() {
        return maxMemberNum;
    }

    public void setMaxMemberNum(Integer maxMemberNum) {
        this.maxMemberNum = maxMemberNum;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    public ContactType getContactType() {
        return contactType;
    }

    public void setContactType(ContactType contactType) {
        this.contactType = contactType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPostTime() {
        return postTime;
    }

    public void setPostTime(Long postTime) {
        this.postTime = postTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public PostType getTag() {
        return tag;
    }

    public void setTag(PostType tag) {
        this.tag = tag;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Long getCreatedDatetime() {
        return createdDatetime;
    }

    public void setCreatedDatetime(Long createdDatetime) {
        this.createdDatetime = createdDatetime;
    }

    public Long getModifiedDatetime() {
        return modifiedDatetime;
    }

    public void setModifiedDatetime(Long modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

}
