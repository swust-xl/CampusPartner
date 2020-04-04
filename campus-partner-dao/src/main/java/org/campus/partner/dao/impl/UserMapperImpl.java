package org.campus.partner.dao.impl;

import static org.campus.partner.pojo.po.mysql.tables.User.USER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.campus.partner.dao.UserMapper;
import org.campus.partner.pojo.po.mysql.tables.pojos.User;
import org.campus.partner.pojo.po.mysql.tables.records.UserRecord;
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
public class UserMapperImpl extends CommonMapper implements UserMapper {

    @Autowired
    private DSLContext dsl;

    /**
     * {@inheritDoc}
     */
    @Override
    public User insertUser(User user) {
        Assert.notNull(user, "待添加的user对象不能为空");
        UserRecord record = dsl.newRecord(USER);
        record.from(user);
        int result = record.store();
        if (result == 1) {
            return record.into(User.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean deleteUser(byte[] objectId) {
        Assert.notNull(objectId, "待删除的user记录的objectId不能为空");
        int result = dsl.deleteFrom(USER)
                .where(USER.OBJECT_ID.eq(objectId))
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
    public User updateUser(User user) {
        Assert.notNull(user, "待更新的user记录不能为空");
        Assert.notNull(user.getObjectId(), "待更新的user记录的objectId不能为空");
        UserRecord record = super.formatUpdateSelective(dsl.newRecord(USER, user));
        int result = dsl.update(USER)
                .set(record)
                .where(USER.OBJECT_ID.eq(user.getObjectId()))
                .execute();
        if (result == 1) {
            return buildSelectQuery(USER, null, USER.OBJECT_ID.eq(user.getObjectId())).fetchOneInto(User.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User selectUserByOid(byte[] objectId) {
        Assert.notNull(objectId, "待查询的user记录的objectId不能为空");
        UserRecord record = dsl.selectFrom(USER)
                .where(USER.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(User.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User selectUserByOidWithFields(byte[] objectId, List<Field<?>> fields) {
        Assert.notNull(objectId, "待查询的user记录的objectId不能为空");
        if (fields == null) {
            return selectUserByOid(objectId);
        }
        Record record = dsl.select(fields)
                .from(USER)
                .where(USER.OBJECT_ID.eq(objectId))
                .fetchOne();
        if (record != null) {
            return record.into(User.class);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> selectUsers(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts, Integer offset,
            Integer limit) {
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
                .from(USER)
                .where(filters)
                .orderBy(defaultSorts)
                .offset(offsetNum)
                .limit(limitNum)
                .fetch();
        if (records.isNotEmpty()) {
            return records.into(User.class);
        }
        return Collections.emptyList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer countUser(Condition filters) {
        return filters == null ? dsl.fetchCount(USER) : dsl.fetchCount(USER, filters);
    }

}
