package org.campus.partner.util.type;

import java.util.HashMap;
import java.util.Map;

import org.campus.partner.util.string.Case;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * JavaBean类型转换成Map类型.
 *
 * @author xl
 * @since 1.0.0
 */
public abstract class BeanToMap {

    /**
     * 通过Java对象获得对应的Map. (注:
     * 默认转换后Map的key值将以{@link org.campus.partner.util.type.NamingStyle.CAMEL}进行命名).
     *
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @return 转换后的Map
     * @author xl
     * @since 0.0.3
     */
    public static Map<String, Object> getMap(Object beanInstanceOrBeanClass) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, false, NamingStyle.CAMEL, false);
    }

    /**
     * 通过Java对象获得对应的Map.
     *
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @param keyNamingStyle
     *            指定转换后的Map键的命名风格
     * @return 转换后的Map
     * @author xl
     * @since 0.0.3
     */
    public static Map<String, Object> getMap(Object beanInstanceOrBeanClass, NamingStyle keyNamingStyle) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, false, keyNamingStyle, false);
    }

    /**
     * 通过Java对象获得对应非空字段的Map. (注:
     * 默认转换后Map的key值将以{@link cn.signit.wesign.lib.common.type.NamingStyle.CAMEL}进行命名).
     * 
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @return 转换后的包含非空字段的Map
     * @author xl
     * @since 0.0.3
     */
    public static Map<String, Object> getSelectiveMap(Object beanInstanceOrBeanClass) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, true, NamingStyle.CAMEL, false);
    }

    /**
     * 通过Java对象获得对应非空字段的Map. (注:
     * 默认转换后Map的key值将以{@link cn.signit.wesign.lib.common.type.NamingStyle.CAMEL}进行命名).
     * 
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @param keyNamingStyle
     *            指定转换后的Map键的命名风格
     * @return 转换后的包含非空字段的Map
     * @author xl
     * @since 1.0.0
     */
    public static Map<String, Object> getSelectiveMap(Object beanInstanceOrBeanClass, NamingStyle keyNamingStyle) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, true, keyNamingStyle, false);
    }

    /**
     * 通过Java对象获得对应的Map. (注:
     * 默认转换后Map的key值将以{@link cn.signit.wesign.lib.common.type.NamingStyle.CAMEL}进行命名).
     * <br/>
     * 注:若Bean是复合对象时，包含的对象属性字段以String形式转换成Map的value时将会是个内存地址.
     *
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @return 转换后的Map
     * @author xl
     * @since 1.0.0
     */
    public static Map<String, String> getStringMap(Object beanInstanceOrBeanClass) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, false, NamingStyle.CAMEL, true);
    }

    /**
     * 通过Java对象获得对应的Map. <br/>
     * 注:若Bean是复合对象时，包含的对象属性字段以String形式转换成Map的value时将会是个内存地址.
     * 
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @param keyNamingStyle
     *            指定转换后的Map键的命名风格
     * @return 转换后的Map
     * @author xl
     * @since 1.0.0
     */
    public static Map<String, String> getStringMap(Object beanInstanceOrBeanClass, NamingStyle keyNamingStyle) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, false, keyNamingStyle, true);
    }

    /**
     * 通过Java对象获得对应非空字段的Map. (注:
     * 默认转换后Map的key值将以{@link cn.signit.wesign.lib.common.type.NamingStyle.CAMEL}进行命名).
     * <br/>
     * 注:若Bean是复合对象时，包含的对象属性字段以String形式转换成Map的value时将会是个内存地址.
     * 
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @return 转换后的包含非空字段的Map
     * @author xl
     * @since 1.0.0
     */
    public static Map<String, String> getSelectiveStringMap(Object beanInstanceOrBeanClass) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, true, NamingStyle.CAMEL, true);
    }

    /**
     * 通过Java对象获得对应非空字段的Map. <br/>
     * 注:若Bean是复合对象时，包含的对象属性字段以String形式转换成Map的value时将会是个内存地址.
     * 
     * @param beanInstanceOrBeanClass
     *            待转换的Java bean实例对象或字节码对象
     * @param keyNamingStyle
     *            指定转换后的Map键的命名风格
     * @return 转换后的包含非空字段的Map
     * @author xl
     * @since 1.0.0
     */
    public static Map<String, String> getSelectiveStringMap(Object beanInstanceOrBeanClass,
            NamingStyle keyNamingStyle) {
        return getMapFromBeanFields(beanInstanceOrBeanClass, true, keyNamingStyle, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getMapFromBeanFields(Object beanInstanceOrBeanClass, boolean isSelective,
            NamingStyle namingStyle, boolean mapValueToString) {
        Map<String, Object> beanFieldKVMap = null;
        Map<String, String> beanFieldKVStringMap = null;
        if (mapValueToString) {
            beanFieldKVStringMap = new HashMap<String, String>();
        } else {
            beanFieldKVMap = new HashMap<String, Object>();
        }
        Object beanObj = BeanToBean.getInstance(beanInstanceOrBeanClass);
        if (beanObj == null) {
            return (T) (beanFieldKVStringMap == null ? beanFieldKVMap : beanFieldKVStringMap);
        }
        MethodAccess methodAccess = MethodAccess.get(beanObj.getClass());
        String[] methodNames = methodAccess.getMethodNames();
        if (methodNames == null) {
            return (T) (beanFieldKVStringMap == null ? beanFieldKVMap : beanFieldKVStringMap);
        }
        StringBuilder sb = new StringBuilder();
        for (String methoName : methodAccess.getMethodNames()) {
            sb.delete(0, sb.length());
            // 提取方法名
            if (methoName.startsWith("set")) {
                Object fieldValue = null;
                sb = sb.append(methoName.substring(3));
                int cutTo = 0;
                try {
                    cutTo = 3;
                    fieldValue = methodAccess.invoke(beanObj, sb.insert(0, "get")
                            .toString());
                } catch (Throwable e1) {
                    sb.delete(0, cutTo);
                    try {
                        cutTo = 2;
                        fieldValue = methodAccess.invoke(beanObj, sb.insert(0, "is")
                                .toString());
                    } catch (Throwable e2) {
                        continue;
                    }
                }
                if (isSelective && fieldValue == null) {
                    continue;
                }
                sb.delete(0, cutTo);
                String formatMethoName = Case.to(sb.toString(), namingStyle);
                if (beanFieldKVStringMap == null) {
                    beanFieldKVMap.put(formatMethoName, fieldValue);
                } else {
                    beanFieldKVStringMap.put(formatMethoName, fieldValue == null ? null : String.valueOf(fieldValue));
                }
            }
        }
        return (T) (beanFieldKVStringMap == null ? beanFieldKVMap : beanFieldKVStringMap);
    }
}
