/*
 * This file is generated by jOOQ.
*/
package org.campus.partner.pojo.po.mysql.tables.pojos;


import java.io.Serializable;
import java.util.Date;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.9.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RoomMember implements Serializable {

    private static final long serialVersionUID = 182128701;

    private Long   id;
    private byte[] objectId;
    private byte[] userId;
    private byte[] roomId;
    private Date   createddatetime;
    private Date   modifieddatetime;

    public RoomMember() {}

    public RoomMember(RoomMember value) {
        this.id = value.id;
        this.objectId = value.objectId;
        this.userId = value.userId;
        this.roomId = value.roomId;
        this.createddatetime = value.createddatetime;
        this.modifieddatetime = value.modifieddatetime;
    }

    public RoomMember(
        Long   id,
        byte[] objectId,
        byte[] userId,
        byte[] roomId,
        Date   createddatetime,
        Date   modifieddatetime
    ) {
        this.id = id;
        this.objectId = objectId;
        this.userId = userId;
        this.roomId = roomId;
        this.createddatetime = createddatetime;
        this.modifieddatetime = modifieddatetime;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getObjectId() {
        return this.objectId;
    }

    public void setObjectId(byte... objectId) {
        this.objectId = objectId;
    }

    public byte[] getUserId() {
        return this.userId;
    }

    public void setUserId(byte... userId) {
        this.userId = userId;
    }

    public byte[] getRoomId() {
        return this.roomId;
    }

    public void setRoomId(byte... roomId) {
        this.roomId = roomId;
    }

    public Date getCreateddatetime() {
        return this.createddatetime;
    }

    public void setCreateddatetime(Date createddatetime) {
        this.createddatetime = createddatetime;
    }

    public Date getModifieddatetime() {
        return this.modifieddatetime;
    }

    public void setModifieddatetime(Date modifieddatetime) {
        this.modifieddatetime = modifieddatetime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RoomMember (");

        sb.append(id);
        sb.append(", ").append("[binary...]");
        sb.append(", ").append("[binary...]");
        sb.append(", ").append("[binary...]");
        sb.append(", ").append(createddatetime);
        sb.append(", ").append(modifieddatetime);

        sb.append(")");
        return sb.toString();
    }
}
