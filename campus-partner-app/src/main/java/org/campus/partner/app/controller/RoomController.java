package org.campus.partner.app.controller;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.campus.partner.pojo.vo.req.VoCreateRoomReq;
import org.campus.partner.pojo.vo.resp.VoRoomInfoListResp;
import org.campus.partner.pojo.vo.resp.VoRedisRoomListResp;
import org.campus.partner.pojo.vo.resp.VoRedisRoomResp;
import org.campus.partner.pojo.vo.resp.VoRoomInfoResp;
import org.campus.partner.util.validate.Fields;
import org.campus.partner.util.validate.Filters;
import org.campus.partner.util.validate.Sorts;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * 
 * 房间操作控制器接口
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Validated
public interface RoomController {
    /**
     * 
     * 创建一个房间
     *
     * @param req
     *            房间信息
     * @return 创建结果
     * @author xuLiang
     * @since 1.0.0
     */
    VoRoomInfoResp createRoom(@Valid @NotNull VoCreateRoomReq req);

    /**
     * 
     * 加入房间
     *
     * @param userOid
     *            用户id
     * @param roomOid
     *            房间id
     * @return 加入结果
     * @author xuLiang
     * @since 1.0.0
     */
    VoRedisRoomResp joinRoom(@Valid @NotBlank String userOid, @Valid @NotBlank String roomOid);

    /**
     * 
     * 退出房间
     *
     * @param userOid
     *            用户id
     * @param roomOid
     *            房间id
     * @return 加入结果
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
     * @author xuLiang
     * @since 1.0.0
     */
    void closeRoom(@Valid @NotBlank String roomOid);

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
    VoRoomInfoResp queryRoom(@Valid @NotBlank String roomOid);

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
    VoRedisRoomResp queryRedisRoom(@Valid @NotBlank String roomOid);

    /**
     * 
     * 关键字搜索
     *
     * @param keyword
     *            内容
     * @return 搜索到的房间
     * @author xuLiang
     * @since 1.0.0
     */
    VoRoomInfoListResp keywordSearch(@Valid @NotBlank String keyword);

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
    VoRedisRoomListResp queryAllJoinedRooms(@Valid @NotBlank String userOid, @Valid @Min(0) Integer offset,
            @Valid @Max(20) Integer limit);

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
    VoRoomInfoListResp queryRooms(@Valid @Fields String fields, @Valid @Filters String filters,
            @Valid @Min(0) Integer offset, @Valid @Max(20) Integer limit, @Valid @Sorts String sorts);
}
