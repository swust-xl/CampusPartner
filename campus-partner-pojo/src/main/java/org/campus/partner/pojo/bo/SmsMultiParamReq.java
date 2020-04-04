package org.campus.partner.pojo.bo;

import java.util.List;

public class SmsMultiParamReq {
    // [{
    // "to":"15*********",
    // "vars":{
    // "name":"kevin",
    // "code":123456
    // }
    // },{
    // "to":"18*********",
    // "vars":{
    // "name":"jacky",
    // "code":236554
    // }
    // },{
    // "to":"13*********",
    // "vars":{
    // "name":"tom",
    // "code":236554
    // }]
    private String appid;
    private String signature;
    private String project;
    private List<SmsMultiVars> multi;

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

    public List<SmsMultiVars> getMulti() {
        return multi;
    }

    public void setMulti(List<SmsMultiVars> multi) {
        this.multi = multi;
    }

}
