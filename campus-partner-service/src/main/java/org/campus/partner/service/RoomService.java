package org.campus.partner.service;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.bo.req.BoCreateRoomReq;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * 
 * 房间相关服务层操作接口
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Validated
public interface RoomService {
    /**
     * 
     * 创建一个房间
     *
     * @param req
     *            房间信息
     * @return 创建完成的房间信息
     * @author xuLiang
     * @since 1.0.0
     */
    BoRoomInfo createRoom(@Valid @NotNull BoCreateRoomReq req);

    /**
     * 
     * 用户加入房间
     *
     * @param userOid
     *            用户id
     * @param roomOid
     *            房间id
     * @return 用户加入后的房间信息
     * @author xuLiang
     * @since 1.0.0
     */
    RedisRoomInfo joinRoom(@Valid @NotBlank String userOid, @Valid @NotBlank String roomOid);

    /**
     * 
     * 用户退出房间
     *
     * @param userOid
     *            用户id
     * @param roomOid
     *            房间id
     * @return 用户加入后的房间信息
     * @author xuLiang
     * @since 1.0.0
     */
    void exitRoom(@Valid @NotBlank String userOid, @Valid @NotBlank String roomOid);

    /**
     * 
     * 关闭房间
     *
     * @param roomOid
     *            房间id 房间id
     * @return true-关闭成功，false-失败
     * @author xuLiang
     * @since 1.0.0
     */
    Boolean closeRoom(@Valid @NotBlank String roomOid);

    /**
     * 
     * 根据oid查询一个房间信息
     *
     * @param roomOid
     *            房间id
     * @return
     * @author xuLiang
     * @since 1.0.0
     */
    BoRoomInfo queryRoomByOid(@Valid @NotBlank String roomOid);

    /**
     * 
     * 查询房间在redis里的信息
     *
     * @param roomOid
     *            房间id
     * @return 查询结果
     * @author xuLiang
     * @since 1.0.0
     */
    RedisRoomInfo queryRedisRoom(@Valid @NotBlank String roomOid);

    /**
     * 
     * 批量查询房间在redis里的信息
     *
     * @param roomOids
     *            房间id
     * @return 查询结果
     * @author xuLiang
     * @since 1.0.0
     */
    List<RedisRoomInfo> queryRedisRooms(List<String> roomOids);

    /**
     * 
     * 条件查询
     *
     * @param fields
     *            可选字段
     * @param filters
     *            过滤条件
     * @param offset
     *            偏移量
     * @param limit
     *            分页大小
     * @param sorts
     *            字段排序
     * @return 查询到的房间信息列表
     * @author xuLiang
     * @since 1.0.0
     */
    List<BoRoomInfo> queryRooms(String fields, String filters, @Valid @Min(0) Integer offset,
            @Valid @Max(20) Integer limit, String sorts);

    /**
     * 
     * 关键字搜索
     *
     * @param text
     *            内容
     * @return 搜索到的房间
     * @author xuLiang
     * @since 1.0.0
     */
    List<BoRoomInfo> searchText(@Valid @NotBlank String text);

    /**
     * 
     * 查询指定用户加入过的房间
     *
     * @param userOid
     *            用户id
     * @param offset
     *            偏移量
     * @param limit
     *            分页大小
     * @return 查询结果
     * @author xuLiang
     * @since 1.0.0
     */
    List<RedisRoomInfo> queryAllJoinedRooms(@Valid @NotBlank String userOid, @Valid @Min(0) Integer offset,
            @Valid @Max(20) Integer limit);

}
