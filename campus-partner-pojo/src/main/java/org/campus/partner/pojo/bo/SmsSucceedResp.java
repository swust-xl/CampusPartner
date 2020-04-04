package org.campus.partner.pojo.bo;

/**
 * 
 * 第三方短信发送成功的响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class SmsSucceedResp {

    private String status;
    private String to;
    private String sendId;
    private String fee;
    private String smsCredits;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSendId() {
        return sendId;
    }

    public void setSendId(String sendId) {
        this.sendId = sendId;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getSmsCredits() {
        return smsCredits;
    }

    public void setSmsCredits(String smsCredits) {
        this.smsCredits = smsCredits;
    }

}
