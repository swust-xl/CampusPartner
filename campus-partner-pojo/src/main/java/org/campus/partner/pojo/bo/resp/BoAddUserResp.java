package org.campus.partner.pojo.bo.resp;

/**
 * 
 * 
 * BO层用户信息
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class BoAddUserResp {

    private String objectId;
    private Long authDatetime;
    private String authId;
    private String authType;
    private String qq;
    private String wechat;
    private String phone;
    private String avatarUrl;
    private String nickname;
    private String gender;
    private String authData;
    private String openId;
    private String isAuthed;
    private String authInstitutionId;
    private String authInstitution;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Long getAuthDatetime() {
        return authDatetime;
    }

    public void setAuthDatetime(Long authDatetime) {
        this.authDatetime = authDatetime;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAuthData() {
        return authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getAuthInstitutionId() {
        return authInstitutionId;
    }

    public void setAuthInstitutionId(String authInstitutionId) {
        this.authInstitutionId = authInstitutionId;
    }

    public String getAuthInstitution() {
        return authInstitution;
    }

    public void setAuthInstitution(String authInstitution) {
        this.authInstitution = authInstitution;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(String isAuthed) {
        this.isAuthed = isAuthed;
    }

}
