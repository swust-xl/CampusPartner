package org.campus.partner.pojo.dto;

import java.sql.Timestamp;

import org.campus.partner.util.enums.AuthStatus;
import org.campus.partner.util.enums.ContactType;
import org.campus.partner.util.enums.Gender;
import org.campus.partner.util.enums.PostType;
import org.campus.partner.util.enums.RoomStatus;

/**
 * 
 * 
 * 属性转换
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class TypeTransform {

    Gender toGender(Integer gender) {
        return gender == null ? null : Gender.getGender(gender);
    }

    Gender toGender(String gender) {
        return gender == null ? null : Gender.getGender(gender);
    }

    String toGenderString(Gender gender) {
        return gender == null ? null : gender.toString();
    }

    Integer toGenderInteger(Gender gender) {
        return gender == null ? null : gender.getCode();
    }

    AuthStatus toAuthStatus(String authStatus) {
        return authStatus == null ? null : AuthStatus.getAuthStatus(authStatus);
    }

    AuthStatus toAuthStatus(Integer authStatus) {
        return authStatus == null ? null : AuthStatus.getAuthStatus(authStatus);
    }

    String toAuthStatusString(AuthStatus authStatus) {
        return authStatus == null ? null : authStatus.toString();
    }

    Integer toAuthStatusInteger(AuthStatus authStatus) {
        return authStatus == null ? null : authStatus.getCode();
    }

    PostType toPostType(String postType) {
        return postType == null ? null : PostType.getPostType(postType);
    }

    PostType toPostType(Integer postType) {
        return postType == null ? null : PostType.getPostType(postType);
    }

    String toPostTypeString(PostType postType) {
        return postType == null ? null : postType.toString();
    }

    Integer toPostTypeInteger(PostType postType) {
        return postType == null ? null : postType.getCode();
    }

    ContactType toContactType(String contactType) {
        return contactType == null ? null : ContactType.getContactType(contactType);
    }

    ContactType toContactType(Integer contactType) {
        return contactType == null ? null : ContactType.getContactType(contactType);
    }

    String toContactTypeString(ContactType contactType) {
        return contactType == null ? null : contactType.toString();
    }

    Integer toContactTypeInteger(ContactType contactType) {
        return contactType == null ? null : contactType.getCode();
    }

    RoomStatus toRoomStatus(String roomStatus) {
        return roomStatus == null ? null : RoomStatus.getRoomStatus(roomStatus);
    }

    RoomStatus toRoomStatus(Integer roomStatus) {
        return roomStatus == null ? null : RoomStatus.getRoomStatus(roomStatus);
    }

    String toRoomStatusString(RoomStatus roomStatus) {
        return roomStatus == null ? null : roomStatus.toString();
    }

    Integer toRoomStatusInteger(RoomStatus roomStatus) {
        return roomStatus == null ? null : roomStatus.getCode();
    }

    Timestamp toTimestamp(Long time) {
        return time == null ? null : new Timestamp(time);
    }

    Long toLong(Timestamp time) {
        return time == null ? null : time.getTime();
    }

    String toString(byte[] bs) {
        return bs == null ? null : new String(bs);
    }

    byte[] toBytes(String string) {
        return string == null ? null : string.getBytes();
    }

}
