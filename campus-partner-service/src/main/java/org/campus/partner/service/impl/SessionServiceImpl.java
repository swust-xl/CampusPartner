package org.campus.partner.service.impl;

import static org.campus.partner.service.rediskeys.RedisKeyGenerator.generateSessionRedisKey;
import org.campus.partner.pojo.bo.BoUserSession;
import org.campus.partner.service.SessionService;
import org.campus.partner.util.id.IdGenerator;
import org.campus.partner.util.redis.RedisMapper;
import org.campus.partner.util.time.StandardTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * 
 * session操作实现类
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Service
public class SessionServiceImpl implements SessionService {

    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private StandardTimes standardTimes;

    private static final String SESSION_ID_PREFIX = "SID";
    private static final Long THIRTY_DAYS_MILLIS = 2592000000L;// 默认30天失效

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public BoUserSession createUserSession(String openId) {
        BoUserSession session = new BoUserSession();
        String sessionId = idGenerator.getId(SESSION_ID_PREFIX);
        session.setSessionId(sessionId);
        session.setOpenId(openId);
        session.setMaxInactiveInterval(THIRTY_DAYS_MILLIS);
        session.setLastAccessedDatetime(standardTimes.getStandardDate()
                .getTime());
        BoUserSession redisResult = redisMapper.insert(BoUserSession.class, generateSessionRedisKey(sessionId),
                THIRTY_DAYS_MILLIS);
        Assert.notNull(redisResult, "创建session失败");
        return redisResult;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoUserSession getUserSession(String sessionId) {
        return redisMapper.get(BoUserSession.class, generateSessionRedisKey(sessionId));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Boolean deleteUserSession(String sessionId) {
        return redisMapper.delete(BoUserSession.class, generateSessionRedisKey(sessionId));
    }

}
