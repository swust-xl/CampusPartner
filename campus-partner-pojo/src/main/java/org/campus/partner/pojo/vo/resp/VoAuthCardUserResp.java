package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.vo.req.VoAuthCardUserReq;
import org.campus.partner.util.enums.UserCode;

public class VoAuthCardUserResp extends CommonResp{

    public VoAuthCardUserResp() {}

    public VoAuthCardUserResp(UserCode userCode) {
        super(userCode);
    }
}
