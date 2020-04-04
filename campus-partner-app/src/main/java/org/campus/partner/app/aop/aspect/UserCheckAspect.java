package org.campus.partner.app.aop.aspect;

import static org.campus.partner.pojo.po.mysql.tables.User.USER;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.campus.partner.conf.UriType;
import org.campus.partner.dao.UserMapper;
import org.campus.partner.pojo.bo.BoUserSession;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.po.mysql.tables.pojos.User;
import org.campus.partner.service.RoomService;
import org.campus.partner.service.SessionService;
import org.campus.partner.util.enums.HttpKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 
 * 用户检查相关切面
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Aspect
@Component
public class UserCheckAspect {

    private static final Logger LOG = LoggerFactory.getLogger(UserCheckAspect.class);

    public UserCheckAspect() {
        LOG.info("=============注入用户校验切面=============");
    }

    @Autowired
    private SessionService sessionService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletRequest request;

    private void checkIfUserLogin() {
        String userOpenId = request.getHeader(HttpKeys.HEADER_OPEN_ID);
        Assert.notNull(userOpenId, "请求头open-id参数不存在");
        String sessionId = request.getHeader(HttpKeys.HEADER_SESSION_ID);
        Assert.notNull(sessionId, "请求头session-id参数不存在");
        BoUserSession session = sessionService.getUserSession(sessionId);
        Assert.notNull(session, "用户登录已过期，请重新登录");
        User user = userMapper.selectUsers(null, USER.OPEN_ID.eq(userOpenId), null, null, null)
                .get(0);
        Assert.notNull(user, String.format("不存在open-id为[%s]的用户", userOpenId));
        Assert.isTrue(session.getOpenId()
                .equals(user.getOpenId()), "用户会话校验失败，禁止用户操作");
    }

    /**
     * 
     * 检查调用用户是否是已登录的合法用户
     *
     * @param joinPoint
     *            切点
     * @param userCheck
     *            注解
     * @author xuLiang
     * @since 1.2.0
     */
    @Before(value = " @annotation(org.campus.partner.app.aop.annotations.CheckUserAccess)")
    public void checkUserAccess(JoinPoint joinPoint) {
        checkIfUserLogin();
        LOG.info("用户切面校验通过");
    }

    /**
     * 
     * 检查用户是否有操作权限
     *
     * @param joinPoint
     *            切点
     * @param userCheck
     *            注解
     * @author xuLiang
     * @since 1.2.0
     */
    @Before(value = " @annotation(org.campus.partner.app.aop.annotations.CheckUserOperateAccess)")
    public void checkUserAuthority(JoinPoint joinPoint) {
        checkIfUserLogin();
        String userOpenId = request.getHeader(HttpKeys.HEADER_OPEN_ID);
        switch (UriType.matches(request.getRequestURI())) {
        case CLOSE_COMPANION_ROOM:
            String roomOid = (String) joinPoint.getArgs()[0];
            RedisRoomInfo redisResult = roomService.queryRedisRoom(roomOid);
            Assert.isTrue(redisResult.getOwnerId()
                    .equals(userOpenId), String.format("用户[%s]不是房间[%s]的房主，不能关闭该房间", userOpenId, roomOid));
            break;
        case USER_ROOMS:
        case USER_OPERATION:
            String userOid = (String) joinPoint.getArgs()[0];
            User user = userMapper.selectUsers(null, USER.OPEN_ID.eq(userOpenId), null, null, null)
                    .get(0);
            Assert.isTrue(new String(user.getObjectId()).equals(userOid), "用户无操作权限");

            // TODO 新的接口校验在此处添加
        default:
        case UNKNOWN:
            LOG.warn("无法匹配的URI:{}", request.getRequestURI());
            throw new IllegalArgumentException(String.format("无法识别的URI:[%s]", request.getRequestURI()));
        }
        LOG.info("用户切面校验通过");
    }

}
