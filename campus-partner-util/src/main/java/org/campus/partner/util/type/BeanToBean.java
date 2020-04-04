package org.campus.partner.util.type;

import java.util.Date;
import java.util.Map;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * 将任意一个JavaBean类型转另外一个JavaBean类型.
 * <p>
 * 注: 2个Bean需要保证有相同名称的属性字段和类型.
 * </p>
 *
 * @author xl
 * @since 1.0.0
 */
public abstract class BeanToBean {

    /**
     * 从已知的JavaBean对象实例化获取与之具有相同属性名称和属性类型的另外一个JavaBean对象.
     *
     * @param fromBeanInstance
     *            已知的已实例化的JavaBean对象
     * @param targetBeanClass
     *            待实例化的JavaBean对应的字节码对象
     * @param <T>
     *            targetBeanClass字节码对应的已实例化的JavaBean对象
     * @return targetBeanClass字节码对应的已实例化的JavaBean对象.获取失败,则返回null.
     * @author xl
     * @since 0.0.3
     */
    public static <T> T getBean(Object fromBeanInstance, Class<?> targetBeanClass) {
        return getBeanToAnyObject(fromBeanInstance, targetBeanClass);
    }

    /**
     * 从已知的JavaBean对象实例化获取与之具有相同属性名称和属性类型的另外一个JavaBean对象.
     *
     * @param fromBeanInstance
     *            已知的已实例化的JavaBean对象
     * @param targetBeanInstanceOrBeanClass
     *            已实例化的JavaBean对象或待实例化的JavaBean对应的字节码对象
     * @param <T>
     *            targetBeanClass字节码对应的已实例化的JavaBean对象
     * @return targetBeanClass字节码对应的已实例化的JavaBean对象.获取失败,则返回null.
     * @author xl
     * @since 0.0.3
     */
    public static <T> T getBean(Object fromBeanInstance, Object targetBeanInstanceOrBeanClass) {
        return getBeanToAnyObject(fromBeanInstance, targetBeanInstanceOrBeanClass);
    }

    private static <T> T getBeanToAnyObject(Object fromBeanInstance, Object targetBeanInstanceOrBeanClass) {
        if (targetBeanInstanceOrBeanClass == null || fromBeanInstance == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        T instance = (T) getInstance(targetBeanInstanceOrBeanClass);
        MethodAccess targetMethodAccess = MethodAccess.get(instance.getClass());
        MethodAccess fromMethodAccess = MethodAccess.get(fromBeanInstance.getClass());
        for (String fromMethod : fromMethodAccess.getMethodNames()) {
            int tIdx = getSetMethodIndexFromGetMethod(targetMethodAccess, fromMethod);
            if (tIdx < 0) {
                continue;
            }
            try {
                Class<?> fromGetReturnType = getReturnTypeFromGetMethod(fromMethodAccess, fromMethod, 2);
                Class<?> targetGetReturnType = getReturnTypeFromGetMethod(targetMethodAccess, fromMethod, 2);
                Object fromVal = fromMethodAccess.invoke(fromBeanInstance, fromMethod);
                // 类型完全匹配/原始类型和包装类型
                if (fromGetReturnType == targetGetReturnType
                        || (Primitives.isBasicType(fromGetReturnType) && Primitives.isBasicType(targetGetReturnType)
                                && (Primitives.isWrapperType(fromGetReturnType)
                                        && !Primitives.isWrapperType(targetGetReturnType)
                                        || Primitives.isWrapperType(targetGetReturnType)
                                                && !Primitives.isWrapperType(fromGetReturnType)))) {
                    targetMethodAccess.invoke(instance, tIdx, fromVal);
                    continue;
                }

                // 支持字符串和枚举类型转换
                if (convertBetweenStringAndEnum(fromGetReturnType, fromVal, targetGetReturnType, targetMethodAccess,
                        instance, tIdx)) {
                    continue;
                }

                // 支持长整型和日期类型转换
                if (convertBetweenLongAndDate(fromGetReturnType, fromVal, targetGetReturnType, targetMethodAccess,
                        instance, tIdx)) {
                    continue;
                }

                // 支持Map和Bean类型转换
                if (convertBetweenMapAndBean(fromGetReturnType, fromVal, targetGetReturnType, targetMethodAccess,
                        instance, tIdx)) {
                    continue;
                }

                // 支持Bean和Bean类型转换
                if (convertBetweenBeanAndBean(fromGetReturnType, fromVal, targetGetReturnType, targetMethodAccess,
                        instance, tIdx)) {
                    continue;
                }
            } catch (Throwable e) {
                continue;
            }
        }
        return instance;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean convertBetweenStringAndEnum(Class<?> fromClass, Object fromVal, Class<?> targetClass,
            MethodAccess targetMethodAccess, Object targetInstance, int targetIndex) {
        if (String.class.isAssignableFrom(fromClass) && Enum.class.isAssignableFrom(targetClass)) {
            // String-> Enum
            targetMethodAccess.invoke(targetInstance, targetIndex,
                    Enum.valueOf((Class<Enum>) targetClass, (String) fromVal));
            return true;
        } else if (String.class.isAssignableFrom(targetClass) && Enum.class.isAssignableFrom(fromClass)) {
            // Enum->String
            targetMethodAccess.invoke(targetInstance, targetIndex, ((Enum) fromVal).name());
            return true;
        } else {
            return false;
        }
    }

    private static boolean convertBetweenLongAndDate(Class<?> fromClass, Object fromVal, Class<?> targetClass,
            MethodAccess targetMethodAccess, Object targetInstance, int targetIndex) {
        if ((Long.class.isAssignableFrom(fromClass) || long.class.isAssignableFrom(fromClass))
                && Date.class.isAssignableFrom(targetClass)) {
            // Long/long -> Date
            targetMethodAccess.invoke(targetInstance, targetIndex, new Date((Long) fromVal));
            return true;
        } else if ((Long.class.isAssignableFrom(targetClass) || long.class.isAssignableFrom(targetClass))
                && Date.class.isAssignableFrom(fromClass)) {
            // Date -> Long/long
            targetMethodAccess.invoke(targetInstance, targetIndex, ((Date) fromVal).getTime());
            return true;
        } else {
            return false;
        }
    }

    private static boolean convertBetweenMapAndBean(Class<?> fromClass, Object fromVal, Class<?> targetClass,
            MethodAccess targetMethodAccess, Object targetInstance, int targetIndex) {
        if (Map.class.isAssignableFrom(fromClass) && Object.class.isAssignableFrom(targetClass)) {
            // Map->Bean
            targetMethodAccess.invoke(targetInstance, targetIndex, MapToBean.getBean((Map<?, ?>) fromVal, targetClass));
            return true;
        } else if (Map.class.isAssignableFrom(targetClass) && Object.class.isAssignableFrom(fromClass)) {
            // Bean->Map
            targetMethodAccess.invoke(targetInstance, targetIndex, BeanToMap.getMap(fromVal));
            return true;
        } else {
            return false;
        }
    }

    private static boolean convertBetweenBeanAndBean(Class<?> fromClass, Object fromVal, Class<?> targetClass,
            MethodAccess targetMethodAccess, Object targetInstance, int targetIndex) {
        if (Object.class.isAssignableFrom(fromClass) && Object.class.isAssignableFrom(targetClass)) {
            // Bean->Bean
            targetMethodAccess.invoke(targetInstance, targetIndex, getBeanToAnyObject(fromVal, targetClass));
            return true;
        } else {
            return false;
        }
    }

    private static int getSetMethodIndexFromGetMethod(MethodAccess methodAccess, String getMethodName) {
        int idx = -1;
        if (methodAccess == null || getMethodName == null) {
            return idx;
        }
        // 支持从"getXXX"方法获取
        if (getMethodName.startsWith("get")) {
            try {
                idx = methodAccess.getIndex(getMethodName.replaceFirst("get", "set"));
            } catch (Throwable e) {
                idx = -1;
            }
        }
        // 支持从"isXXX"方法获取
        else if (getMethodName.startsWith("is")) {
            try {
                idx = methodAccess.getIndex(getMethodName.replaceFirst("is", "set"));
            } catch (Throwable e) {
                idx = -1;
            }
        } else {
            idx = -1;
        }
        return idx;
    }

    private static Class<?> getReturnTypeFromGetMethod(MethodAccess methodAccess, String getMethodName,
            int supportCount) {
        int iSupportCount = supportCount;
        if (iSupportCount <= 0) {
            return null;
        }
        int idx = -1;
        if (methodAccess == null || getMethodName == null) {
            return null;
        }
        // 支持从"getXXX"方法获取
        if (getMethodName.startsWith("get")) {
            try {
                idx = methodAccess.getIndex(getMethodName);
            } catch (Throwable e) {
                return getReturnTypeFromGetMethod(methodAccess, getMethodName.replaceFirst("get", "is"),
                        --iSupportCount);
            }
        }
        // 支持从"isXXX"方法获取
        else if (getMethodName.startsWith("is")) {
            try {
                idx = methodAccess.getIndex(getMethodName);
            } catch (Throwable e) {
                return getReturnTypeFromGetMethod(methodAccess, getMethodName.replaceFirst("is", "get"),
                        --iSupportCount);
            }
        } else {
            idx = -1;
        }
        if (idx < 0) {
            return null;
        }
        Class<?>[] returnTypes = methodAccess.getReturnTypes();
        if (idx >= returnTypes.length) {
            return null;
        }
        return returnTypes[idx];
    }

    /**
     * 获取任何对象的实例.
     *
     * @param beanInstanceOrBeanClass
     *            实例对象本身或字节码对象
     * @param <T>
     *            beanInstanceOrBeanClass对应的实例化后的对象
     * @return 实例化后的对象
     * @throws IllegalArgumentException
     *             Constructs an IllegalArgumentException with the specified
     *             detail message.
     * @author xl
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Object beanInstanceOrBeanClass) {
        if (beanInstanceOrBeanClass instanceof Class) {
            try {
                return (T) ((Class<?>) beanInstanceOrBeanClass).newInstance();
            } catch (Throwable e) {
                try {
                    return UnsafeUtil.forceInstance((Class<?>) beanInstanceOrBeanClass);
                } catch (InstantiationException e1) {
                    throw new IllegalArgumentException(e1.getMessage());
                }
            }
        } else {
            return (T) beanInstanceOrBeanClass;
        }
    }
}