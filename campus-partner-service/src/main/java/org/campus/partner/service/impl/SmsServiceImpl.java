package org.campus.partner.service.impl;

import java.util.List;

import org.campus.partner.pojo.bo.SmsMultiParamReq;
import org.campus.partner.pojo.bo.SmsMultiVars;
import org.campus.partner.pojo.bo.SmsSingleParamReq;
import org.campus.partner.pojo.bo.SmsSucceedResp;
import org.campus.partner.pojo.bo.SmsVars;
import org.campus.partner.service.SmsService;
import org.campus.partner.util.enums.PostType;
import org.campus.partner.util.io.FileHandler;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.type.NamingStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 
 * 短信发送服务
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@Service
public class SmsServiceImpl implements SmsService {

    private static final Logger LOG = LoggerFactory.getLogger(SmsServiceImpl.class);

    @Value("${submail.single.request.url}")
    private String SINGLE_REQUEST_URL;
    @Value("${submail.multi.request.url}")
    private String MULTI_REQUEST_URL;
    @Value("${submail.appid}")
    private String APP_ID;
    @Value("${submail.signature}")
    private String SIGNATURE;
    @Value("${sports.project.id}")
    private String SPORTS_PROJECT_ID;
    @Value("${study.project.id}")
    private String STUDY_PROJECT_ID;
    @Value("${travel.project.id}")
    private String TRAVEL_PROJECT_ID;
    @Value("${transport.project.id}")
    private String TRANSPORT_PROJECT_ID;

    /**
     * 创建发送单条短信所需的json数据
     */
    private String createSingleRequestJson(String to, SmsVars vars, PostType postType) {
        SmsSingleParamReq req = new SmsSingleParamReq();
        req.setAppid(APP_ID);
        req.setSignature(SIGNATURE);
        req.setTo(to);
        switch (postType) {
        case SPORTS:
            req.setProject(SPORTS_PROJECT_ID);
            break;
        case STUDY:
            req.setProject(STUDY_PROJECT_ID);
            break;
        case TRAVEL:
            req.setProject(TRAVEL_PROJECT_ID);
            break;
        case TRANSPORT:
            req.setProject(TRANSPORT_PROJECT_ID);
            break;
        default:
        case UNKNOWN:
            throw new IllegalArgumentException("不支持的结伴类型");
        }
        req.setVars(vars);
        return JsonConverter.encodeAsString(req);
    }

    /**
     * 创建批量发送短信所需的json数据
     */
    private String createMultiRequestJson(PostType postType, List<SmsMultiVars> multi) {
        SmsMultiParamReq req = new SmsMultiParamReq();
        req.setAppid(APP_ID);
        req.setSignature(SIGNATURE);
        switch (postType) {
        case SPORTS:
            req.setProject(SPORTS_PROJECT_ID);
            break;
        case STUDY:
            req.setProject(STUDY_PROJECT_ID);
            break;
        case TRAVEL:
            req.setProject(TRAVEL_PROJECT_ID);
            break;
        case TRANSPORT:
            req.setProject(TRANSPORT_PROJECT_ID);
            break;
        default:
        case UNKNOWN:
            throw new IllegalArgumentException("不支持的结伴类型");
        }
        req.setMulti(multi);
        return JsonConverter.encodeAsString(req);
    }

    // {
    // "status":"success",
    // "to":"15*********",
    // "send_id":"093c0a7df143c087d6cba9cdf0cf3738",
    // "fee":1,
    // "sms_credits":14197
    // }
    // {
    // "status":"error",
    // "to":"15*********",
    // "code":1xx,
    // "msg":"error message",
    // }
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendSingleSms(String to, PostType postType, SmsVars vars) {
        SmsSucceedResp resp = JsonConverter.decodeAsBean(new String(
                FileHandler.postDataWithUrl(SINGLE_REQUEST_URL, createSingleRequestJson(to, vars, postType), null)),
                NamingStyle.SNAKE, SmsSucceedResp.class);
        if (resp == null) {
            throw new RuntimeException(String.format("[%s]短信发送失败", to));
        }
        if (resp.getStatus()
                .equals("success")) {
            LOG.info("[{}]短信发送成功", to);
        }
    }

    // [
    // {
    // "status":"success",
    // "to":"15*********",
    // "send_id":"093c0a7df143c087d6cba9cdf0cf3738",
    // "fee":1,
    // "sms_credits":14197
    // },{
    // "status":"success",
    // "to":"18*********",
    // "send_id":"093c0a7df143c087d6cba9cdf0cf3738",
    // "fee":1,
    // "sms_credits":14196
    // }
    // ]
    /**
     * {@inheritDoc}
     */
    @Override
    public void sendMultiSms(PostType postType, List<SmsMultiVars> multi) {
        List<SmsSucceedResp> resp = JsonConverter.decodeAsList(
                new String(
                        FileHandler.postDataWithUrl(MULTI_REQUEST_URL, createMultiRequestJson(postType, multi), null)),
                NamingStyle.SNAKE, SmsSucceedResp.class);
        if (resp == null) {// 抛异常后房间不会被设置为关闭，继续等待下一轮短信发送
            throw new RuntimeException("短信发送失败");
        }
        resp.forEach(e -> {
            if (e.getStatus()
                    .equals("success")) {
                LOG.info("短信发送成功");
            }
        });

    }

}
