package org.campus.partner.util;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 异常格式化工具.
 * 
 * @author xl
 * @since 1.0.0
 */
public class ExceptionFormater {
    private static final Set<String> TRACE_CAPTURE_KEY_WORDS = new CopyOnWriteArraySet<String>();

    /**
     * 默认格式化模板格式：
     * 
     * <pre>
     * |-- DATETIME     : %s
     * |-- FILE NAME    : %s
     * |-- CLASS NAME   : %s
     * |-- METHOD NAME  : %s
     * |-- LINE NUMBER  : %d
     * |-- MESSAGE      : %s
     * |-- STACK TRACE  :
     * |                ^
     * |                | %s.%s():%d%n
     * </pre>
     */
    private static final String DEFAULT_TRACE_DATE_FORMAT_TEMPLATE = "%n|-- DATETIME     : %s";
    private static final String DEFAULT_TRACE_FILE_NAME_FORMAT_TEMPLATE = "%n|-- FILE NAME    : %s";
    private static final String DEFAULT_TRACE_CLASS_NAME_FORMAT_TEMPLATE = "%n|-- CLASS NAME   : %s";
    private static final String DEFAULT_TRACE_METHOD_NAME_FORMAT_TEMPLATE = "%n|-- METHOD NAME  : %s";
    private static final String DEFAULT_TRACE_LINE_NUMBER_FORMAT_TEMPLATE = "%n|-- LINE NUMBER  : %d";
    private static final String DEFAULT_TRACE_MESSAGE_FORMAT_TEMPLATE = "%n|-- MESSAGE      : %s";
    private static final String DEFAULT_TRACE_FORMAT_TEMPLATE = "%n|-- STACK TRACE  :" + "%n|                ^";
    private static final String DEFAULT_TRACE_BASE_FORMAT_TEMPLATE = DEFAULT_TRACE_DATE_FORMAT_TEMPLATE
            + DEFAULT_TRACE_FILE_NAME_FORMAT_TEMPLATE + DEFAULT_TRACE_CLASS_NAME_FORMAT_TEMPLATE
            + DEFAULT_TRACE_METHOD_NAME_FORMAT_TEMPLATE + DEFAULT_TRACE_LINE_NUMBER_FORMAT_TEMPLATE
            + DEFAULT_TRACE_MESSAGE_FORMAT_TEMPLATE + DEFAULT_TRACE_FORMAT_TEMPLATE;
    private static final String DEFAULT_TRACE_DETAIL_FORMAT_TEMPLATE = "%n|                | %s.%s():%d";
    static {
        TRACE_CAPTURE_KEY_WORDS.add("cn.signit");
    }

    /**
     * 获取已追踪的关键词集合.
     *
     * @return 返回已追踪的关键词集合
     * @author xl
     * @since 1.0.0
     */
    public static Set<String> getTraceCaptureKeyWords() {
        return TRACE_CAPTURE_KEY_WORDS;
    }

    /**
     * 默认格式化异常消息为格式 - 时间：
     * 
     * <pre>
     * |-- DATETIME     : %s
     * </pre>
     */
    private static String formatDatetime() {
        return new Date().toString();
    }

    /**
     * 默认格式化异常消息为格式 - 类型文件名：
     * 
     * <pre>
     * |-- FILE NAME    : %s
     * </pre>
     */
    private static String formatFileName(StackTraceElement ste) {
        return Validator.isEmpty(ste.getFileName()) ? "" : ste.getFileName();
    }

    /**
     * 默认格式化异常消息为格式 - 类型名：
     * 
     * <pre>
     * |-- CLASS NAME   : %s
     * </pre>
     */
    private static String formatClassName(StackTraceElement ste) {
        return Validator.isEmpty(ste.getClassName()) ? "" : ste.getClassName();
    }

    /**
     * 默认格式化异常消息为格式 - 方法名：
     * 
     * <pre>
     * |-- METHOD NAME  : %s
     * </pre>
     */
    private static String formatMethodName(StackTraceElement ste) {
        return Validator.isEmpty(ste.getMethodName()) ? "" : ste.getMethodName();
    }

    /**
     * 默认格式化异常消息为格式 - 行数：
     * 
     * <pre>
     * |-- LINE NUMBER  : %d
     * </pre>
     */
    private static int formatLineNumber(StackTraceElement ste) {
        return ste.getLineNumber();
    }

    /**
     * 默认格式化异常消息为格式 - 异常消息：
     * 
     * <pre>
     * |-- MESSAGE      : %s
     * </pre>
     */
    private static String formatMessage(Throwable e) {
        return e.getClass()
                .getName()
                .concat(": ")
                .concat(String.valueOf(e.getLocalizedMessage()));
    }

    /**
     * 默认格式化异常消息为格式：
     * 
     * <pre>
     * |-- DATETIME     : %s
     * |-- FILE NAME    : %s
     * |-- CLASS NAME   : %s
     * |-- METHOD NAME  : %s
     * |-- LINE NUMBER  : %d
     * |-- MESSAGE      : %s
     * |-- STACK TRACE  :
     * |                ^
     * </pre>
     */
    private static String formatTraceBase(Throwable e) {
        if (e == null || e.getStackTrace().length <= 0) {
            return "";
        }
        StackTraceElement ste = e.getStackTrace()[0];
        return String.format(DEFAULT_TRACE_BASE_FORMAT_TEMPLATE, formatDatetime(), formatFileName(ste),
                formatClassName(ste), formatMethodName(ste), formatLineNumber(ste), formatMessage(e));
    }

    /**
     * 默认格式化异常消息为格式：
     * 
     * <pre>
     * |                | %s.%s():%d
     * </pre>
     */
    private static String formatTraceDetail(Throwable e) {
        if (e == null || e.getStackTrace().length <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement ste : e.getStackTrace()) {
            String steStr = ste.toString();
            for (String ks : TRACE_CAPTURE_KEY_WORDS) {
                if (steStr.indexOf(ks) >= 0) {
                    sb.append(String.format(DEFAULT_TRACE_DETAIL_FORMAT_TEMPLATE,
                            simplifyClassName(formatClassName(ste)), formatMethodName(ste), formatLineNumber(ste)));
                    break;
                }
            }
        }
        return sb.toString();
    }

    /**
     * 简化类型名称
     * 
     * <pre>
     *  ExceptionFormater -> ExceptionFormater
     *  
     *  cn.signit.wesign.lib.common.util.ExceptionFormater -> c.s.w.l.c.u.ExceptionFormater
     * </pre>
     */
    private static String simplifyClassName(String className) {
        if (Validator.isEmpty(className) || className.indexOf(".") <= 0) {
            return className;
        }
        int idx = className.lastIndexOf(".");
        String extractSimpleName = className.substring(++idx);
        String extractPackgeName = className.substring(0, idx);
        String[] pkgPaths = extractPackgeName.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String pkgPath : pkgPaths) {
            sb.append(pkgPath.charAt(0))
                    .append('.');
        }
        return sb.append(extractSimpleName)
                .toString();
    }

    /**
     * 格式化异常消息.<br/>
     * 注：默认追踪的异常堆栈消息中包含的关键词: <code>cn.signit</code> <br/>
     * 默认格式化后的消息形如：
     * 
     * <pre>
     * |-- DATETIME     : Sat Dec 23 16:44:09 CST 2017
     * |-- FILE NAME    : ExceptionFormater.java
     * |-- CLASS NAME   : cn.signit.wesign.lib.common.util.ExceptionFormater
     * |-- METHOD NAME  : say
     * |-- LINE NUMBER  : 233
     * |-- MESSAGE      : java.lang.RuntimeException: zzz
     * |-- STACK TRACE  :
     * |                ^
     * |                | c.s.w.l.c.u.ExceptionFormater.say():233
     * |                | c.s.w.l.c.u.ExceptionFormater.test():229
     * |                | c.s.w.l.c.u.ExceptionFormater.main():249
     * </pre>
     * 
     * @param e
     *            抛出的异常对象
     * @return 格式化后的异常消息
     * @author xl
     * @since 1.0.0
     */
    public static String format(Throwable e) {
        return formatTraceBase(e).concat(formatTraceDetail(e));
    }

    /**
     * 格式化异常消息.<br/>
     * <br/>
     * 默认格式化后的消息形如：
     * 
     * <pre>
     * |-- DATETIME     : Sat Dec 23 16:44:09 CST 2017
     * |-- FILE NAME    : ExceptionFormater.java
     * |-- CLASS NAME   : cn.signit.wesign.lib.common.util.ExceptionFormater
     * |-- METHOD NAME  : say
     * |-- LINE NUMBER  : 233
     * |-- MESSAGE      : java.lang.RuntimeException: zzz
     * |-- STACK TRACE  :
     * |                ^
     * |                | c.s.w.l.c.u.ExceptionFormater.say():233
     * |                | c.s.w.l.c.u.ExceptionFormater.test():229
     * |                | c.s.w.l.c.u.ExceptionFormater.main():249
     * </pre>
     * 
     * @param e
     *            抛出的异常对象
     * @param traceCaptureKeyWords
     *            定义需要追踪的异常堆栈消息中包含的关键词
     * @return 格式化后的异常消息
     * @author xl
     * @since 1.0.0
     */
    public static String format(Throwable e, String... traceCaptureKeyWords) {
        if (!Validator.isEmpty(traceCaptureKeyWords)) {
            TRACE_CAPTURE_KEY_WORDS.addAll(Arrays.asList(traceCaptureKeyWords));
        }
        return formatTraceBase(e).concat(formatTraceDetail(e));
    }
}
