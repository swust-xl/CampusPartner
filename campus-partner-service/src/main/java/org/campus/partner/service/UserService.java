package org.campus.partner.service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.campus.partner.pojo.bo.req.*;
import org.campus.partner.pojo.bo.resp.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * 
 * 
 * 用户相关操作服务层接口
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Validated
public interface UserService {

    BoAddUserResp addUser(@Valid @NotNull BoAddUserReq req);

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
    BoUserLoginResp userLogin(@Valid @NotBlank String code, BoUpdateUserReq req);

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
    BoGetUserResp getUser(@Valid @NotBlank String userOid);

    /**
     *
     * 更新用户资料
     *
     * @param req
     *            用户信息
     *
     * @return 添加结果响应
     * @author cheli
     * @since 1.0.0
     */
    BoUpdateUserResp updateUser(@Valid @NotNull BoUpdateUserReq req);

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
    BoAuthUserResp authUser(@Valid @NotNull BoAuthUserReq req);

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
    BoAuthCardUserResp authCardUser(@Valid @NotNull BoAuthCardUserReq req);

}
