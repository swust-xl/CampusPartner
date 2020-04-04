package org.campus.partner.pojo.bo.req;

public class BoUserLoginReq {
    private String code;
    private String encrytedData;
    private String iv;
    private String openId;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEncrytedData() {
        return encrytedData;
    }

    public void setEncrytedData(String encrytedData) {
        this.encrytedData = encrytedData;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }
}
