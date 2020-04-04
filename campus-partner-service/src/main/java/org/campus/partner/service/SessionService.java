package org.campus.partner.service;

import javax.validation.Valid;

import org.campus.partner.pojo.bo.BoUserSession;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

/**
 * 
 * session操作接口
 *
 *
 * @author xuLiang
 * @since 1.0.0
 */
@Validated
public interface SessionService {
    /**
     * 
     * 根据用户的openid来创建session
     *
     * @param openId
     *            openid
     * @return 创建结果
     * @author xuLiang
     * @since 1.0.0
     */
    BoUserSession createUserSession(@Valid @NotBlank String openId);

    /**
     * 
     * 根据sessionId来获取一条用户session记录
     *
     * @param sessionId
     *            sessionId
     * @return 获取结果
     * @author xuLiang
     * @since 1.0.0
     */
    BoUserSession getUserSession(@Valid @NotBlank String sessionId);

    /**
     * 
     * 根据sessionId来删除一条用户session记录
     *
     * @param sessionId
     * @return 删除结果，true-成功；false-失败
     * @author xuLiang
     * @since 1.0.0
     */
    Boolean deleteUserSession(@Valid @NotBlank String sessionId);

}
