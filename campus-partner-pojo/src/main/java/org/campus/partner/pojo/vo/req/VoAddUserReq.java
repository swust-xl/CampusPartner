package org.campus.partner.pojo.vo.req;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * VO添加用户请求
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
public class VoAddUserReq {
    // 默认传所有数据，后面根据实际情况修改，昵称、性别、openId必填
    private Long authDatetime;
    private String authId;
    private String authType;
    private String qq;
    private String wechat;
    private String phone;
    private String avatarUrl;
    @NotBlank(message = "用户昵称不能为空")
    private String nickname;
    @NotNull(message = "用户性别不能为空")
    private Integer gender;
    private String authData;
    @NotBlank(message = "用户openId不能为空")
    private String openId;
    private String isAuthed;
    private String authInstitutionId;
    private String authInstitution;

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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
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

    public String getIsAuthed() {
        return isAuthed;
    }

    public void setIsAuthed(String isAuthed) {
        this.isAuthed = isAuthed;
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

}
