package org.campus.partner.service;

import java.util.List;

import org.campus.partner.pojo.bo.SmsMultiVars;
import org.campus.partner.pojo.bo.SmsVars;
import org.campus.partner.util.enums.PostType;

/**
 * 
 * 短信发送服务
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
public interface SmsService {
    /**
     * 
     * 发送单条短信
     *
     * @param to
     *            收信人
     * @param postType
     *            此次结伴的类型
     * @param vars
     *            短信参数
     * @author xuLiang
     * @since 1.0.0
     */
    void sendSingleSms(String to, PostType postType, SmsVars vars);

    /**
     * 
     * 批量发送短信
     *
     * @param postType
     *            此次结伴的类型
     * @param multi
     *            短信参数
     * @author xuLiang
     * @since 1.0.0
     */
    void sendMultiSms(PostType postType, List<SmsMultiVars> multi);
}
