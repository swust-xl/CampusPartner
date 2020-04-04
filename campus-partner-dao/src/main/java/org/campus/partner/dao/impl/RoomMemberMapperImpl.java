package org.campus.partner.dao.impl;

import static org.campus.partner.pojo.po.mysql.tables.RoomMember.ROOM_MEMBER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.campus.partner.dao.RoomMemberMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.RoomMember;
import org.campus.partner.pojo.po.mysql.tables.records.RoomMemberRecord;
import org.campus.partner.util.dao.CommonMapper;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SortField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

@Repository
public class RoomMemberMapperImpl extends CommonMapper implements RoomMemberMapper {

    @Autowired
    private DSLContext dsl;

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomMember insertRoomMember(RoomMember roomMember) {
        Assert.notNull(roomMember, "待添加的roomMember对象不能为空");
        RoomMemberRecord record = dsl.newRecord(ROOM_MEMBER);
        record.from(roomMember);
        int result = record.store();
        if (result == 1) {
            return record.into(RoomMember.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean deleteRoomMember(byte[] objectId) {
        Assert.notNull(objectId, "待删除的roomMember记录的objectId不能为空");
        int result = dsl.deleteFrom(ROOM_MEMBER)
                .where(ROOM_MEMBER.OBJECT_ID.eq(objectId))
                .execute();
        if (result == 1) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomMember updateRoomMember(RoomMember roomMember) {
        Assert.notNull(roomMember, "待更新的roomMember记录不能为空");
        Assert.notNull(roomMember.getObjectId(), "待更新的记录的ObjectId不能为空");
        RoomMemberRecord record = super.formatUpdateSelective(dsl.newRecord(ROOM_MEMBER, roomMember));
        int result = dsl.update(ROOM_MEMBER)
                .set(record)
                .where(ROOM_MEMBER.OBJECT_ID.eq(roomMember.getObjectId()))
                .execute();
        if (result == 1) {
            return buildSelectQuery(ROOM_MEMBER, null, ROOM_MEMBER.OBJECT_ID.eq(roomMember.getObjectId()))
                    .fetchOneInto(RoomMember.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomMember selectRoomMemberByOid(byte[] objectId) {
        Assert.notNull(objectId, "待查询的roomMember记录的objectId不能为空");
        RoomMemberRecord record = dsl.selectFrom(ROOM_MEMBER)
                .where(ROOM_MEMBER.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(RoomMember.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RoomMember selectRoomMemberByOidWithFields(byte[] objectId, List<Field<?>> fields) {
        Assert.notNull(objectId, "待查询的roomMember记录的objectId不能为空");
        if (fields == null) {
            return selectRoomMemberByOid(objectId);
        }
        Record record = dsl.select(fields)
                .from(ROOM_MEMBER)
                .where(ROOM_MEMBER.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(RoomMember.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RoomMember> selectRoomMembers(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts,
            Integer offset, Integer limit) {
        int offsetNum = 0;
        int limitNum = 20;
        List<SortField<?>> defaultSorts = new ArrayList<SortField<?>>();

        if (offset != null) {
            offsetNum = offset;
        }
        if (limit != null) {
            limitNum = limit;
        }
        if (sorts != null) {
            defaultSorts = sorts;
        }
        Result<Record> records = dsl.select(fields)
                .from(ROOM_MEMBER)
                .where(filters)
                .orderBy(defaultSorts)
                .offset(offsetNum)
                .limit(limitNum)
                .fetch();
        if (records.isNotEmpty()) {
            return records.into(RoomMember.class);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countRoomMember(Condition filters) {
        return filters == null ? dsl.fetchCount(ROOM_MEMBER) : dsl.fetchCount(ROOM_MEMBER, filters);
    }

}
