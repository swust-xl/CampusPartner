/*
 * This file is generated by jOOQ.
*/
package org.campus.partner.pojo.po.mysql.tables.pojos;


import java.io.Serializable;
import java.util.Date;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class User implements Serializable {

    private static final long serialVersionUID = 1258637504;

    private Long    id;
    private byte[]  objectId;
    private Date    authDatetime;
    private String  authId;
    private Integer authType;
    private String  qq;
    private String  wechat;
    private String  phone;
    private String  avatarUrl;
    private String  nickname;
    private Integer gender;
    private String  authData;
    private String  openId;
    private Integer isAuthed;
    private String  authInstitutionId;
    private String  authInstitution;

    public User() {}

    public User(User value) {
        this.id = value.id;
        this.objectId = value.objectId;
        this.authDatetime = value.authDatetime;
        this.authId = value.authId;
        this.authType = value.authType;
        this.qq = value.qq;
        this.wechat = value.wechat;
        this.phone = value.phone;
        this.avatarUrl = value.avatarUrl;
        this.nickname = value.nickname;
        this.gender = value.gender;
        this.authData = value.authData;
        this.openId = value.openId;
        this.isAuthed = value.isAuthed;
        this.authInstitutionId = value.authInstitutionId;
        this.authInstitution = value.authInstitution;
    }

    public User(
        Long    id,
        byte[]  objectId,
        Date    authDatetime,
        String  authId,
        Integer authType,
        String  qq,
        String  wechat,
        String  phone,
        String  avatarUrl,
        String  nickname,
        Integer gender,
        String  authData,
        String  openId,
        Integer isAuthed,
        String  authInstitutionId,
        String  authInstitution
    ) {
        this.id = id;
        this.objectId = objectId;
        this.authDatetime = authDatetime;
        this.authId = authId;
        this.authType = authType;
        this.qq = qq;
        this.wechat = wechat;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
        this.nickname = nickname;
        this.gender = gender;
        this.authData = authData;
        this.openId = openId;
        this.isAuthed = isAuthed;
        this.authInstitutionId = authInstitutionId;
        this.authInstitution = authInstitution;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getObjectId() {
        return this.objectId;
    }

    public void setObjectId(byte... objectId) {
        this.objectId = objectId;
    }

    public Date getAuthDatetime() {
        return this.authDatetime;
    }

    public void setAuthDatetime(Date authDatetime) {
        this.authDatetime = authDatetime;
    }

    public String getAuthId() {
        return this.authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }

    public Integer getAuthType() {
        return this.authType;
    }

    public void setAuthType(Integer authType) {
        this.authType = authType;
    }

    public String getQq() {
        return this.qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWechat() {
        return this.wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return this.avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAuthData() {
        return this.authData;
    }

    public void setAuthData(String authData) {
        this.authData = authData;
    }

    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getIsAuthed() {
        return this.isAuthed;
    }

    public void setIsAuthed(Integer isAuthed) {
        this.isAuthed = isAuthed;
    }

    public String getAuthInstitutionId() {
        return this.authInstitutionId;
    }

    public void setAuthInstitutionId(String authInstitutionId) {
        this.authInstitutionId = authInstitutionId;
    }

    public String getAuthInstitution() {
        return this.authInstitution;
    }

    public void setAuthInstitution(String authInstitution) {
        this.authInstitution = authInstitution;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("User (");

        sb.append(id);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(authDatetime);
        sb.append(", ").append(authId);
        sb.append(", ").append(authType);
        sb.append(", ").append(qq);
        sb.append(", ").append(wechat);
        sb.append(", ").append(phone);
        sb.append(", ").append(avatarUrl);
        sb.append(", ").append(nickname);
        sb.append(", ").append(gender);
        sb.append(", ").append(authData);
        sb.append(", ").append(openId);
        sb.append(", ").append(isAuthed);
        sb.append(", ").append(authInstitutionId);
        sb.append(", ").append(authInstitution);

        sb.append(")");
        return sb.toString();
    }
}
