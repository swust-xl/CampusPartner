package org.campus.partner.service.schedule;

import java.util.LinkedList;
import java.util.List;

import org.campus.partner.dao.CompanionRoomMapper;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.campus.partner.util.enums.RoomStatus;
import org.campus.partner.util.redis.RedisMapper;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.time.StandardTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 房间信息定时归档
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
@EnableScheduling
@Service
public class RoomInfoArchieveScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomInfoArchieveScheduledService.class);

    private static final String EVERY_HOUR_CRON = "0 0 0/1 * * ?";// 每1个小时触发
    private static final long ONE_DAY_MILLIS = 86400000L;

    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private CompanionRoomMapper companionRoomMapper;
    @Autowired
    private StandardTimes standardTimes;

    /**
     * 
     * 每1个小时归档redis中已关闭房间记录信息至数据库
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @Scheduled(cron = EVERY_HOUR_CRON)
    // @SchedulerLock(name = "ArchieveLock", lockAtMostFor = 1200000)
    @Transactional
    private void archieveRoomInfoFromRedisToTable() {
        LOG.info("[------------定时任务------------]处理Redis中已经关闭的房间记录");
        List<RedisRoomInfo> redisRoomList = redisMapper.multiGet(RedisRoomInfo.class);
        List<String> redisRoomToDelete = new LinkedList<>();
        CompanionRoom companionRoom = new CompanionRoom();
        redisRoomList.forEach(e -> {
            if (e.getRoomStatus()
                    .equals(RoomStatus.CLOSED)) {
                companionRoom.setObjectId(e.getRoomId()
                        .getBytes());
                companionRoom.setContent(JsonConverter.encodeAsString(e));
                companionRoomMapper.updateCompanionRoom(companionRoom);
                LOG.info("将房间[{}]的信息保存至数据库", e.getRoomId());
                redisRoomToDelete.add(e.getRoomId());
            }
        });
        if (redisRoomToDelete.isEmpty()) {
            LOG.info("没有需要处理的记录");
        } else {
            redisRoomToDelete.forEach(e -> {
                redisMapper.delete(RedisRoomInfo.class, generateRoomRedisKey(e));
                LOG.info("将房间[{}]的信息从redis删除", e);
            });
        }

    }

    private String generateRoomRedisKey(String roomId) {
        return RedisRoomInfo.class.getSimpleName()
                .concat(":")
                .concat(roomId);
    }
}
