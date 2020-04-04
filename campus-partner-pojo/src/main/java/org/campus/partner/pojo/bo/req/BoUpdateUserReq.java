package org.campus.partner.pojo.bo.req;

import org.campus.partner.pojo.vo.req.VoUpdateUserReq;

public class BoUpdateUserReq extends VoUpdateUserReq {

    private String objectId;
    private String openId;

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

}
