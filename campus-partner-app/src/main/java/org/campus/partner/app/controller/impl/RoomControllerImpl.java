package org.campus.partner.app.controller.impl;

import java.util.List;

import org.campus.partner.app.aop.annotations.CheckUserOperateAccess;
import org.campus.partner.app.aop.annotations.CheckUserAccess;
import org.campus.partner.app.controller.RoomController;
import org.campus.partner.conf.cons.RestJsonPath;
import org.campus.partner.conf.cons.RestParam;
import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.pojo.bo.RedisRoomInfo;
import org.campus.partner.pojo.dto.DtoRoomMapper;
import org.campus.partner.pojo.vo.req.VoCreateRoomReq;
import org.campus.partner.pojo.vo.resp.VoRoomInfoListResp;
import org.campus.partner.pojo.vo.resp.VoRedisRoomListResp;
import org.campus.partner.pojo.vo.resp.VoRedisRoomResp;
import org.campus.partner.pojo.vo.resp.VoRoomInfoResp;
import org.campus.partner.service.RoomService;
import org.campus.partner.util.enums.UserCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * 房间操作控制器实现类
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@RestController
public class RoomControllerImpl implements RoomController {

    @Autowired
    private RoomService roomService;

    /**
     * {@inheritDoc}
     */
    @PostMapping(RestJsonPath.COMPANION_ROOMS)
    @ResponseStatus(HttpStatus.CREATED)
    @CheckUserAccess
    @Override
    public VoRoomInfoResp createRoom(@RequestBody VoCreateRoomReq req) {
        BoRoomInfo result = roomService.createRoom(DtoRoomMapper.INSTANCE.toBoCreateRoomReq(req));
        VoRoomInfoResp resp = new VoRoomInfoResp(UserCode.SUCCESS);
        resp.setRoomInfo(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @PostMapping(RestJsonPath.JOIN_COMPANION_ROOM)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoRedisRoomResp joinRoom(@PathVariable(name = RestParam.PV_USER_ID) String userOid,
            @PathVariable(name = RestParam.PV_COMPANION_ROOM_ID) String roomOid) {
        RedisRoomInfo result = roomService.joinRoom(userOid, roomOid);
        VoRedisRoomResp resp = new VoRedisRoomResp(UserCode.SUCCESS);
        resp.setRoomInfo(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @PostMapping(RestJsonPath.EXIT_COMPANION_ROOM)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CheckUserAccess
    @Override
    public void exitRoom(@PathVariable(name = RestParam.PV_USER_ID) String userOid,
            @PathVariable(name = RestParam.PV_COMPANION_ROOM_ID) String roomOid) {
        roomService.exitRoom(userOid, roomOid);
    }

    /**
     * {@inheritDoc}
     */
    @PostMapping(RestJsonPath.CLOSE_COMPANION_ROOM)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @CheckUserOperateAccess
    @Override
    public void closeRoom(@PathVariable(name = RestParam.PV_COMPANION_ROOM_ID) String roomOid) {
        Boolean result = roomService.closeRoom(roomOid);
        Assert.isTrue(result, String.format("删除房间号为[%s]的房间失败", roomOid));
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(RestJsonPath.COMPANION_ROOM)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoRoomInfoResp queryRoom(@PathVariable(name = RestParam.PV_COMPANION_ROOM_ID) String roomOid) {
        BoRoomInfo result = roomService.queryRoomByOid(roomOid);
        VoRoomInfoResp resp = new VoRoomInfoResp(UserCode.SUCCESS);
        resp.setRoomInfo(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(RestJsonPath.COMPANION_ROOM_REDIS)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoRedisRoomResp queryRedisRoom(@PathVariable(name = RestParam.PV_COMPANION_ROOM_ID) String roomOid) {
        RedisRoomInfo result = roomService.queryRedisRoom(roomOid);
        VoRedisRoomResp resp = new VoRedisRoomResp(UserCode.SUCCESS);
        resp.setRoomInfo(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(RestJsonPath.COMPANION_ROOMS_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    @Override
    public VoRoomInfoListResp keywordSearch(@RequestParam(name = RestParam.QV_KEYWORD) String keyword) {
        List<BoRoomInfo> result = roomService.searchText(keyword);
        VoRoomInfoListResp resp = new VoRoomInfoListResp(UserCode.SUCCESS);
        resp.setRoomInfos(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(RestJsonPath.USER_ROOMS)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserOperateAccess
    @Override
    public VoRedisRoomListResp queryAllJoinedRooms(@PathVariable(name = RestParam.PV_USER_ID) String userOid,
            @RequestParam(name = RestParam.QV_OFFSET, required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = RestParam.QV_LIMIT, required = false, defaultValue = "20") Integer limit) {
        List<RedisRoomInfo> result = roomService.queryAllJoinedRooms(userOid, offset, limit);
        VoRedisRoomListResp resp = new VoRedisRoomListResp(UserCode.SUCCESS);
        resp.setRoomInfos(result);
        return resp;
    }

    /**
     * {@inheritDoc}
     */
    @GetMapping(RestJsonPath.COMPANION_ROOMS)
    @ResponseStatus(HttpStatus.OK)
    @CheckUserAccess
    @Override
    public VoRoomInfoListResp queryRooms(@RequestParam(name = RestParam.QV_FIELDS, required = false) String fields,
            @RequestParam(name = RestParam.QV_FILTERS, required = false) String filters,
            @RequestParam(name = RestParam.QV_OFFSET, required = false, defaultValue = "0") Integer offset,
            @RequestParam(name = RestParam.QV_LIMIT, required = false, defaultValue = "20") Integer limit,
            @RequestParam(name = RestParam.QV_SORTS, required = false) String sorts) {
        Assert.isTrue(limit != null && limit > 0, "limit必须大于0");
        Assert.isTrue(offset != null && offset >= 0, "offset必须大于或等于0");
        List<BoRoomInfo> result = roomService.queryRooms(fields, filters, offset, limit, sorts);
        VoRoomInfoListResp resp = new VoRoomInfoListResp(UserCode.SUCCESS);
        resp.setRoomInfos(result);
        return resp;
    }

}
