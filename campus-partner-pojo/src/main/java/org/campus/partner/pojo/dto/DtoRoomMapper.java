package org.campus.partner.pojo.dto;

import java.util.List;

import org.campus.partner.pojo.bo.BoRoomInfo;
import org.campus.partner.pojo.bo.req.BoCreateRoomReq;
import org.campus.partner.pojo.po.mysql.tables.pojos.CompanionRoom;
import org.campus.partner.pojo.vo.req.VoCreateRoomReq;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = TypeTransform.class)
public interface DtoRoomMapper {

    DtoRoomMapper INSTANCE = Mappers.getMapper(DtoRoomMapper.class);

    CompanionRoom toCompanionRoom(BoCreateRoomReq req);

    BoRoomInfo toBoRoomInfo(CompanionRoom companionRoom);

    List<BoRoomInfo> toBoRoomInfos(List<CompanionRoom> list);

    BoCreateRoomReq toBoCreateRoomReq(VoCreateRoomReq req);

}
