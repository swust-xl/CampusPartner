package org.campus.partner.pojo.vo.resp;

import org.campus.partner.util.enums.UserCode;
import org.springframework.hateoas.ResourceSupport;

/**
 * 
 * 通用响应
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public class CommonResp extends ResourceSupport {

    private Integer code;

    private String message;

    public CommonResp() {}

    public CommonResp(UserCode userCode) {
        this.code = userCode.getCode();
        this.message = userCode.getMessage();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
