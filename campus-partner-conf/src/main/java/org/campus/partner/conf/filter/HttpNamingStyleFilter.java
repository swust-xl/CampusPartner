package org.campus.partner.conf.filter;

import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.Validator;
import org.campus.partner.util.enums.HttpKeys;
import org.campus.partner.util.io.FileHandler;
import org.campus.partner.util.string.Case;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.type.NamingStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 添加HTTP请求的的命名风格的拦截器.
 *
 * @see {@link org.springframework.web.filter.OncePerRequestFilter}.
 * @author xl
 * @since 1.2.3
 */
public class HttpNamingStyleFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(HttpNamingStyleFilter.class);
    private AbstractJackson2HttpMessageConverter jackson2HttpMessageConverter;
    private Set<NamingStyle> parameterNamingStyles = new HashSet<NamingStyle>(
            Arrays.asList(NamingStyle.CAMEL, NamingStyle.SNAKE));

    private NamingStyle jsonResponseBodyNamingStyle = NamingStyle.CAMEL;

    /**
     * 
     * 默认构造方法.
     * 
     * @param jackson2HttpMessageConverter
     *            HTTP消息的JSON转换对象
     *
     */
    public HttpNamingStyleFilter(AbstractJackson2HttpMessageConverter jackson2HttpMessageConverter) {
        this.jackson2HttpMessageConverter = jackson2HttpMessageConverter;
    }

    /**
     * 
     * 设置请求参数的命名风格的构造方法.
     * 
     * @param jackson2HttpMessageConverter
     *            HTTP消息的JSON转换对象
     * @param parameterNamingStyles
     *            系统支持的HTTP请求参数的命名风格
     *
     */
    public HttpNamingStyleFilter(AbstractJackson2HttpMessageConverter jackson2HttpMessageConverter,
            NamingStyle... parameterNamingStyles) {
        this(jackson2HttpMessageConverter);
        this.parameterNamingStyles.addAll(Arrays.asList(parameterNamingStyles));
    }

    /**
     * 
     * 设置请求参数和响应体的命名风格的构造方法.
     * 
     * @param jackson2HttpMessageConverter
     *            HTTP消息的JSON转换对象
     * @param parameterNamingStyles
     *            系统支持的HTTP请求参数的命名风格
     * @param responseBodyNamingStyle
     *            系统支持的HTTP JSON响应体的命名风格
     *
     */
    public HttpNamingStyleFilter(AbstractJackson2HttpMessageConverter jackson2HttpMessageConverter,
            List<NamingStyle> parameterNamingStyles, NamingStyle responseBodyNamingStyle) {
        this(jackson2HttpMessageConverter);
        this.parameterNamingStyles.addAll(parameterNamingStyles);
        this.jsonResponseBodyNamingStyle = responseBodyNamingStyle;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CustomRequestWrapper customRequestWrapper = new CustomRequestWrapper(request);
        preHandleRequestPath(customRequestWrapper);
        preHandleRequestQuery(customRequestWrapper);
        preHandleRequestHeader(customRequestWrapper);
        preHandleRequestBody(customRequestWrapper);
        NamingStyle namingStyle = getAcceptNamingStyle(request);
        if (namingStyle == jsonResponseBodyNamingStyle) {// 走正常过滤
            filterChain.doFilter(request, response);
        } else {// 走重命名风格过滤
            CustomResponseWrapper jsonResponseWrapper = new CustomResponseWrapper(response, namingStyle);
            filterChain.doFilter(request, jsonResponseWrapper);
            afterResponseBody(jsonResponseWrapper);
            afterResponseHeader(jsonResponseWrapper);
            afterResponseQuery(jsonResponseWrapper);
            afterResponsePath(jsonResponseWrapper);
        }
    }

    private boolean preHandleRequestPath(CustomRequestWrapper requestWrapper) {
        // do nothing
        return true;
    }

    private boolean preHandleRequestQuery(CustomRequestWrapper requestWrapper) {
        Map<String, String[]> paramMap = requestWrapper.getParameterMap();
        int size = paramMap.size();
        if (size <= 0) {
            return true;
        }
        Map<String, String[]> namingStyleParamMap = new HashMap<String, String[]>(size);
        for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
            parameterNamingStyles.parallelStream()
                    .forEach(namingStyle -> {
                        String renamedKey = Case.to(entry.getKey(), namingStyle);
                        if (paramMap.containsKey(renamedKey)) {
                            return;
                        }
                        namingStyleParamMap.put(renamedKey, entry.getValue());
                    });
        }
        requestWrapper.addParameters(namingStyleParamMap);
        return true;
    }

    private boolean preHandleRequestHeader(CustomRequestWrapper requestWrapper) {
        // do nothing
        return true;
    }

    private boolean preHandleRequestBody(CustomRequestWrapper requestWrapper) {
        if (jackson2HttpMessageConverter.getObjectMapper() == JsonConverter.getObjectMapper()) {
            LOG.debug("系统已设置由[{}]提供JSON的转换，支持[{}]命名风格的请求体， 不需要进行预处理转换请求体", JsonConverter.class, NamingStyle.values());
            return true;
        }
        jackson2HttpMessageConverter.setObjectMapper(JsonConverter.getObjectMapper());
        return true;
    }

    private void afterResponsePath(CustomResponseWrapper jsonResponseWrapper) {
        // do nothing
    }

    private void afterResponseQuery(CustomResponseWrapper jsonResponseWrapper) {
        // do nothing
    }

    private void afterResponseHeader(CustomResponseWrapper jsonResponseWrapper) {
        // do nothing
    }

    private void afterResponseBody(CustomResponseWrapper jsonResponseWrapper) {
        try {
            byte[] resultBytes = jsonResponseWrapper.getResultAsBytes();
            if (JsonConverter.isJSONValid(resultBytes)
                    && jsonResponseWrapper.getAcceptNamingStyle() != NamingStyle.CAMEL) {
                resultBytes = JsonConverter.encodeAsBytes(JsonConverter.decodeAsMap(resultBytes),
                        jsonResponseWrapper.getAcceptNamingStyle());
            } else {
                LOG.debug("检查并自动去除JSON结尾的字符串");
                resultBytes = autoCheckAndRemoveJSONTails(resultBytes);
            }
            FileHandler.writeBytes2Stream(resultBytes, jsonResponseWrapper.getResponse()
                    .getOutputStream());
        } catch (IOException e) {
            LOG.warn("响应结构体的命名风格重新生成时异常，异常描述：{}", ExceptionFormater.format(e));
            throw new RuntimeException(e);
        }
    }

    // 检查并自动去除JSON结尾的字符串组
    private byte[] autoCheckAndRemoveJSONTails(byte[] resultBytes) {
        int endIdx = resultBytes.length - 1;
        if (resultBytes[endIdx] == 125) {// 包含“}”
            int startIdx = endIdx;
            int rgtCBCnt = 1;
            int lftCBCnt = 0;
            for (int i = endIdx; i >= 0; i--) {
                byte charVal = resultBytes[--startIdx];
                if (charVal == 123) {// 包含“{”
                    lftCBCnt++;
                } else if (charVal == 125) {// 包含“}”
                    rgtCBCnt++;
                } else {
                    continue;
                }
                if (rgtCBCnt == lftCBCnt) {
                    byte[] subBytes = new byte[endIdx - startIdx + 1];
                    System.arraycopy(resultBytes, startIdx, subBytes, 0, subBytes.length);
                    if (JsonConverter.isJSONValid(subBytes)) {
                        subBytes = new byte[startIdx];
                        System.arraycopy(resultBytes, 0, subBytes, 0, subBytes.length);
                        return autoCheckAndRemoveJSONTails(subBytes);// 递归调用，因为结尾可能出现多组JSON字符串
                    }
                }
            }
        }
        return resultBytes;
    }
    // private NamingStyle getRequestedNamingStyle(CustomRequestWrapper
    // requestWrapper) {
    // return
    // parseNamingStyle(requestWrapper.getHeader(HttpKeys.HEADER_X_REQUESTED_NAMING_STYLE));
    // }

    private NamingStyle getAcceptNamingStyle(HttpServletRequest request) {
        return parseNamingStyle(request.getHeader(HttpKeys.HEADER_ACCEPT_NAMING_STYLE));
    }

    private NamingStyle parseNamingStyle(String namingStyle) {
        if (Validator.isEmpty(namingStyle)) {
            return jsonResponseBodyNamingStyle;
        }
        for (NamingStyle val : NamingStyle.values()) {
            if (val.name()
                    .equalsIgnoreCase(namingStyle)) {
                return val;
            }
        }
        return jsonResponseBodyNamingStyle;
    }

    /**
     * 自定义HTTP请求封装器，允许修改HTTP请求参数.
     * 
     * @author xl
     * @since 1.2.3
     */
    public static class CustomRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest request;

        private Map<String, String[]> parameterMap;

        /**
         * 自定义HTTP请求封装器构造方法.<br/>
         * <br/>
         * Constructs a request object wrapping the given request.
         *
         * @param request
         *            The request to wrap
         *
         * @throws java.lang.IllegalArgumentException
         *             if the request is null
         */
        public CustomRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        /**
         * 添加新的请求参数.
         *
         * @param name
         *            请求参数名
         * @param value
         *            请求参数值
         * @author xl
         * @since 1.2.3
         */
        public void addParameter(String name, String value) {
            Map<String, String[]> newParameterMap = new HashMap<String, String[]>(1);
            newParameterMap.put(name, new String[] { value });
            addParameters(newParameterMap);
        }

        /**
         * 添加新的请求参数.
         *
         * @param newParameterMap
         *            新的请求参数Map
         * @author xl
         * @since 1.2.3
         */
        public void addParameters(Map<String, String[]> newParameterMap) {
            if (parameterMap == null) {
                parameterMap = new HashMap<String, String[]>();
                parameterMap.putAll(request.getParameterMap());
            }
            for (Map.Entry<String, String[]> entry : newParameterMap.entrySet()) {
                String[] values = parameterMap.get(entry.getKey());
                if (values == null) {
                    values = new String[0];
                }
                List<String> list = new ArrayList<String>(values.length + 1);
                list.addAll(Arrays.asList(values));
                list.addAll(Arrays.asList(entry.getValue()));
                parameterMap.put(entry.getKey(), list.toArray(new String[list.size()]));
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getParameter(String name) {
            if (parameterMap == null) {
                return request.getParameter(name);
            }
            String[] strings = parameterMap.get(name);
            if (strings != null) {
                return strings[0];
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, String[]> getParameterMap() {
            if (parameterMap == null) {
                return request.getParameterMap();
            }
            return Collections.unmodifiableMap(parameterMap);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Enumeration<String> getParameterNames() {
            if (parameterMap == null) {
                return request.getParameterNames();
            }
            return Collections.enumeration(parameterMap.keySet());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String[] getParameterValues(String name) {
            if (parameterMap == null) {
                return request.getParameterValues(name);
            }
            return parameterMap.get(name);
        }

    }

    /**
     * 自定义HTTP响应封装器，允许修改HTTP响应参数.
     * 
     * @author xl
     * @since 1.2.3
     */
    public static class CustomResponseWrapper extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream capture;
        private ServletOutputStream output;
        private PrintWriter writer;

        private NamingStyle acceptNamingStyle = NamingStyle.CAMEL;

        /**
         * 自定义HTTP JSON响应封装器构造方法.<br/>
         * <br/>
         * 
         * Constructs a response adaptor wrapping the given response.
         *
         * @param response
         *            The response to be wrapped
         *
         * @throws java.lang.IllegalArgumentException
         *             if the response is null
         */
        public CustomResponseWrapper(HttpServletResponse response) {
            super(response);
            this.capture = new ByteArrayOutputStream(response.getBufferSize());
        }

        /**
         * 自定义HTTP JSON响应封装器并允许设置响应体的命名风格构造方法.<br/>
         * <br/>
         * 
         * Constructs a response adaptor wrapping the given response.
         *
         * @param response
         *            The response to be wrapped
         * @param acceptNamingStyle
         *            期望接受的命名风格响应体
         *
         * @throws java.lang.IllegalArgumentException
         *             if the response is null
         */
        public CustomResponseWrapper(HttpServletResponse response, NamingStyle acceptNamingStyle) {
            this(response);
            this.acceptNamingStyle = acceptNamingStyle;
        }

        /**
         * 
         * 响应的可接受的命名风格类型.
         *
         * @return 命名风格类型枚举
         * @author xl
         * @since 1.2.3
         */
        public NamingStyle getAcceptNamingStyle() {
            return acceptNamingStyle;
        }

        /**
         * 设置响应的可接受的命名风格类型.
         *
         * @param acceptNamingStyle
         *            名风格类型枚举
         * @author xl
         * @since 1.2.3
         */
        public void setAcceptNamingStyle(NamingStyle acceptNamingStyle) {
            this.acceptNamingStyle = acceptNamingStyle;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ServletOutputStream getOutputStream() {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }
            if (output == null) {
                output = new ServletOutputStream() {
                    @Override
                    public void write(int b) throws IOException {
                        capture.write(b);
                    }

                    @Override
                    public void flush() throws IOException {
                        capture.flush();
                    }

                    @Override
                    public void close() throws IOException {
                        capture.close();
                    }

                    @Override
                    public boolean isReady() {
                        return false;
                    }

                    @Override
                    public void setWriteListener(WriteListener arg0) {}
                };
            }
            return output;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PrintWriter getWriter() throws IOException {
            if (output != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(capture, getCharacterEncoding()));
            }
            return writer;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void flushBuffer() throws IOException {
            super.flushBuffer();
            if (writer != null) {
                writer.flush();
            } else if (output != null) {
                output.flush();
            }
        }

        /**
         * 获取响应体数据字符串表示.
         *
         * @return 字符串表示的响应体
         * @author xl
         * @since 1.2.3
         */
        public String getResultAsString() {
            try {
                return new String(getResultAsBytes(), getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                LOG.warn(ExceptionFormater.format(e));
                return "";
            }
        }

        /**
         * 获取响应体数据字节数组.
         *
         * @return 获取响应体数据字符串表示字节数组
         * @author xl
         * @since 1.2.3
         */
        public byte[] getResultAsBytes() {
            try {
                flushBuffer();
                if (writer != null) {
                    writer.close();
                } else if (output != null) {
                    output.close();
                }
                return capture.toByteArray();
            } catch (IOException e) {
                LOG.warn(ExceptionFormater.format(e));
                return "".getBytes();
            }
        }
    }
}
