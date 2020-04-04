package org.campus.partner.pojo.vo.resp;

import org.campus.partner.pojo.bo.resp.BoAddUserResp;
import org.campus.partner.util.enums.UserCode;

/**
 * 
 * 添加用户vo层响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class VoAddUserResp extends CommonResp {

    private BoAddUserResp user;

    public VoAddUserResp() {}

    public VoAddUserResp(UserCode userCode) {
        super(userCode);
    }

    public BoAddUserResp getUser() {
        return user;
    }

    public void setUser(BoAddUserResp user) {
        this.user = user;
    }

}
