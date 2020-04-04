package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.resp.BoUpdateUserResp;
import org.campus.partner.util.enums.UserCode;

public class VoUpdateUserResp extends CommonResp{

    private BoUpdateUserResp data;

    public BoUpdateUserResp getData() {
        return data;
    }

    public void setData(BoUpdateUserResp data) {
        this.data = data;
    }

    public VoUpdateUserResp() {}

    public VoUpdateUserResp(UserCode userCode) {
        super(userCode);
    }
}
