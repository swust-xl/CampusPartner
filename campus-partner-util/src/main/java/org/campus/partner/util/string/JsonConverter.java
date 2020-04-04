package org.campus.partner.util.string;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.Validator;
import org.campus.partner.util.type.NamingStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * 基于jackson的Json转换工具.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public final class JsonConverter {
    private JsonConverter() {}

    // 自定义默认模块
    private static List<Module> CUSTOMIZE_MODULES = new ArrayList<Module>();
    static {
        CUSTOMIZE_MODULES.add(new SimpleModule().addSerializer(new CustomizeMapSerializer(Map.class)));
        CUSTOMIZE_MODULES.add(new SimpleModule().addSerializer(new CustomizeUnixTimestampSerializer(Date.class)));
        CUSTOMIZE_MODULES.add(
                new SimpleModule().addDeserializer(Date.class, new CustomizeUnixTimestampDeserializer(Date.class)));
    }

    /**
     * *****************************************************************************************************
     * 序列化
     * *****************************************************************************************************
     */

    /**
     * 将指定java对象序列化成相应字符串
     * 
     * @param obj
     *            java对象
     * @param namingStyle
     *            命名风格
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字符串.序列化失败，返回null
     * @author xl
     */
    public static String encodeAsString(Object obj, NamingStyle namingStyle, boolean useUnicode) {
        ObjectMapper objectMapper = getObjectMapper();
        if (!useUnicode) {
            objectMapper = reRegistCustomizeModules(objectMapper.copy())
                    .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        }
        if (namingStyle != null && getNamingStyle(objectMapper.getPropertyNamingStrategy()) != namingStyle) {
            objectMapper = reRegistCustomizeModules(objectMapper.copy())
                    .setPropertyNamingStrategy(getNamingStrategy(namingStyle));
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.err.println("序列化对象异常并返回null, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 将指定java对象序列化成相应字符串<br/>
     * (默认：使用unicode编码（当有中文字段时）)
     * 
     * @param obj
     *            java对象
     * @param namingStyle
     *            命名风格
     * @return 序列化后的json字符串.序列化失败，返回null
     * @author xl
     */
    public static String encodeAsString(Object obj, NamingStyle namingStyle) {
        return encodeAsString(obj, namingStyle, true);
    }

    /**
     * 将指定java对象序列化成相应字符串<br/>
     * (默认：使用NamingStyle.CAMEL命名风格)
     * 
     * @param obj
     *            java对象
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字符串.序列化失败，返回null
     * @author xl
     */
    public static String encodeAsString(Object obj, boolean useUnicode) {
        return encodeAsString(obj, DEFAULT_STYLE, useUnicode);
    }

    /**
     * 将指定java对象序列化成相应字符串<br/>
     * (默认：使用unicode编码（当有中文字段时）;使用NamingStyle.CAMEL命名风格)
     * 
     * @param obj
     *            java对象
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字符串.序列化失败，返回null
     * @author xl
     */
    public static String encodeAsString(Object obj) {
        return encodeAsString(obj, DEFAULT_STYLE, true);
    }

    public static String encodeAsString(Object obj, NamingStyle namingStyle, boolean useUnicode,
            boolean forceDefaultModule) {
        try {
            ObjectMapper objectMapper = getWorkingMapper(namingStyle, useUnicode, forceDefaultModule);
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.err.println("序列化对象异常并返回null, 异常描述: " + ExceptionFormater.format(e));
        }
        return null;
    }

    public static String encodeAsStringWithDefaultModules(Object obj) {
        return encodeAsString(obj, DEFAULT_STYLE, true, true);
    }

    public static String encodeAsStringWithDefaultModules(Object obj, boolean useUnicode) {
        return encodeAsString(obj, DEFAULT_STYLE, useUnicode, true);
    }

    private static ObjectMapper getWorkingMapper(NamingStyle namingStyle, boolean useUnicode,
            boolean forceDefaultModule) {
        ObjectMapper objectMapper = null;
        if (forceDefaultModule) {
            objectMapper = getBasicConfigureObjectMapper();
            if (!useUnicode) {
                objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
            }
        } else {
            objectMapper = getObjectMapper();
            if (!useUnicode) {
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
            }
            if ((namingStyle != null) && (getNamingStyle(objectMapper.getPropertyNamingStrategy()) != namingStyle)) {
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(namingStyle));
            }
        }
        return objectMapper;
    }

    /**
     * 将指定java对象序列化成相应字节数组
     * 
     * @param obj
     *            java对象
     * @param namingStyle
     *            命名风格
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字节数组.序列化失败，返回null
     * @author xl
     */
    public static byte[] encodeAsBytes(Object obj, NamingStyle namingStyle, boolean useUnicode) {
        ObjectMapper objectMapper = getObjectMapper();
        if (!useUnicode) {
            objectMapper = reRegistCustomizeModules(objectMapper.copy())
                    .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
        }
        if (namingStyle != null && getNamingStyle(objectMapper.getPropertyNamingStrategy()) != namingStyle) {
            objectMapper = reRegistCustomizeModules(objectMapper.copy())
                    .setPropertyNamingStrategy(getNamingStrategy(namingStyle));
        }
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            System.err.println("序列化对象异常并返回null, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 将指定java对象序列化成相应字节数组<br/>
     * (默认：使用unicode编码（当有中文字段时）)
     * 
     * @param obj
     *            java对象
     * @param namingStyle
     *            命名风格
     * @return 序列化后的json字节数组.序列化失败，返回null
     * @author xl
     */
    public static byte[] encodeAsBytes(Object obj, NamingStyle namingStyle) {
        return encodeAsBytes(obj, namingStyle, true);
    }

    /**
     * 将指定java对象序列化成相应字节数组<br/>
     * (默认：使用NamingStyle.CAMEL命名风格)
     * 
     * @param obj
     *            java对象
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字节数组.序列化失败，返回null
     * @author xl
     */
    public static byte[] encodeAsBytes(Object obj, boolean useUnicode) {
        return encodeAsBytes(obj, DEFAULT_STYLE, useUnicode);
    }

    /**
     * 将指定java对象序列化成相应字节数组<br/>
     * (默认：使用unicode编码（当有中文字段时）;使用NamingStyle.CAMEL命名风格)
     * 
     * @param obj
     *            java对象
     * @param useUnicode
     *            是否使用unicode编码（当有中文字段时）
     * @return 序列化后的json字节数组.序列化失败，返回null
     * @author xl
     */
    public static byte[] encodeAsBytes(Object obj) {
        return encodeAsBytes(obj, DEFAULT_STYLE, true);
    }

    /**
     * *****************************************************************************************************
     * 反序列化
     * *****************************************************************************************************
     */

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonStr
     *            待校验的字符串
     * @return <code>true</code> - 是有效的JSON字符串; <code>false</code> -
     *         不是有效的JSON字符串
     * @author xl
     * @since 1.0.0
     */
    public static boolean isJSONValid(String jsonStr) {
        return isJSONValidObject(jsonStr);
    }

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonBytes
     *            待校验的字节数组
     * @return <code>true</code> - 是有效的JSON字节数组; <code>false</code> -
     *         不是有效的JSON字节数组
     * @author xl
     * @since 1.0.1
     */
    public static boolean isJSONValid(byte[] jsonBytes) {
        return isJSONValidObject(jsonBytes);
    }

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonFile
     *            待校验的文件
     * @return <code>true</code> - 是有效的JSON文件; <code>false</code> - 不是有效的JSON文件
     * @author xl
     * @since 1.0.1
     */
    public static boolean isJSONValid(File jsonFile) {
        return isJSONValidObject(jsonFile);
    }

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonInputStream
     *            待校验的输入流
     * @return <code>true</code> - 是有效的JSON输入流; <code>false</code> -
     *         不是有效的JSON输入流
     * @author xl
     * @since 1.0.1
     */
    public static boolean isJSONValid(InputStream jsonInputStream) {
        return isJSONValidObject(jsonInputStream);
    }

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonURL
     *            待校验的URL地址
     * @return <code>true</code> - 是有效的JSON URL地址; <code>false</code> -
     *         不是有效的JSON URL地址
     * @author xl
     * @since 1.0.1
     */
    public static boolean isJSONValid(URL jsonURL) {
        return isJSONValidObject(jsonURL);
    }

    /**
     * 判断字符串是否是合法的JSON数据.
     *
     * @param jsonObject
     *            待校验的对象
     * @return <code>true</code> - 是有效的JSON对象; <code>false</code> - 不是有效的JSON对象
     * @author xl
     * @since 1.0.1
     */
    private static boolean isJSONValidObject(Object jsonObject) {
        try {
            if (jsonObject instanceof String) {
                getObjectMapper().readTree((String) jsonObject);
            } else if (jsonObject instanceof byte[]) {
                getObjectMapper().readTree((byte[]) jsonObject);
            } else if (jsonObject instanceof File) {
                getObjectMapper().readTree((File) jsonObject);
            } else if (jsonObject instanceof InputStream) {
                getObjectMapper().readTree((InputStream) jsonObject);
            } else if (jsonObject instanceof URL) {
                getObjectMapper().readTree((URL) jsonObject);
            } else {
                return false;
            }
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    /**
     * 反序列化JSON字符串到指定的java对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @return java对象.反序列化失败，返回null
     * @since 1.0.0
     * @author xl
     */
    public static Object decode(String jsonStr) {
        if (Validator.isEmpty(jsonStr)) {
            return jsonStr;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return objectMapper.readValue(jsonStr, Object.class);
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonStr)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字符串到指定的java对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @return java对象.反序列化失败，返回null
     * @since 1.0.0
     * @author xl
     */
    public static Object decode(String jsonStr, NamingStyle jsonNamingStyle) {
        if (jsonNamingStyle == null) {
            return decode(jsonStr);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return objectMapper.readValue(jsonStr, Object.class);
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 反序列化JSON字符串到指定的java bean对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param javaBeanClass
     *            java bean对象字节码对象
     * @return java bean对象.反序列化失败，返回null
     * @param <T>
     *            java bean对象
     * @since 1.0.0
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static <T> T decodeAsBean(String jsonStr, Class<?> javaBeanClass) {
        if (Validator.isEmpty(jsonStr) || "\"\"".equals(jsonStr)) {
            return null;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return (T) objectMapper.readValue(jsonStr, javaBeanClass);
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonStr)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字符串到指定的java对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param javaBeanClass
     *            java bean对象字节码对象
     * @return java对象.反序列化失败，返回null
     * @param <T>
     *            java Bean对象
     * @since 1.0.0
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static <T> T decodeAsBean(String jsonStr, NamingStyle jsonNamingStyle, Class<?> javaBeanClass) {
        if (jsonNamingStyle == null) {
            return decodeAsBean(jsonStr, javaBeanClass);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return (T) objectMapper.readValue(jsonStr, javaBeanClass);
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 反序列化JSON字节数组到指定的java bean对象.
     * 
     * @param jsonBytes
     *            JSON字节数组
     * @param javaBeanClass
     *            java bean对象字节码对象
     * @return java bean对象.反序列化失败，返回null
     * @param <T>
     *            java bean对象
     * @since 1.0.1
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static <T> T decodeAsBean(byte[] jsonBytes, Class<?> javaBeanClass) {
        if (Validator.isEmpty(jsonBytes) || Arrays.equals("\"\"".getBytes(), jsonBytes)) {
            return null;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return (T) objectMapper.readValue(jsonBytes, javaBeanClass);
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonBytes)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字节数组到指定的java对象.
     * 
     * @param jsonBytes
     *            JSON字节数组
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param javaBeanClass
     *            java bean对象字节码对象
     * @return java对象.反序列化失败，返回null
     * @param <T>
     *            java Bean对象
     * @since 1.0.1
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static <T> T decodeAsBean(byte[] jsonBytes, NamingStyle jsonNamingStyle, Class<?> javaBeanClass) {
        if (jsonNamingStyle == null) {
            return decodeAsBean(jsonBytes, javaBeanClass);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return (T) objectMapper.readValue(jsonBytes, javaBeanClass);
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 反序列化JSON字符串到指定的java bean的Collection(List/Set等)容器对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param javaCollectionClass
     *            java Collection容器（List/Set等）对象字节码对象
     * @param elementClass
     *            Collection容器（List/Set等）中存储的元素的类型字节码对象
     * @return java bean的Collection容器（List/Set等）对象.反序列化失败，返回null
     * @param <T>
     *            java Collection对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Collection<T> decodeAsCollection(String jsonStr,
            @SuppressWarnings("rawtypes") Class<? extends Collection> javaCollectionClass, Class<T> elementClass) {
        if (Validator.isEmpty(jsonStr) || "\"\"".equals(jsonStr)) {
            return null;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return objectMapper.readValue(jsonStr, objectMapper.getTypeFactory()
                        .constructCollectionType(javaCollectionClass, elementClass));
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonStr)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字符串到指定的java bean的Collection(List/Set等)容器对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param javaCollectionClass
     *            java Collection容器（List/Set等）对象字节码对象
     * @param elementClass
     *            Collection容器（List/Set等）中存储的元素的类型字节码对象
     * @return java bean的Collection容器（List/Set等）对象.反序列化失败，返回null
     * @param <T>
     *            java Collection对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Collection<T> decodeAsCollection(String jsonStr, NamingStyle jsonNamingStyle,
            @SuppressWarnings("rawtypes") Class<? extends Collection> javaCollectionClass, Class<T> elementClass) {
        if (jsonNamingStyle == null) {
            return decodeAsCollection(jsonStr, javaCollectionClass, elementClass);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return objectMapper.readValue(jsonStr, objectMapper.getTypeFactory()
                    .constructCollectionType(javaCollectionClass, elementClass));
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 反序列化JSON字符串到指定的java bean的Collection(List/Set等)容器对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param elementClass
     *            Collection容器（List/Set等）中存储的元素的类型字节码对象
     * @return java bean的Collection容器（List/Set等）对象.反序列化失败，返回null
     * @param <T>
     *            java bean对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Collection<T> decodeAsCollection(String jsonStr, Class<T> elementClass) {
        return decodeAsCollection(jsonStr, Collection.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的Collection(List/Set等)容器对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param elementClass
     *            Collection容器（List/Set等）中存储的元素的类型字节码对象
     * @return java bean的Collection容器（List/Set等）对象.反序列化失败，返回null
     * @param <T>
     *            java bean对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Collection<T> decodeAsCollection(String jsonStr, NamingStyle jsonNamingStyle,
            Class<T> elementClass) {
        return decodeAsCollection(jsonStr, jsonNamingStyle, Collection.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.List对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param javaListClass
     *            java util.List对象字节码对象
     * @param elementClass
     *            java util.List中存储的元素的类型字节码对象
     * @return java bean的util.List对象.反序列化失败，返回null
     * @param <T>
     *            java List对象
     * @since 1.0.0
     * @author pxy
     */
    public static <T> List<T> decodeAsList(String jsonStr,
            @SuppressWarnings("rawtypes") Class<? extends List> javaListClass, Class<T> elementClass) {
        return (List<T>) decodeAsCollection(jsonStr, javaListClass, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.List对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param javaListClass
     *            java util.List对象字节码对象
     * @param elementClass
     *            java util.List中存储的元素的类型字节码对象
     * @return java bean的util.List对象.反序列化失败，返回null
     * @param <T>
     *            java List对象
     * @since 1.0.0
     * @author pxy
     */
    public static <T> List<T> decodeAsList(String jsonStr, NamingStyle jsonNamingStyle,
            @SuppressWarnings("rawtypes") Class<? extends List> javaListClass, Class<T> elementClass) {
        return (List<T>) decodeAsCollection(jsonStr, jsonNamingStyle, javaListClass, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.List对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param elementClass
     *            java util.List中存储的元素的类型字节码对象
     * @return java bean的util.List对象.反序列化失败，返回null
     * @param <T>
     *            java List对象
     * @since 1.0.0
     * @author pxy
     */
    public static <T> List<T> decodeAsList(String jsonStr, Class<T> elementClass) {
        return (List<T>) decodeAsCollection(jsonStr, List.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.List对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param elementClass
     *            java util.List中存储的元素的类型字节码对象
     * @return java bean的util.List对象.反序列化失败，返回null
     * @param <T>
     *            java List对象
     * @since 1.0.0
     * @author pxy
     */
    public static <T> List<T> decodeAsList(String jsonStr, NamingStyle jsonNamingStyle, Class<T> elementClass) {
        return (List<T>) decodeAsCollection(jsonStr, jsonNamingStyle, List.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.Set对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param javaSetClass
     *            java util.Set对象字节码对象
     * @param elementClass
     *            java util.Set中存储的元素的类型字节码对象
     * @return java bean的util.Set对象.反序列化失败，返回null
     * @param <T>
     *            java Set对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Set<T> decodeAsSet(String jsonStr,
            @SuppressWarnings("rawtypes") Class<? extends Set> javaSetClass, Class<T> elementClass) {
        return (Set<T>) decodeAsCollection(jsonStr, javaSetClass, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.Set对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param javaSetClass
     *            java util.Set对象字节码对象
     * @param elementClass
     *            java util.Set中存储的元素的类型字节码对象
     * @return java bean的util.Set对象.反序列化失败，返回null
     * @param <T>
     *            java Set对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Set<T> decodeAsSet(String jsonStr, NamingStyle jsonNamingStyle,
            @SuppressWarnings("rawtypes") Class<? extends Set> javaSetClass, Class<T> elementClass) {
        return (Set<T>) decodeAsCollection(jsonStr, jsonNamingStyle, javaSetClass, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.Set对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param elementClass
     *            java util.Set中存储的元素的类型字节码对象
     * @return java bean的util.Set对象.反序列化失败，返回null
     * @param <T>
     *            java Set对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Set<T> decodeAsSet(String jsonStr, Class<T> elementClass) {
        return (Set<T>) decodeAsCollection(jsonStr, Set.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java bean的util.Set对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @param elementClass
     *            java util.Set中存储的元素的类型字节码对象
     * @return java bean的util.Set对象.反序列化失败，返回null
     * @param <T>
     *            java Set对象
     * @since 1.0.0
     * @author xl
     */
    public static <T> Set<T> decodeAsSet(String jsonStr, NamingStyle jsonNamingStyle, Class<T> elementClass) {
        return (Set<T>) decodeAsCollection(jsonStr, jsonNamingStyle, Set.class, elementClass);
    }

    /**
     * 反序列化JSON字符串到指定的java Map集合对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @return java Map集合对象.反序列化失败，返回null
     * @since 1.0.0
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> decodeAsMap(String jsonStr) {
        if (Validator.isEmpty(jsonStr) || "\"\"".equals(jsonStr)) {
            return null;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return objectMapper.readValue(jsonStr, Map.class);
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonStr)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字符串到指定的java Map集合对象.
     * 
     * @param jsonStr
     *            JSON字符串
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @return java Map集合对象.反序列化失败，返回null
     * @since 1.0.0
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> decodeAsMap(String jsonStr, NamingStyle jsonNamingStyle) {
        if (jsonNamingStyle == null) {
            return decodeAsMap(jsonStr);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return objectMapper.readValue(jsonStr, Map.class);
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 反序列化JSON字节数组到指定的java Map集合对象.
     * 
     * @param jsonBytes
     *            JSON字节数组
     * @return java Map集合对象.反序列化失败，返回null
     * @since 1.0.1
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> decodeAsMap(byte[] jsonBytes) {
        if (Validator.isEmpty(jsonBytes) || Arrays.equals("\"\"".getBytes(), jsonBytes)) {
            return null;
        }
        ObjectMapper objectMapper = getObjectMapper();
        for (int i = 0; i < NAMING_STYLES_LEN; i++) {
            try {
                return objectMapper.readValue(jsonBytes, Map.class);
            } catch (Throwable e) {
                System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
                if (!isJSONValid(jsonBytes)) {
                    return null;
                }
                System.err.println("自动尝试执行[" + NAMING_STYLES[NAMING_STYLES_LEN - 1 - i] + "]方式的反序列化方式策略...");
                objectMapper = reRegistCustomizeModules(objectMapper.copy())
                        .setPropertyNamingStrategy(getNamingStrategy(NAMING_STYLES[NAMING_STYLES_LEN - 1 - i]));
                continue;
            }
        }
        return null;
    }

    /**
     * 反序列化JSON字节数组到指定的java Map集合对象.
     * 
     * @param jsonBytes
     *            JSON字节数组
     * @param jsonNamingStyle
     *            传入的json命名风格.若为null，则自动尝试支持的所有命名风格.
     * @return java Map集合对象.反序列化失败，返回null
     * @since 1.0.1
     * @author xl
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> decodeAsMap(byte[] jsonBytes, NamingStyle jsonNamingStyle) {
        if (jsonNamingStyle == null) {
            return decodeAsMap(jsonBytes);
        }
        ObjectMapper objectMapper = getObjectMapper();
        if (jsonNamingStyle != getNamingStyle(objectMapper.getPropertyNamingStrategy())) {
            objectMapper = getObjectMapper().copy()
                    .setPropertyNamingStrategy(getNamingStrategy(jsonNamingStyle));
        }
        try {
            return objectMapper.readValue(jsonBytes, Map.class);
        } catch (Throwable e) {
            System.err.println("反序列化对象异常, 异常描述: " + ExceptionFormater.format(e));
            return null;
        }
    }

    /**
     * 获取序列化JSON使用的对象.
     *
     * @return 序列化JSON使用的对象
     * @author xl
     * @since 1.0.0
     */
    public static ObjectMapper getObjectMapper() {
        return ObjectMapperSingleton.INSTANCE.getMapper();
    }

    protected static ObjectMapper updateObjectMapper(ObjectMapper objectMapper) {
        return ObjectMapperSingleton.INSTANCE.updateMapper(objectMapper);
    }

    /**
     * 重新注册自定义序列化/反序列化模块.
     *
     * @param objectMapper
     *            序列化/反序列化JSON使用的对象
     * @return 序列化/反序列化JSON使用的对象
     * @author xl
     * @since 1.0.0
     */
    public static ObjectMapper reRegistCustomizeModules(ObjectMapper objectMapper) {
        for (Module module : CUSTOMIZE_MODULES) {
            objectMapper.registerModules(module);
        }
        return objectMapper;
    }

    /**
     * *****************************************************************************************************
     * 自定义命名策略
     * *****************************************************************************************************
     */

    /**
     * 自定义snake(xxx_xxx)命名风格的策略.
     *
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeSnakeCaseStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {
        private static final long serialVersionUID = -2751685741784442267L;

        /**
         * 自定义重写snake命名风格的转换.
         *
         * @param input
         *            待转换的任意命名风格字符串
         * @return snake命名风格字符串
         * @author xl
         * @since 1.0.0
         */
        @Override
        public String translate(String input) {
            return Case.toUnix(input);
        }
    }

    /**
     * 自定义camel(xxxXxx)命名风格的策略.
     *
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeCamelCaseStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {
        private static final long serialVersionUID = -5962054609324768038L;

        /**
         * 自定义重写camel命名风格的转换.
         *
         * @param input
         *            待转换的任意命名风格字符串
         * @return camel命名风格字符串
         * @author xl
         * @since 1.0.0
         */
        @Override
        public String translate(String input) {
            return Case.toLowerCamel(input);
        }
    }

    /**
     * 自定义pascal(XxxXxx)命名风格的策略.
     *
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizePascalCaseStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {
        private static final long serialVersionUID = -3519677788320057846L;

        /**
         * 自定义重写pascal命名风格的转换.
         *
         * @param input
         *            待转换的任意命名风格字符串
         * @return pascal命名风格字符串
         * @author xl
         * @since 1.0.0
         */
        @Override
        public String translate(String input) {
            return Case.toUpperCamel(input);
        }
    }

    /**
     * 自定义kebab(xxx-xxx)命名风格的策略.
     *
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeKebabCaseStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {
        private static final long serialVersionUID = 2049571128175513230L;

        /**
         * 自定义重写kebab命名风格的转换.
         *
         * @param input
         *            待转换的任意命名风格字符串
         * @return kebab命名风格字符串
         * @author xl
         * @since 1.0.0
         */
        @Override
        public String translate(String input) {
            return Case.toKebab(input);
        }
    }

    public static final PropertyNamingStrategy SNAKE_CASE = new CustomizeSnakeCaseStrategy();
    public static final PropertyNamingStrategy LOWER_CAMEL_CASE = new CustomizeCamelCaseStrategy();
    public static final PropertyNamingStrategy UPPER_CAMEL_CASE = new CustomizePascalCaseStrategy();
    public static final PropertyNamingStrategy KEBAB_CASE = new CustomizeKebabCaseStrategy();
    private static final NamingStyle[] NAMING_STYLES = NamingStyle.values();
    private static final int NAMING_STYLES_LEN = NAMING_STYLES.length;
    // 默认命名风格 - 小驼峰
    public static final PropertyNamingStrategy DEFAULT_CASE = LOWER_CAMEL_CASE;
    private static final NamingStyle DEFAULT_STYLE = getNamingStyle(DEFAULT_CASE);

    private static PropertyNamingStrategy getNamingStrategy(NamingStyle namingStyle) {
        switch (namingStyle) {
        case CAMEL:
            return LOWER_CAMEL_CASE;
        case SNAKE:
            return SNAKE_CASE;
        case PASCAL:
            return UPPER_CAMEL_CASE;
        case KEBAB:
            return KEBAB_CASE;
        default:
            return DEFAULT_CASE;
        }
    }

    private static NamingStyle getNamingStyle(PropertyNamingStrategy propertyNamingStrategy) {
        if (propertyNamingStrategy instanceof PropertyNamingStrategy.LowerCaseStrategy
                || propertyNamingStrategy instanceof CustomizeCamelCaseStrategy) {
            return NamingStyle.CAMEL;
        } else if (propertyNamingStrategy instanceof PropertyNamingStrategy.SnakeCaseStrategy
                || propertyNamingStrategy instanceof CustomizeSnakeCaseStrategy) {
            return NamingStyle.SNAKE;
        } else if (propertyNamingStrategy instanceof PropertyNamingStrategy.UpperCamelCaseStrategy
                || propertyNamingStrategy instanceof CustomizePascalCaseStrategy) {
            return NamingStyle.PASCAL;
        } else if (propertyNamingStrategy instanceof PropertyNamingStrategy.KebabCaseStrategy
                || propertyNamingStrategy instanceof CustomizeKebabCaseStrategy) {
            return NamingStyle.KEBAB;
        } else {
            return DEFAULT_STYLE;
        }
    }

    /**
     * 自定义Map序列化工具.
     * 
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeMapSerializer extends StdSerializer<Map<?, ?>> {
        private static final long serialVersionUID = 2254974000378622572L;

        /**
         * 通过Map的字节码对象构造自定义的序列化工具
         *
         * @param t
         *            Map的字节码对象
         */
        @SuppressWarnings("unchecked")
        public CustomizeMapSerializer(Class<?> t) {
            super((Class<Map<?, ?>>) t);
        }

        /**
         * 自定义Map的序列化方式.
         *
         * @param value
         *            Map对象
         * @param gen
         *            产生JSON内容的对象
         * @param provider
         *            序列化提供者
         * @throws IOException
         *             Signals that an I/O exception of some sort has occurred.
         *             This class is the general class of exceptions produced by
         *             failed or interrupted I/O operations.
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
         *      com.fasterxml.jackson.core.JsonGenerator,
         *      com.fasterxml.jackson.databind.SerializerProvider).
         * @author xl
         * @since 0.0.9
         */
        @Override
        public void serialize(Map<?, ?> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<?, ?> entry : value.entrySet()) {
                String fieldName = Case.to(entry.getKey()
                        .toString(),
                        getNamingStyle(provider.getConfig()
                                .getPropertyNamingStrategy()));
                Object fieldVal = entry.getValue();
                gen.writeObjectField(fieldName, fieldVal);
            }
            gen.writeEndObject();
        }
    }

    /**
     * 自定义java.util.Date反序列化工具支持Unix时间戳.
     * 
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeUnixTimestampDeserializer extends StdDeserializer<Date> {
        private static final long serialVersionUID = 7204286006683957838L;

        /**
         * 通过Date的字节码对象构造自定义的反序列化工具.
         *
         * @param vc
         *            Date的字节码对象
         */
        @SuppressWarnings("unchecked")
        protected CustomizeUnixTimestampDeserializer(Class<?> vc) {
            super((Class<Date>) vc);
        }

        /**
         * 自定义Date的反序列化方式.
         *
         * @param p
         *            Json解析对象
         * @param ctxt
         *            反序列化上下文对象
         * @return 反序列化的日期对象
         * @throws IOException
         *             Signals that an I/O exception of some sort has occurred.
         *             This class is the general class of exceptions produced by
         *             failed or interrupted I/O operations.
         * @throws JsonProcessingException
         *             Intermediate base class for all problems encountered when
         *             processing (parsing, generating) JSON content that are
         *             not pure I/O problems. Regular
         *             {@link java.io.IOException}s will be passed through as
         *             is. Sub-class of {@link java.io.IOException} for
         *             convenience.
         * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser,
         *      com.fasterxml.jackson.databind.DeserializationContext).
         * @author xl
         * @since 1.0.0
         */
        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            String time = p.getText()
                    .trim();
            if (time.length() >= 13) {// 自动识别为：毫秒
                return new Date(TimeUnit.MILLISECONDS.toMillis(Long.valueOf(time)));
            } else {// 自动识别为：秒
                return new Date(TimeUnit.SECONDS.toMillis(Long.valueOf(time)));
            }
        }
    }

    /**
     * 自定义java.util.Date序列化工具支持Unix时间戳.
     * 
     * @author xl
     * @since 1.0.0
     */
    private static class CustomizeUnixTimestampSerializer extends StdSerializer<Date> {
        private static final long serialVersionUID = -8439873094644121678L;

        /**
         * 通过Date的字节码对象构造自定义的序列化工具
         *
         * @param t
         *            Date的字节码对象
         */
        @SuppressWarnings("unchecked")
        public CustomizeUnixTimestampSerializer(Class<?> t) {
            super((Class<Date>) t);
        }

        /**
         * 自定义Date的序列化方式.
         *
         * @param value
         *            Date对象
         * @param gen
         *            产生JSON内容的对象
         * @param provider
         *            序列化提供者
         * @throws IOException
         *             Signals that an I/O exception of some sort has occurred.
         *             This class is the general class of exceptions produced by
         *             failed or interrupted I/O operations.
         * @see com.fasterxml.jackson.databind.ser.std.StdSerializer#serialize(java.lang.Object,
         *      com.fasterxml.jackson.core.JsonGenerator,
         *      com.fasterxml.jackson.databind.SerializerProvider).
         * @author xl
         * @since 0.0.9
         */
        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            String time = String.valueOf(value.getTime());
            if (time.length() >= 13) {// 自动识别为：毫秒
                gen.writeNumber(value.getTime() / 1000);
            } else {// 自动识别为：秒
                gen.writeNumber(value.getTime());
            }
        }
    }

    public static ObjectMapper getBasicConfigureObjectMapper() {
        return ObjectMapperSingleton.INSTANCE.getBasicConfigureMapper();
    }

    /**
     * 单例模式构造序列化JSON使用的对象.
     * 
     * @author xl
     * @since 1.0.0
     */
    private static enum ObjectMapperSingleton {
        INSTANCE;
        private ObjectMapper basicObjectMapper = new ObjectMapper()
                .configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
        private ObjectMapper objectMapper = registerCustomizeModules(
                defaultPropertyNamingStrategy(getBasicConfigureMapper().copy()));

        private static ObjectMapper registerCustomizeModules(ObjectMapper objectMapper) {
            if (objectMapper == null) {
                return ObjectMapperSingleton.INSTANCE.getMapper();
            }
            for (Module module : CUSTOMIZE_MODULES) {
                objectMapper.registerModule(module);
            }
            return objectMapper;
        }

        public ObjectMapper defaultPropertyNamingStrategy(ObjectMapper objectMapper) {
            if (objectMapper == null) {
                return INSTANCE.getMapper();
            }
            return objectMapper.configure(MapperFeature.ALLOW_EXPLICIT_PROPERTY_RENAMING, true)
                    .setPropertyNamingStrategy(JsonConverter.DEFAULT_CASE);
        }

        public ObjectMapper getMapper() {
            return objectMapper;
        }

        public ObjectMapper updateMapper(ObjectMapper objectMapper) {
            if (objectMapper == null) {
                return objectMapper;
            }
            if (getObjectMapper() != objectMapper) {
                registerCustomizeModules(getMapper());
            }
            this.objectMapper = objectMapper;
            return getMapper();
        }

        public ObjectMapper getBasicConfigureMapper() {
            return this.basicObjectMapper;
        }
    }

}
