package org.campus.partner.pojo.bo;

/**
 * 
 * 发送短信的json参数实体类
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class SmsSingleParamReq {
    // {
    // "to":"15*********",
    // "vars":{
    // "name":"kevin",
    // "code":123456
    // }
    // }
    private String appid;
    private String signature;
    private String project;
    private String to;
    private SmsVars vars;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public SmsVars getVars() {
        return vars;
    }

    public void setVars(SmsVars vars) {
        this.vars = vars;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

}
