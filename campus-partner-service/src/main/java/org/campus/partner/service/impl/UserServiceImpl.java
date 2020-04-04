package org.campus.partner.service.impl;

import static org.campus.partner.pojo.po.mysql.tables.User.USER;
import org.campus.partner.dao.UserMapper;
import org.campus.partner.pojo.bo.BoUserSession;
import org.campus.partner.pojo.bo.WechatCodeToSessionResp;
import org.campus.partner.pojo.bo.req.BoAddUserReq;
import org.campus.partner.pojo.bo.req.BoAuthCardUserReq;
import org.campus.partner.pojo.bo.req.BoAuthUserReq;
import org.campus.partner.pojo.bo.req.BoUpdateUserReq;
import org.campus.partner.pojo.bo.resp.BoAddUserResp;
import org.campus.partner.pojo.bo.resp.BoAuthCardUserResp;
import org.campus.partner.pojo.bo.resp.BoAuthUserResp;
import org.campus.partner.pojo.bo.resp.BoGetUserResp;
import org.campus.partner.pojo.bo.resp.BoUpdateUserResp;
import org.campus.partner.pojo.bo.resp.BoUserLoginResp;
import org.campus.partner.pojo.dto.DtoUserMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.User;
import org.campus.partner.service.SessionService;
import org.campus.partner.service.UserService;
import org.campus.partner.util.id.IdGenerator;
import org.campus.partner.util.io.FileHandler;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.type.NamingStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 
 * 
 * 用户相关操作服务层实现类
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SessionService sessionService;

    @Value("${wechat.code2session.url}")
    private String WECHAT_CODE2SESSION_URL;

    private static final String USER_ID_PREFIX = "UID";

    @Transactional
    @Override
    public BoAddUserResp addUser(BoAddUserReq req) {
        req.setObjectId(idGenerator.getId(USER_ID_PREFIX));
        // TODO 参数校验、其他参数设置
        User user = userMapper.insertUser(DtoUserMapper.INSTANCE.toUser(req));
        return DtoUserMapper.INSTANCE.toBoAddUserResp(user);
    }

    @Transactional
    @Override
    public BoUserLoginResp userLogin(String code, BoUpdateUserReq req) {
        String url = WECHAT_CODE2SESSION_URL.concat(code);
        WechatCodeToSessionResp wechatResp = JsonConverter.decodeAsBean(FileHandler.getDataWithUrl(url),
                NamingStyle.SNAKE, WechatCodeToSessionResp.class);
        LOG.debug("请求微信接口进行验证");
        Assert.isTrue(wechatResp.getErrcode()
                .equals(0), "登录失败");// 微信返回码为0时才请求成功
        User user = userMapper.selectUsers(null, USER.OPEN_ID.eq(wechatResp.getOpenid()), null, null, null)
                .get(0);
        if (user == null) {// 如果用户不存在
            req.setObjectId(idGenerator.getId(USER_ID_PREFIX));
            req.setOpenId(wechatResp.getOpenid());
            User insertResult = userMapper.insertUser(DtoUserMapper.INSTANCE.toUser(req));
            Assert.notNull(insertResult, "创建新用户失败");
            LOG.info("openId为[{}]的用户不存在，创建一个新用户", wechatResp.getOpenid());
            BoUserSession session = sessionService.createUserSession(insertResult.getOpenId());
            LOG.info("session创建完成，返回结果");
            BoUserLoginResp resp = DtoUserMapper.INSTANCE.toBoUserLoginResp(insertResult);
            resp.setSessionId(session.getSessionId());
            return resp;
        } else {// 用户已存在
            BoUserSession session = sessionService.createUserSession(wechatResp.getOpenid());
            LOG.info("openId为[{}]的用户已存在，覆盖式创建session", wechatResp.getOpenid());
            User userToUpdate = DtoUserMapper.INSTANCE.toUser(req);
            userToUpdate.setObjectId(user.getObjectId());
            userMapper.updateUser(userToUpdate);
            LOG.info("为用户[{}]更新个人信息", user.getObjectId());
            BoUserLoginResp resp = DtoUserMapper.INSTANCE.toBoUserLoginResp(user);
            resp.setSessionId(session.getSessionId());
            return resp;
        }
    }

    /**
     *
     * @param userOid
     * @return
     */
    @Override
    public BoGetUserResp getUser(String userOid) {
        User user = userMapper.selectUserByOid(userOid.getBytes());
        return DtoUserMapper.INSTANCE.toBoGetUserResp(user);
    }

    /**
     *
     * @param userReq
     * @return
     */
    @Override
    public BoUpdateUserResp updateUser(BoUpdateUserReq req) {
        BoUpdateUserResp resp = DtoUserMapper.INSTANCE
                .toBoUpdateUserResp(userMapper.updateUser(DtoUserMapper.INSTANCE.toUser(req)));
        return resp;
    }

    // 登录账号验证
    @Override
    public BoAuthUserResp authUser(BoAuthUserReq req) {
        req.getAccount();
        req.getAccPasswd();
        /**
         *
         * 登录验证
         */
        return null;
    }

    // 登录一卡通验证
    public BoAuthCardUserResp authCardUser(BoAuthCardUserReq req) {
        req.getUrl();
        /**
         * 登录验证
         *
         */
        return null;
    }

}
