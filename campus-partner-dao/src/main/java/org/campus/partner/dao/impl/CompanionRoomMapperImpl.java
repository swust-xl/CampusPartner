package org.campus.partner.dao.impl;

import static org.campus.partner.pojo.po.mysql.tables.CompanionRoom.COMPANION_ROOM;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.campus.partner.dao.CompanionRoomMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.campus.partner.pojo.po.mysql.tables.records.CompanionRoomRecord;
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

/**
 * 
 * companion_room表操作实现类
 * 
 * @author xuLiang
 * @since 1.0.0
 */
@Repository
public class CompanionRoomMapperImpl extends CommonMapper implements CompanionRoomMapper {

    @Autowired
    private DSLContext dsl;

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanionRoom insertCompanionRoom(CompanionRoom companionRoom) {
        Assert.notNull(companionRoom, "待添加的companionRoom对象不能为空");
        CompanionRoomRecord record = dsl.newRecord(COMPANION_ROOM);
        record.from(companionRoom);
        int result = record.store();
        if (result == 1) {
            return record.into(CompanionRoom.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean deleteCompanionRoom(byte[] objectId) {
        Assert.notNull(objectId, "待删除的companionRoom记录的objectId不能为空");
        int result = dsl.deleteFrom(COMPANION_ROOM)
                .where(COMPANION_ROOM.OBJECT_ID.eq(objectId))
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
    public CompanionRoom updateCompanionRoom(CompanionRoom companionRoom) {
        Assert.notNull(companionRoom, "待更新的companionRoom记录不能为空");
        Assert.notNull(companionRoom.getObjectId(), "待更新的记录的ObjectId不能为空");
        CompanionRoomRecord record = super.formatUpdateSelective(dsl.newRecord(COMPANION_ROOM, companionRoom));
        int result = dsl.update(COMPANION_ROOM)
                .set(record)
                .where(COMPANION_ROOM.OBJECT_ID.eq(companionRoom.getObjectId()))
                .execute();
        if (result == 1) {
            return buildSelectQuery(COMPANION_ROOM, null, COMPANION_ROOM.OBJECT_ID.eq(companionRoom.getObjectId()))
                    .fetchOneInto(CompanionRoom.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanionRoom selectCompanionRoomByOid(byte[] objectId) {
        Assert.notNull(objectId, "待查询的companionRoom记录的objectId不能为空");
        CompanionRoomRecord record = dsl.selectFrom(COMPANION_ROOM)
                .where(COMPANION_ROOM.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(CompanionRoom.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompanionRoom selectCompanionRoomByOidWithFields(byte[] objectId, List<Field<?>> fields) {
        Assert.notNull(objectId, "待查询的companionRoom记录的objectId不能为空");
        if (fields == null) {
            return selectCompanionRoomByOid(objectId);
        }
        Record record = dsl.select(fields)
                .from(COMPANION_ROOM)
                .where(COMPANION_ROOM.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(CompanionRoom.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompanionRoom> selectCompanionRooms(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts,
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
                .from(COMPANION_ROOM)
                .where(filters)
                .orderBy(defaultSorts)
                .offset(offsetNum)
                .limit(limitNum)
                .fetch();
        if (records.isNotEmpty()) {
            return records.into(CompanionRoom.class);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countCompanionRoom(Condition filters) {
        return filters == null ? dsl.fetchCount(COMPANION_ROOM) : dsl.fetchCount(COMPANION_ROOM, filters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CompanionRoom> searchText(String text) {
        Result<CompanionRoomRecord> records = dsl.selectFrom(COMPANION_ROOM)
                .where(COMPANION_ROOM.SEARCH_TEXT.contains(text))
                .fetch();
        return records.into(CompanionRoom.class);
    }

}
