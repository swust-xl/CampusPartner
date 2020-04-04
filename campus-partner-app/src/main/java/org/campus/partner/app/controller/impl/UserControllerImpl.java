package org.campus.partner.app.controller.impl;

import javax.servlet.http.HttpServletRequest;

import org.campus.partner.app.aop.annotations.CheckUserAccess;
import org.campus.partner.app.controller.UserController;
import org.campus.partner.conf.cons.RestJsonPath;
import org.campus.partner.conf.cons.RestParam;
import org.campus.partner.pojo.bo.resp.*;
import org.campus.partner.pojo.dto.DtoUserMapper;
import org.campus.partner.pojo.vo.req.*;
import org.campus.partner.pojo.vo.req.VoAuthUserReq;
import org.campus.partner.pojo.vo.resp.*;
import org.campus.partner.service.UserService;
import org.campus.partner.util.enums.UserCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * 
 * 用户相关操作vo层实现类
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@RestController
public class UserControllerImpl implements UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping(RestJsonPath.WECHAT_LOGIN)
    @ResponseStatus(HttpStatus.OK)
    @Override
    public VoUserLoginResp userLogin(@RequestParam String code, @RequestBody VoUpdateUserReq req) {
        BoUserLoginResp boUserLoginResp = userService.userLogin(code, DtoUserMapper.INSTANCE.toBoUpdateUserReq(req));
        VoUserLoginResp voUserLoginResp = new VoUserLoginResp(UserCode.SUCCESS);
        voUserLoginResp.setUserLoginResp(boUserLoginResp);
        return voUserLoginResp;
    }

    @GetMapping(RestJsonPath.USER)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoGetUserResp getUser(@PathVariable(RestParam.PV_USER_ID) String userOid) {
        BoGetUserResp boGetUserResp = userService.getUser(userOid);
        VoGetUserResp voGetUserResp = new VoGetUserResp(UserCode.SUCCESS);
        voGetUserResp.setData(boGetUserResp);
        return voGetUserResp;
    }

    @PostMapping(RestJsonPath.ACCOUNT_AUTH)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoAuthUserResp authUser(@PathVariable(RestParam.PV_USER_ID) String uid, @RequestBody VoAuthUserReq req) {
        BoAuthUserResp boAuth = userService.authUser(DtoUserMapper.INSTANCE.toBoAuthUserReq(req));
        VoAuthUserResp resp = new VoAuthUserResp(UserCode.SUCCESS);
        return resp;
    }

    @PostMapping(RestJsonPath.CARD_AUTH)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoAuthCardUserResp authCardUser(@PathVariable(RestParam.PV_USER_ID) String uid,
            @RequestBody VoAuthCardUserReq req) {
        BoAuthCardUserResp boresp = userService.authCardUser(DtoUserMapper.INSTANCE.toBoAuthCardUserReq(req));
        VoAuthCardUserResp resp = new VoAuthCardUserResp(UserCode.SUCCESS);
        return resp;
    }
}
