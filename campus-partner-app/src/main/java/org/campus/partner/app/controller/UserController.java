package org.campus.partner.app.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.campus.partner.pojo.vo.req.VoAuthCardUserReq;
import org.campus.partner.pojo.vo.req.VoAuthUserReq;
import org.campus.partner.pojo.vo.req.VoUpdateUserReq;
import org.campus.partner.pojo.vo.resp.VoAuthCardUserResp;
import org.campus.partner.pojo.vo.resp.VoAuthUserResp;
import org.campus.partner.pojo.vo.resp.VoGetUserResp;
import org.campus.partner.pojo.vo.resp.VoUserLoginResp;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 *
 * 用户相关操作vo层接口
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Validated
public interface UserController {
    /**
     *
     * 微信授权登录
     *
     * @param code
     *            微信登录信息
     * @return 添加结果响应
     * @author cheli
     * @since 1.0.0
     */
    VoUserLoginResp userLogin(@Valid @NotBlank String code, VoUpdateUserReq req);

    /**
     *
     * 获取用户资料
     *
     * @param userOid
     *            用户uid
     * @return 添加结果响应
     * @author cheli
     * @since 1.0.0
     */
    VoGetUserResp getUser(@Valid @NotBlank String userOid);

    /**
     *
     * 教务处学生认证
     *
     * @param req
     *            用户信息
     * @return 添加结果响应
     * @author cheli
     * @since 1.0.0
     */
    VoAuthUserResp authUser(@Valid @NotBlank String uid, @Valid @NotNull VoAuthUserReq req);

    /**
     *
     * 用户一卡通学生认证
     *
     * @param req
     *            用户信息
     * @return 添加结果响应
     * @author cheli
     * @since 1.0.0
     */
    VoAuthCardUserResp authCardUser(@Valid @NotBlank String uid, @Valid @NotNull VoAuthCardUserReq req);

}