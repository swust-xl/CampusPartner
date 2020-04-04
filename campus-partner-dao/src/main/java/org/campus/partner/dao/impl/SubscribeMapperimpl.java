package org.campus.partner.dao.impl;

import static org.campus.partner.pojo.po.mysql.tables.Subscribe.SUBSCRIBE;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.campus.partner.dao.SubscribeMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.Subscribe;
import org.campus.partner.pojo.po.mysql.tables.records.SubscribeRecord;
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
public class SubscribeMapperimpl extends CommonMapper implements SubscribeMapper {

    @Autowired
    private DSLContext dsl;

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscribe insertSubscribe(Subscribe subscribe) {
        Assert.notNull(subscribe, "待添加的subscribe对象不能为空");
        SubscribeRecord record = dsl.newRecord(SUBSCRIBE);
        record.from(subscribe);
        int result = record.store();
        if (result == 1) {
            return record.into(Subscribe.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean deleteSubscribe(byte[] objectId) {
        Assert.notNull(objectId, "待删除的subscribe记录的objectId不能为空");
        int result = dsl.deleteFrom(SUBSCRIBE)
                .where(SUBSCRIBE.OBJECT_ID.eq(objectId))
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
    public Subscribe updateSubscribe(Subscribe subscribe) {
        Assert.notNull(subscribe, "待更新的subscribe记录不能为空");
        Assert.notNull(subscribe.getObjectId(), "待更新的subscribe记录的objectId不能为空");
        SubscribeRecord record = super.formatUpdateSelective(dsl.newRecord(SUBSCRIBE, subscribe));
        int result = dsl.update(SUBSCRIBE)
                .set(record)
                .where(SUBSCRIBE.OBJECT_ID.eq(subscribe.getObjectId()))
                .execute();
        if (result == 1) {
            return buildSelectQuery(SUBSCRIBE, null, SUBSCRIBE.OBJECT_ID.eq(subscribe.getObjectId()))
                    .fetchOneInto(Subscribe.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscribe selectSubscribeByOid(byte[] objectId) {
        Assert.notNull(objectId, "待查询的subscribe记录的objectId不能为空");
        SubscribeRecord record = dsl.selectFrom(SUBSCRIBE)
                .where(SUBSCRIBE.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(Subscribe.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subscribe selectSubscribeByOidWithFields(byte[] objectId, List<Field<?>> fields) {
        Assert.notNull(objectId, "待查询的subscribe记录的objectId不能为空");
        if (fields == null) {
            return selectSubscribeByOid(objectId);
        }
        Record record = dsl.select(fields)
                .from(SUBSCRIBE)
                .where(SUBSCRIBE.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(Subscribe.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Subscribe> selectSubscribes(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts,
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
                .from(SUBSCRIBE)
                .where(filters)
                .orderBy(defaultSorts)
                .offset(offsetNum)
                .limit(limitNum)
                .fetch();
        if (records.isNotEmpty()) {
            return records.into(Subscribe.class);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countSubscribe(Condition filters) {
        return filters == null ? dsl.fetchCount(SUBSCRIBE) : dsl.fetchCount(SUBSCRIBE, filters);
    }

}
