package org.campus.partner.pojo.vo.resp;

import org.campus.partner.util.enums.UserCode;

public class VoAuthUserResp extends CommonResp{

    public VoAuthUserResp() {}

    public VoAuthUserResp(UserCode userCode) {
        super(userCode);
    }
}
