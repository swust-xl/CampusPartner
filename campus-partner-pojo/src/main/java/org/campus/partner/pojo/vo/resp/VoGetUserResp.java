package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.resp.BoGetUserResp;
import org.campus.partner.util.enums.UserCode;

public class VoGetUserResp extends CommonResp{

    private BoGetUserResp data;

    public BoGetUserResp getData() {
        return data;
    }

    public void setData(BoGetUserResp data) {
        this.data = data;
    }

    public VoGetUserResp() {}

    public VoGetUserResp(UserCode userCode) {
        super(userCode);
    }
}
