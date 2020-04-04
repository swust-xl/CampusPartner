package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.resp.BoUserLoginResp;
import org.campus.partner.util.enums.UserCode;

/**
 *
 * 用户登录验证vo层响应
 *
 *
 * @author cheli
 * @since 1.0.0
 */
public class VoUserLoginResp extends CommonResp {

    private BoUserLoginResp userLoginResp;

    public VoUserLoginResp() {}

    public VoUserLoginResp(UserCode userCode) {
        super(userCode);
    }

    public BoUserLoginResp getUserLoginResp() {
        return userLoginResp;
    }

    public void setUserLoginResp(BoUserLoginResp userLoginResp) {
        this.userLoginResp = userLoginResp;
    }
}
