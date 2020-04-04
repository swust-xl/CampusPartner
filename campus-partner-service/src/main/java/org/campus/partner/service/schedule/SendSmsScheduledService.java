package org.campus.partner.service.schedule;

import java.util.ArrayList;
import java.util.List;

import org.campus.partner.dao.CompanionRoomMapper;
import org.campus.partner.dao.UserMapper;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.bo.SmsMultiVars;
import org.campus.partner.pojo.bo.SmsVars;
import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.campus.partner.service.SmsService;
import org.campus.partner.util.enums.PostType;
import org.campus.partner.util.enums.RoomStatus;
import org.campus.partner.util.redis.RedisMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * 定时给已满房间的成员发送短信
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
@EnableScheduling
@Service
public class SendSmsScheduledService {

    private static final Logger LOG = LoggerFactory.getLogger(SendSmsScheduledService.class);

    private static final String EVERY_TWENTY_SECONDS_CRON = "0/20 * * * * ?";// 每20秒触发

    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private CompanionRoomMapper companionRoomMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SmsService smsService;

    @Scheduled(cron = EVERY_TWENTY_SECONDS_CRON)
    // @SchedulerLock(name = "SmsLock", lockAtMostFor = 1200000)
    @Transactional
    private void sendSmsToMembersIfRoomIsFull() {
        LOG.info("[------------定时任务------------]向已满房间的成员发送短信");
        List<RedisRoomInfo> redisRoomList = redisMapper.multiGet(RedisRoomInfo.class);
        redisRoomList.forEach(e -> {
            if (e.getMaxMemberNum()
                    .equals(e.getMembers()
                            .size())
                    && e.getRoomStatus()
                            .equals(RoomStatus.OPEN)) {// 房间成员数量与最大数量相等&&房间为open状态
                CompanionRoom room = companionRoomMapper.selectCompanionRoomByOid(e.getRoomId()
                        .getBytes());
                List<SmsMultiVars> varList = new ArrayList<>();
                e.getMembers()
                        .forEach(m -> {
                            SmsMultiVars smsMultiVars = new SmsMultiVars();
                            smsMultiVars.setTo(userMapper.selectUserByOid(m.getBytes())
                                    .getPhone());
                            SmsVars vars = new SmsVars();
                            if (PostType.getPostType(room.getTag())
                                    .equals(PostType.TRANSPORT)) {// 如果类型是transport要加上出发地
                                vars.setStartLocation(room.getStartLocation());
                            }
                            vars.setEndLocation(room.getEndLocation());
                            smsMultiVars.setVars(vars);
                            varList.add(smsMultiVars);
                        });
                smsService.sendMultiSms(PostType.getPostType(room.getTag()), varList);
                LOG.info("向房间[{}]的成员{}发送短信", e.getRoomId(), e.getMembers()
                        .toString());
                e.setRoomStatus(RoomStatus.CLOSED);
                redisMapper.upsert(e, generateRoomRedisKey(e.getRoomId()));
                LOG.info("此次结伴已完成，关闭房间[{}]", e.getRoomId());
            }
        });
    }

    private String generateRoomRedisKey(String roomId) {
        return RedisRoomInfo.class.getSimpleName()
                .concat(":")
                .concat(roomId);
    }
}
