package org.campus.partner.service.rediskeys;

import org.campus.partner.pojo.bo.BoUserSession;
import org.campus.partner.pojo.bo.RedisRoomInfo;

/**
 * 
 * redis中key的一些生成方法
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class RedisKeyGenerator {

    /**
     * 创建房间的redisKey
     */
    public static String generateRoomRedisKey(String roomId) {
        return RedisRoomInfo.class.getSimpleName()
                .concat(":")
                .concat(roomId);
    }

    /**
     * 创建userSession的redisKey
     */
    public static String generateSessionRedisKey(String openId) {
        return BoUserSession.class.getSimpleName()
                .concat(":")
                .concat(openId);
    }
}
