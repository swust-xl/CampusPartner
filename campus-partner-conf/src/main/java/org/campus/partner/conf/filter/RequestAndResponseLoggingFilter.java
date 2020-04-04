package org.campus.partner.conf.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.campus.partner.util.string.RandomStringMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

/**
 * 基于Spring的OncePerRequestFilter实现HTTP请求和响应日志的记录的过滤器.<br/>
 * 
 * 参见：<a href=
 * 'https://gist.github.com/int128/e47217bebdb4c402b2ffa7cc199307ba'>https://gist.github.com/int128/e47217bebdb4c402b2ffa7cc199307ba</a>
 *
 * @see org.springframework.web.filter.AbstractRequestLoggingFilter
 * @see ContentCachingRequestWrapper
 * @see ContentCachingResponseWrapper
 * @author Hidetake Iwata
 * @author xl
 * @since 1.3.5
 */
public class RequestAndResponseLoggingFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestAndResponseLoggingFilter.class);
    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"), MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA);

    /**
     * 是否禁用请求日志输出.<br/>
     * 默认：false
     * 
     * @since 1.3.5
     */
    private boolean disableRequestLogging;
    /**
     * 是否禁用响应日志输出.<br/>
     * 默认：false
     * 
     * @since 1.3.5
     */
    private boolean disableResponseLogging;

    /**
     * 是否禁用Multipart日志输出.<br/>
     * 默认：true
     *
     * @since 1.3.5
     */
    private boolean disableMultipartContentLogging = true;
    /**
     * 最大允许请求数据内容日志输出，单位：字节.<br/>
     * 默认：500KB
     *
     * @since 1.3.5
     */
    private int maxRequestContentLogging = 500 * 1024;
    /**
     * 最大允许响应数据内容日志输出，单位：字节.<br/>
     * 默认：1024KB=1MB
     *
     * @since 1.3.5
     */
    private int maxResponseContentLogging = 1024 * 1024;

    /**
     * 默认构造方法.
     *
     */
    public RequestAndResponseLoggingFilter() {}

    /**
     * 设置是否禁用的构造方法.
     *
     * @param disableRequestLogging
     *            是否禁用请求日志输出
     * @param disableResponseLogging
     *            是否禁用响应日志输出
     */
    public RequestAndResponseLoggingFilter(boolean disableRequestLogging, boolean disableResponseLogging) {
        this.disableRequestLogging = disableRequestLogging;
        this.disableResponseLogging = disableResponseLogging;
    }

    /**
     * 完整构造方法.
     *
     * @param disableRequestLogging
     *            是否禁用请求日志输出
     * @param disableResponseLogging
     *            是否禁用响应日志输出
     * @param disableMultipartContentLogging
     *            是否禁用Multipart日志输出
     * @param maxRequestContentLogging
     *            最大允许请求数据内容日志输出，单位：字节
     * @param maxResponseContentLogging
     *            最大允许响应数据内容日志输出，单位：字节
     */
    public RequestAndResponseLoggingFilter(boolean disableRequestLogging, boolean disableResponseLogging,
            boolean disableMultipartContentLogging, int maxRequestContentLogging, int maxResponseContentLogging) {
        this.disableRequestLogging = disableRequestLogging;
        this.disableResponseLogging = disableResponseLogging;
        this.disableMultipartContentLogging = disableMultipartContentLogging;
        this.maxRequestContentLogging = maxRequestContentLogging;
        this.maxResponseContentLogging = maxResponseContentLogging;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        boolean notFilter = isAsyncDispatch(request) || (disableRequestLogging && disableResponseLogging);
        if (notFilter) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    protected void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            FilterChain filterChain) throws ServletException, IOException {
        String rdm = null;
        try {
            if (!disableRequestLogging) {
                rdm = RandomStringMaker.get8FixedStr();
                beforeRequest(request, response, rdm);
            }
            filterChain.doFilter(request, response);
        } finally {
            afterRequest(request, response, rdm == null ? RandomStringMaker.get8FixedStr() : rdm,
                    disableMultipartContentLogging, maxRequestContentLogging, maxResponseContentLogging);
            response.copyBodyToResponse();
        }
    }

    protected void beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            String currentReqAndRespRandom) {
        if (LOG.isInfoEnabled()) {
            logRequestHeader(request, request.getRemoteAddr() + "|" + currentReqAndRespRandom + "|>");
        }
    }

    protected void afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
            String currentReqAndRespRandom, boolean disableMultipartContentLogging, int maxRequestContentLogging,
            int maxResponsetContentLogging) {
        if (LOG.isInfoEnabled()) {
            String prefix = request.getRemoteAddr() + "|" + currentReqAndRespRandom;
            if (!disableRequestLogging) {
                logRequestBody(request, prefix + "|>", disableMultipartContentLogging, maxRequestContentLogging);
            }
            if (!disableResponseLogging) {
                LOG.info("{}", prefix + "|>");
                LOG.info("{} {}", prefix + "|>", "L O A D I N G...");
                LOG.info("{}", prefix + "|>");
                logResponse(response, prefix + "|<", maxResponsetContentLogging);
            }
            if (!disableRequestLogging || !disableResponseLogging) {
                LOG.info("{} {} {}", prefix + "|-", "L O G G I N G   E N D", "\r\n");
            }
        }
    }

    private static void logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        String queryString = request.getQueryString();
        if (queryString == null) {
            LOG.info("{} {} {}", prefix, request.getMethod(), request.getRequestURI());
        } else {
            LOG.info("{} {} {}?{}", prefix, request.getMethod(), request.getRequestURI(), queryString);
        }
        Collections.list(request.getHeaderNames())
                .//
                forEach(headerName -> Collections.list(request.getHeaders(headerName))//
                        .forEach(headerValue -> LOG.info("{} {}: {}", prefix, headerName, headerValue)));
        LOG.info("{}", prefix);
    }

    private static void logRequestBody(ContentCachingRequestWrapper request, String prefix,
            boolean disableMultipartContentLogging, int maxRequestContentLogging) {
        byte[] content = request.getContentAsByteArray();
        int reqContentLen = content.length;
        if (reqContentLen <= 0) {
            LOG.info("{} [no bytes request content]", prefix);
            return;
        }
        if (disableMultipartContentLogging
                && MediaType.MULTIPART_FORM_DATA.includes(MediaType.valueOf(request.getContentType()))) {
            LOG.info("{} [{} bytes multipart request content]", prefix, reqContentLen);
            return;
        }
        if (reqContentLen > maxRequestContentLogging) {
            LOG.info("{} [{} bytes request content]", prefix, reqContentLen);
            return;
        }
        logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix);
    }

    private static void logResponse(ContentCachingResponseWrapper response, String prefix,
            int maxResponseContentLogging) {
        int status = response.getStatus();
        LOG.info("{} {} {}", prefix, status, HttpStatus.valueOf(status)
                .getReasonPhrase());
        response.getHeaderNames()//
                .forEach(headerName -> response.getHeaders(headerName)//
                        .forEach(headerValue -> LOG.info("{} {}: {}", prefix, headerName, headerValue)));
        byte[] content = response.getContentAsByteArray();
        int respLen = content.length;
        LOG.info("{} {}: {}", prefix, HttpHeaders.CONTENT_LENGTH, respLen);
        LOG.info("{}", prefix);
        if (respLen <= 0) {
            LOG.info("{} [no bytes response content]", prefix);
        } else if (respLen > maxResponseContentLogging) {
            LOG.info("{} [{} bytes response content]", prefix, respLen);
        } else {
            logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix);
        }
        LOG.info("{}", prefix);
    }

    private static void logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        MediaType mediaType = MediaType.valueOf(contentType);
        boolean visible = VISIBLE_TYPES.stream()
                .anyMatch(visibleType -> visibleType.includes(mediaType));
        if (visible) {
            try {
                String contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r|\n"))
                        .forEach(line -> LOG.info("{} {}", prefix, line));
            } catch (UnsupportedEncodingException e) {
                LOG.info("{} [{} bytes response content]", prefix, content.length);
            }
        } else {
            LOG.info("{} [{} bytes response content]", prefix, content.length);
        }
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    /**
     * 获取disableRequestLogging属性字段的值.
     *
     * @return 类型为boolean的disableRequestLogging属性字段的值.
     */
    public boolean isDisableRequestLogging() {
        return disableRequestLogging;
    }

    /**
     * 设置disableRequestLogging属性字段的值.
     *
     * @param disableRequestLogging
     *            待设置类型为boolean的disableRequestLogging属性字段的值.
     */
    public void setDisableRequestLogging(boolean disableRequestLogging) {
        this.disableRequestLogging = disableRequestLogging;
    }

    /**
     * 获取disableResponseLogging属性字段的值.
     *
     * @return 类型为boolean的disableResponseLogging属性字段的值.
     */
    public boolean isDisableResponseLogging() {
        return disableResponseLogging;
    }

    /**
     * 设置disableResponseLogging属性字段的值.
     *
     * @param disableResponseLogging
     *            待设置类型为boolean的disableResponseLogging属性字段的值.
     */
    public void setDisableResponseLogging(boolean disableResponseLogging) {
        this.disableResponseLogging = disableResponseLogging;
    }

    /**
     * 获取disableMultipartContentLogging属性字段的值.
     *
     * @return 类型为boolean的disableMultipartContentLogging属性字段的值.
     */
    public boolean isDisableMultipartContentLogging() {
        return disableMultipartContentLogging;
    }

    /**
     * 设置disableMultipartContentLogging属性字段的值.
     *
     * @param disableMultipartContentLogging
     *            待设置类型为boolean的disableMultipartContentLogging属性字段的值.
     */
    public void setDisableMultipartContentLogging(boolean disableMultipartContentLogging) {
        this.disableMultipartContentLogging = disableMultipartContentLogging;
    }

    /**
     * 获取maxRequestContentLogging属性字段的值.
     *
     * @return 类型为long的maxRequestContentLogging属性字段的值.
     */
    public int getMaxRequestContentLogging() {
        return maxRequestContentLogging;
    }

    /**
     * 设置maxRequestContentLogging属性字段的值.
     *
     * @param maxRequestContentLogging
     *            待设置类型为long的maxRequestContentLogging属性字段的值.
     */
    public void setMaxRequestContentLogging(int maxRequestContentLogging) {
        this.maxRequestContentLogging = maxRequestContentLogging;
    }

    /**
     * 获取maxResponseContentLogging属性字段的值.
     *
     * @return 类型为long的maxResponseContentLogging属性字段的值.
     */
    public int getMaxResponseContentLogging() {
        return maxResponseContentLogging;
    }

    /**
     * 设置maxResponseContentLogging属性字段的值.
     *
     * @param maxResponseContentLogging
     *            待设置类型为long的maxResponseContentLogging属性字段的值.
     */
    public void setMaxResponseContentLogging(int maxResponseContentLogging) {
        this.maxResponseContentLogging = maxResponseContentLogging;
    }

}