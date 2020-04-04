package org.campus.partner.service.impl;

import static org.campus.partner.service.rediskeys.RedisKeyGenerator.generateRoomRedisKey;
import static org.campus.partner.pojo.po.mysql.tables.CompanionRoom.COMPANION_ROOM;
import static org.campus.partner.pojo.po.mysql.tables.RoomMember.ROOM_MEMBER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.campus.partner.dao.CompanionRoomMapper;
import org.campus.partner.dao.RoomMemberMapper;
import org.campus.partner.dao.UserMapper;
import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.bo.req.BoCreateRoomReq;
import org.campus.partner.pojo.dto.DtoRoomMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.campus.partner.pojo.po.mysql.tables.pojos.RoomMember;
import org.campus.partner.pojo.po.mysql.tables.pojos.User;
import org.campus.partner.service.RoomService;
import org.campus.partner.util.dao.JooqSqlFieldsConverter;
import org.campus.partner.util.enums.ContactType;
import org.campus.partner.util.enums.RoomStatus;
import org.campus.partner.util.id.IdGenerator;
import org.campus.partner.util.redis.RedisMapper;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.time.StandardTimes;
import org.jooq.Condition;
import org.jooq.Operator;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class RoomServiceImpl implements RoomService {

    private static final Logger LOG = LoggerFactory.getLogger(RoomServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CompanionRoomMapper companionRoomMapper;
    @Autowired
    private RoomMemberMapper roomMemberMapper;
    @Autowired
    private RedisMapper redisMapper;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private StandardTimes standardTimes;

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public BoRoomInfo createRoom(BoCreateRoomReq req) {
        Assert.notNull(req.getStartLocation(), "出发地点不能为空");
        Assert.notNull(req.getEndLocation(), "结束地点不能为空");
        Assert.isTrue(req.getMaxMemberNum() != null && req.getMaxMemberNum() > 0 && req.getMaxMemberNum() <= 4,
                "房间人数不合法");
        Assert.isTrue(req.getContactType() != ContactType.UNKNOWN, "无法识别的联系方式");
        Assert.notNull(req.getOwnerId(), "房主ID不能为空");
        Assert.notNull(userMapper.selectUserByOid(req.getOwnerId()
                .getBytes()), "房主信息不存在");
        String roomId = idGenerator.getId("RID");
        req.setObjectId(roomId);
        req.setPostTime(standardTimes.getStandardDate()
                .getTime());
        req.setCreatedDatetime(standardTimes.getStandardDate()
                .getTime());
        req.setSearchText(createSearchText(req));
        BoRoomInfo result = DtoRoomMapper.INSTANCE
                .toBoRoomInfo(companionRoomMapper.insertCompanionRoom(DtoRoomMapper.INSTANCE.toCompanionRoom(req)));
        LOG.info("向数据库中插入companionRoom记录");
        Assert.notNull(result, "添加房间信息失败");
        RedisRoomInfo redisRoomInfo = new RedisRoomInfo();
        redisRoomInfo.setOwnerId(req.getOwnerId());
        redisRoomInfo.setRoomId(roomId);
        redisRoomInfo.setMaxMemberNum(req.getMaxMemberNum());
        redisRoomInfo.setRoomStatus(RoomStatus.OPEN);
        List<String> memberList = new ArrayList<>();
        memberList.add(req.getOwnerId());
        redisRoomInfo.setMembers(memberList);
        redisRoomInfo.setRequiredContactType(req.getContactType());
        RedisRoomInfo redisResult = redisMapper.insert(redisRoomInfo, generateRoomRedisKey(roomId), 0);
        LOG.info("向redis中插入记录");
        Assert.notNull(redisResult, "添加房间信息失败");
        RoomMember roomMember = new RoomMember();
        roomMember.setObjectId(idGenerator.getId()
                .getBytes());
        roomMember.setRoomId(roomId.getBytes());
        roomMember.setUserId(req.getOwnerId()
                .getBytes());
        roomMember.setCreateddatetime(standardTimes.getStandardDate());
        roomMemberMapper.insertRoomMember(roomMember);
        LOG.info("向RoomMember表插入记录");
        return result;
    }

    private String createSearchText(BoCreateRoomReq req) {
        return req.getTag()
                .toString()
                .concat(";")
                .concat(req.getStartLocation())
                .concat(";")
                .concat(req.getEndLocation());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public RedisRoomInfo joinRoom(String userOid, String roomOid) {
        User user = userMapper.selectUserByOid(userOid.getBytes());
        Assert.notNull(user, String.format("ID为[%s]的用户不存在", userOid));
        CompanionRoom room = companionRoomMapper.selectCompanionRoomByOid(roomOid.getBytes());
        Assert.notNull(room, String.format("ID为[%s]的房间不存在", roomOid));
        RedisRoomInfo redisRoomInfo = redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
        Assert.notNull(redisRoomInfo, "房间已过期");
        Assert.isTrue(redisRoomInfo.getRoomStatus()
                .equals(RoomStatus.OPEN), "房间已关闭");
        Assert.isTrue(redisRoomInfo.getMembers()
                .size() < redisRoomInfo.getMaxMemberNum(), "房间人数已达上限");
        Assert.isTrue(!redisRoomInfo.getMembers()
                .contains(userOid), "用户已经在房间中，请勿重复加入");
        switch (redisRoomInfo.getRequiredContactType()) {
        case PHONE:
            Assert.notNull(user.getPhone(), "进入该房间需绑定手机号");
            break;
        case QQ:
            Assert.notNull(user.getQq(), "进入该房间需绑定QQ");
            break;
        case WECHAT:
            Assert.notNull(user.getQq(), "进入该房间需绑定微信");
            break;
        default:
            throw new IllegalArgumentException("未知的联系方式");
        }
        List<String> newMemberList = new ArrayList<>(redisRoomInfo.getMembers());
        newMemberList.add(userOid);
        redisRoomInfo.setMembers(newMemberList);
        boolean result = redisMapper.upsert(redisRoomInfo, generateRoomRedisKey(roomOid));
        Assert.isTrue(result, "更新房间信息失败");
        RoomMember roomMember = new RoomMember();
        roomMember.setObjectId(idGenerator.getId()
                .getBytes());
        roomMember.setRoomId(roomOid.getBytes());
        roomMember.setUserId(userOid.getBytes());
        roomMember.setCreateddatetime(standardTimes.getStandardDate());
        roomMemberMapper.insertRoomMember(roomMember);
        LOG.debug("向roomMember表插入记录");
        LOG.info("用户[{}]加入房间[{}]", userOid, roomOid);
        return redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void exitRoom(String userOid, String roomOid) {
        User user = userMapper.selectUserByOid(userOid.getBytes());
        Assert.notNull(user, String.format("ID为[%s]的用户不存在", userOid));
        CompanionRoom room = companionRoomMapper.selectCompanionRoomByOid(roomOid.getBytes());
        Assert.notNull(room, String.format("ID为[%s]的房间不存在", roomOid));
        RedisRoomInfo redisRoomInfo = redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
        Assert.notNull(redisRoomInfo, "房间已过期");
        Assert.isTrue(redisRoomInfo.getRoomStatus()
                .equals(RoomStatus.OPEN), "房间已关闭");
        Assert.isTrue(redisRoomInfo.getMembers()
                .contains(userOid), String.format("用户[%s]不在房间[%s]中", userOid, roomOid));
        List<String> newMemberList = new ArrayList<>(redisRoomInfo.getMembers());
        newMemberList.remove(userOid);
        redisRoomInfo.setMembers(newMemberList);
        boolean result = redisMapper.upsert(redisRoomInfo, generateRoomRedisKey(roomOid));
        Assert.isTrue(result, "用户退出房间失败");
        LOG.info("用户[{}]退出房间[{}]", userOid, roomOid);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public Boolean closeRoom(String roomOid) {
        RedisRoomInfo redisRoomInfo = redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
        Assert.notNull(redisRoomInfo, String.format("未找到房间ID为[%s]的记录", roomOid));
        Assert.isTrue(redisRoomInfo.getRoomStatus()
                .equals(RoomStatus.OPEN), "房间已经是关闭状态，请勿重复关闭");
        redisRoomInfo.setRoomStatus(RoomStatus.CLOSED);
        Boolean result = redisMapper.upsert(redisRoomInfo, generateRoomRedisKey(roomOid));
        Assert.isTrue(result, "房间状态改为关闭失败");
        LOG.info("将redis中房间[{}]的状态改为关闭", roomOid);
        CompanionRoom companionRoom = new CompanionRoom();
        companionRoom.setObjectId(roomOid.getBytes());
        companionRoom.setStatus(RoomStatus.CLOSED.getCode());
        companionRoomMapper.updateCompanionRoom(companionRoom);
        LOG.debug("将对应的数据库记录的房间状态改为关闭");
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoRoomInfo queryRoomByOid(String roomOid) {
        BoRoomInfo boRoomInfo = DtoRoomMapper.INSTANCE
                .toBoRoomInfo(companionRoomMapper.selectCompanionRoomByOid(roomOid.getBytes()));
        if (boRoomInfo.getContent() == null) {
            LOG.debug("房间的成员信息还未归档，从redis里实时查询后填充至content字段");
            RedisRoomInfo redisRoomInfo = redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
            boRoomInfo.setContent(JsonConverter.encodeAsString(redisRoomInfo));
        }
        return boRoomInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RedisRoomInfo queryRedisRoom(String roomOid) {
        return redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(roomOid));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RedisRoomInfo> queryRedisRooms(List<String> roomOids) {
        List<String> keys = new ArrayList<>();
        roomOids.forEach(e -> keys.add(generateRoomRedisKey(e)));
        return redisMapper.multiGet(keys, RedisRoomInfo.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BoRoomInfo> queryRooms(String fields, String filters, Integer offset, Integer limit, String sorts) {
        List<CompanionRoom> result = companionRoomMapper.selectCompanionRooms(
                JooqSqlFieldsConverter.convertToFields(COMPANION_ROOM, fields),
                JooqSqlFieldsConverter.convertToCondition(COMPANION_ROOM, filters),
                JooqSqlFieldsConverter.convertToSorts(COMPANION_ROOM, sorts), offset, limit);
        return DtoRoomMapper.INSTANCE.toBoRoomInfos(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BoRoomInfo> searchText(String text) {
        return DtoRoomMapper.INSTANCE.toBoRoomInfos(companionRoomMapper.searchText(text));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RedisRoomInfo> queryAllJoinedRooms(String userOid, Integer offset, Integer limit) {
        List<RoomMember> roomMembers = roomMemberMapper.selectRoomMembers(null,
                ROOM_MEMBER.USER_ID.eq(userOid.getBytes()), null, offset, limit);
        LOG.debug("从RoomMember表查询用户[{}]加入过的房间", userOid);
        Set<String> joinedRooms = new HashSet<>();// Set防止重复添加
        roomMembers.forEach(e -> {
            joinedRooms.add(new String(e.getRoomId()));
        });
        // 构造查询条件
        List<Condition> conditions = new ArrayList<>();
        joinedRooms.forEach(e -> {
            conditions.add(DSL.field(COMPANION_ROOM.OBJECT_ID)
                    .eq(e.getBytes()));
        });

        List<CompanionRoom> allCompanionRooms = companionRoomMapper.selectCompanionRooms(null,
                DSL.condition(Operator.OR, conditions), null, offset, limit);
        LOG.debug("从CompanionRoom表查询用户[{}]加入过的房间信息", userOid);
        List<String> roomsOnlySavedOnRedis = new ArrayList<>(joinedRooms);
        List<RedisRoomInfo> result = new ArrayList<>();
        allCompanionRooms.forEach(e -> {
            if (e.getContent() != null) {// 如果content不为空，即redis中的信息已被归档至CompanionRoom表
                result.add(JsonConverter.decodeAsBean(e.getContent(), RedisRoomInfo.class));
                roomsOnlySavedOnRedis.remove(new String(e.getObjectId()));// 此记录不再从redis中查
            }
        });
        roomsOnlySavedOnRedis.forEach(e -> {
            result.add(redisMapper.get(RedisRoomInfo.class, generateRoomRedisKey(e)));
            LOG.debug("从redis查询房间号为[{}]的信息", e);
        });

        return result;
    }

}
