package org.campus.partner.pojo.bo;

/**
 * 
 * 第三方短信发送失败响应
 *
 * 
 * @author xuLiang
 * @since 1.2.0
 */
public class SmsFailedResp {

    private String status;
    private Integer code;
    private String msg;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
