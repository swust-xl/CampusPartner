package org.campus.partner.dao;

import java.util.List;

import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.SortField;

/**
 * 
 * companion_room表操作接口
 * 
 * @author xuLiang
 * @since 1.0.0
 */
public interface CompanionRoomMapper {
    /**
     * 
     * 添加一条companion_room记录
     *
     * @param companionRoom
     *            待添加的记录
     * @return 成功返回添加完成的companionRoom对象，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    CompanionRoom insertCompanionRoom(CompanionRoom companionRoom);

    /**
     * 
     * 通过objectId删除一条companion_room记录
     *
     * @param objectId
     *            待删除记录的objectId
     * @return 成功返回true，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    Boolean deleteCompanionRoom(byte[] objectId);

    /**
     * 可选字段更新一条companion_room记录,不更新为null的字段.
     * 
     * @param companionRoom
     *            待更新的companionRoom对象
     * @return 成功返回更新后的companionRoom对象，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    CompanionRoom updateCompanionRoom(CompanionRoom companionRoom);

    /**
     * 
     * 根据objectId查询一条companion_room记录
     *
     * @param objectId
     *            待查询记录的objectId
     * @return 查询到的记录，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    CompanionRoom selectCompanionRoomByOid(byte[] objectId);

    /**
     * 
     * 根据objectId可选字段查询一条companion_room记录
     *
     * @param objectId
     *            待查询记录的objectId
     * @param fields
     *            选择字段
     * @return 查询到的记录，失败返回null
     * @author xuLiang
     * @since 1.0.0
     */
    CompanionRoom selectCompanionRoomByOidWithFields(byte[] objectId, List<Field<?>> fields);

    /**
     * 
     * 查询多条companion_room记录
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
     * @return 成功返回一个CompanionRoom对象列表，失败返回空值[]
     * @author xuLiang
     * @since 1.0.0
     */
    List<CompanionRoom> selectCompanionRooms(List<Field<?>> fields, Condition filters, List<SortField<?>> sorts,
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
    Integer countCompanionRoom(Condition filters);

    /**
     * 
     * 关键字搜索
     *
     * @param text
     * @return 搜索结果
     * @author xuLiang
     * @since 1.0.0
     */
    List<CompanionRoom> searchText(String text);
}
