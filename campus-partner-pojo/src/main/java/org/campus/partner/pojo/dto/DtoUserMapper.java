package org.campus.partner.pojo.dto;

import org.campus.partner.pojo.bo.req.BoAddUserReq;
import org.campus.partner.pojo.bo.req.BoAuthCardUserReq;
import org.campus.partner.pojo.bo.req.BoAuthUserReq;
import org.campus.partner.pojo.bo.req.BoUpdateUserReq;
import org.campus.partner.pojo.bo.resp.BoAddUserResp;
import org.campus.partner.pojo.bo.resp.BoGetUserResp;
import org.campus.partner.pojo.bo.resp.BoUpdateUserResp;
import org.campus.partner.pojo.bo.resp.BoUserLoginResp;
import org.campus.partner.pojo.po.mysql.tables.pojos.User;
import org.campus.partner.pojo.vo.req.VoAddUserReq;
import org.campus.partner.pojo.vo.req.VoAuthCardUserReq;
import org.campus.partner.pojo.vo.req.VoAuthUserReq;
import org.campus.partner.pojo.vo.req.VoUpdateUserReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 
 * 用户属性转换
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Mapper(uses = TypeTransform.class)
public interface DtoUserMapper {

    DtoUserMapper INSTANCE = Mappers.getMapper(DtoUserMapper.class);

    User toUser(BoAddUserReq boAddUserReq);

    BoAddUserResp toBoAddUserResp(User user);

    BoAddUserReq toBoAddUserReq(VoAddUserReq voAddUserReq);

    BoAuthCardUserReq toBoAuthCardUserReq(VoAuthCardUserReq voAuthCardUserReq);

    BoAuthUserReq toBoAuthUserReq(VoAuthUserReq voAuthUserReq);

    BoUpdateUserReq toBoUpdateUserReq(VoUpdateUserReq voUpdateUserReq);

    BoUpdateUserResp toBoUpdateUserResp(User user);

    User toUser(BoUpdateUserReq req);

    BoGetUserResp toBoGetUserResp(User user);

    BoUserLoginResp toBoUserLoginResp(User user);
}
