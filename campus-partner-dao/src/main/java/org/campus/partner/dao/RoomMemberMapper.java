package org.campus.partner.dao;

import java.util.List;

import org.campus.partner.pojo.po.mysql.tables.pojos.RoomMember;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SortField;

public interface RoomMemberMapper {
    /**
     * 
     * 添加一条RoomMember记录
     *
     * @param roomMember
     *            待添加的记录
     * @return 成功返回添加完成的RoomMember对象，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    RoomMember insertRoomMember(RoomMember roomMember);

    /**
     * 
     * 通过objectId删除一条RoomMember记录
     *
     * @param objectId
     *            待删除记录的objectId
     * @return 成功返回true，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    Boolean deleteRoomMember(byte[] objectId);

    /**
     * 可选字段更新一条RoomMember记录,不更新为null的字段.
     * 
     * @param roomMember
     *            待更新的RoomMember对象
     * @return 成功返回更新后的RoomMember对象，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    RoomMember updateRoomMember(RoomMember roomMember);

    /**
     * 
     * 根据objectId查询一条RoomMember记录
     *
     * @param objectId
     *            待查询记录的objectId
     * @return 查询到的记录，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    RoomMember selectRoomMemberByOid(byte[] objectId);

    /**
     * 
     * 根据objectId可选字段查询一条RoomMember记录
     *
     * @param objectId
     *            待查询记录的objectId
     * @param fields
     *            选择字段
     * @return 查询到的记录，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    RoomMember selectRoomMemberByOidWithFields(byte[] objectId, List<Field<?>> fields);

    /**
     * 
     * 查询多条RoomMember记录
     *
     * @param fields
     *            选择字段
     * @param filters
     *            过滤条件
     * @param sorts
     *            排序
     * @param offset
     *            偏移量，默认0
     * @param limit
     *            分页大小，默认20
     * @return 成功返回一个RoomMember对象列表，失败返回空值[]
     * @author xuLiang
     * @since 1.0.0
     */
    List<RoomMember> selectRoomMembers(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts,
            Integer offset, Integer limit);

    /**
     * 
     * 统计一定条件下的记录条数.
     *
     * @param filters
     *            过滤条件
     * @return 返回统计条数,filters为空则返回表里所有记录条数
     * @author xuLiang
     * @since 1.0.0
     */
    Integer countRoomMember(Condition filters);
}
