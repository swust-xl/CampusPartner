package org.campus.partner.pojo.vo.req;

public class VoUpdateUserReq {

    private String qq;
    private Integer gender;
    private String avatarUrl;
    private String phone;
    private String nickname;
    private String wechat;

    public void setQq(String qq) {
        this.qq = qq;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public Integer getGender() {
        return gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

}
