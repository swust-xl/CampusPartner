package org.campus.partner.app.controller.impl;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.campus.partner.pojo.vo.resp.VoErrorResp;
import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.Validator;
import org.campus.partner.util.id.FlakeIdProvider.StringUtil;
import org.campus.partner.util.time.StandardTimes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.http.HttpStatus;

/**
 * 
 * 异常处理
 *
 *
 * @author xuLiang
 * @since 1.2.0
 */
@RestControllerAdvice
public class ExecptionController {

    private static final Logger LOG = LoggerFactory.getLogger(ExecptionController.class);

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private StandardTimes standardTimes;

    /**
     * 空指针处理
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleNullpointerException(NullPointerException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "数据对象为空");
    }

    /**
     * 参数不合法处理
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleIllegalArgumentExceptionException(IllegalArgumentException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "参数错误");
    }

    /**
     * 参数校验不合法处理 {@link javax.validation.ConstraintViolationException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleConstraintViolationExceptionException(ConstraintViolationException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), getFriendlyDeveloperMessage(e));
    }

    /**
     * {@code @valid}参数校验不合法处理
     * {@link org.springframework.web.bind.MethodArgumentNotValidException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "参数校验失败");
    }

    /**
     * 不支持的HTTP请求方法处理
     * {@link org.springframework.web.HttpRequestMethodNotSupportedException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    private VoErrorResp handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.METHOD_NOT_ALLOWED, request.getRequestURI(), "不支持的HTTP请求方法");
    }

    /**
     * 404处理 {@link org.springframework.web.servlet.NoHandlerFoundException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private VoErrorResp handleNoHandlerFoundException(NoHandlerFoundException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.NOT_FOUND, request.getRequestURI(), "请求资源的地址不存在");
    }

    /**
     * 缺少请求参数处理
     * {@link org.springframework.web.bind.MissingServletRequestParameterException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "缺少请求参数");
    }

    /**
     * 请求体Content-type不匹配处理
     * {@link org.springframework.web.HttpMediaTypeNotSupportedException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    private VoErrorResp handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request.getRequestURI(), "请求的数据类型不支持");
    }

    /**
     * 参数类型不匹配处理 {@link org.springframework.beans.TypeMismatchException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleTypeMismatch(TypeMismatchException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "属性类型不匹配");
    }

    /**
     * 不能产生客户端需要的contentType处理
     * {@link org.springframework.web.HttpMediaTypeNotAcceptableException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    private VoErrorResp handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.NOT_ACCEPTABLE, request.getRequestURI(), "无法生成客户端可接受数据类型");
    }

    /**
     * URI地址参数缺失处理
     * {@link org.springframework.web.bind.MissingPathVariableException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(MissingPathVariableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleMissingPathVariable(MissingPathVariableException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "请求资源的路径参数缺失");
    }

    /**
     * 数据转换异常处理
     * {@link org.springframework.beans.ConversionNotSupportedException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(ConversionNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleConversionNotSupported(ConversionNotSupportedException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(), "数据转换异常");
    }

    /**
     * multipart/form-data数据异常处理
     * {@link org.springframework.web.multipart.support.MissingServletRequestPartException}
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private VoErrorResp handleMissingServletRequestPart(MissingServletRequestPartException e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        return createErrorResp(e, HttpStatus.BAD_REQUEST, request.getRequestURI(),
                "无法处理客户端发送的'multipart/form-data'类型数据");
    }

    /**
     * 捕获异常的最后一道防线
     * 
     * @author xuLiang
     * @since 1.0.0
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public VoErrorResp handleRestNotExpectedGlobalException(Throwable e) {
        LOG.error(ExceptionFormater.format(e));
        e.printStackTrace();
        VoErrorResp voErrorResp = new VoErrorResp();
        voErrorResp.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        voErrorResp.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        // 此处禁止直接把异常信息返回给客户端
        voErrorResp.setMessage("未知异常，请联系程序员");
        voErrorResp.setPath(request.getRequestURI());
        voErrorResp.setTimestamp(standardTimes.getStandardDate()
                .getTime());
        return voErrorResp;
    }

    // 创建错误响应信息
    private VoErrorResp createErrorResp(Throwable e, HttpStatus httpStatus, String url, String defaultMessage) {
        VoErrorResp voErrorResp = new VoErrorResp();
        voErrorResp.setStatus(httpStatus == null ? HttpStatus.BAD_REQUEST.value() : httpStatus.value());
        voErrorResp
                .setError(httpStatus == null ? HttpStatus.BAD_REQUEST.getReasonPhrase() : httpStatus.getReasonPhrase());
        voErrorResp.setMessage(StringUtil.isEmpty(e.getMessage()) ? defaultMessage : e.getMessage());
        voErrorResp.setPath(StringUtil.isEmpty(url) ? null : url);
        voErrorResp.setTimestamp(standardTimes.getStandardDate()
                .getTime());
        return voErrorResp;
    }

    // 抽取友好的开发者异常描述信息(针对ConstraintViolationException)
    private String getFriendlyDeveloperMessage(Throwable e) {
        if (e instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> cvSet = ((ConstraintViolationException) e).getConstraintViolations();
            if (!Validator.isEmpty(cvSet)) {
                ConstraintViolation<?> cv = cvSet.iterator()
                        .next();
                return cv.getPropertyPath() + " " + cv.getMessage();
            }
        }
        return e.getMessage();
    }
}
