package org.campus.partner.util.type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.campus.partner.util.ExceptionFormater;
import org.campus.partner.util.Validator;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * Map类型转换成JavaBean类型.
 *
 * @author xl
 * @since 1.0.0
 */
public abstract class MapToBean {

    /**
     * 通过value为String的Map获得对应的JavaBean对象.
     *
     * @param map
     *            value为String的Map对象
     * @param beanClass
     *            将要用于存储Map中键值对应的Java字节码对象
     * @return JavaBean对象.若获取失败, 则返回null
     * @author xl
     * @since 1.0.0
     */

    public static <T> T getBeanFromStringMap(Map<String, String> map, Class<?> beanClass) {
        return getBean(map, beanClass);
    }

    /**
     * 通过value为Object的Map获得对应的JavaBean对象.
     *
     * @param map
     *            value为Object的Map对象
     * @param beanClass
     *            将要用于存储Map中键值对应的Java字节码对象
     * @return JavaBean对象.若获取失败, 则返回null
     * @author xl
     * @since 1.0.0
     */
    public static <T> T getBeanFromObjectMap(Map<String, Object> map, Class<?> beanClass) {
        return getBean(map, beanClass);
    }

    /**
     * 通过Map获得对应的JavaBean对象.
     * 
     * @param map
     *            待转换的Map
     * @param beanClass
     *            将要用于存储Map中键值对应的Java字节码对象
     * @param <T>
     *            JavaBean对象
     * @return JavaBean对象.若获取失败, 则返回null
     * @author xl
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> T getBean(Map<?, ?> map, Class<?> beanClass) {
        if (map == null || map.isEmpty() || beanClass == null) {
            return null;
        }
        MethodAccess methodAccess = MethodAccess.get(beanClass);
        T bean = (T) BeanToBean.getInstance(beanClass);
        Object val = null;
        Map<String, Class<?>> setAndTypeMap = getBeanSetMethodNameAndTypeMap(methodAccess);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String setMethodName = null;
            Class<?> beanFieldType = null;
            String key = entry.getKey()
                    .toString();
            try {
                for (Map.Entry<String, Class<?>> entry2 : setAndTypeMap.entrySet()) {
                    if (entry2.getKey()
                            .toLowerCase()
                            .endsWith(key.toLowerCase())) {
                        setMethodName = entry2.getKey();
                        beanFieldType = entry2.getValue();
                        setAndTypeMap.remove(setMethodName);
                        break;
                    }
                }
                if (setMethodName == null || beanFieldType == null) {
                    continue;
                }
                val = entry.getValue();
                // 类型匹配直接调用
                if (val == null || Primitives.wrap(val.getClass()) == Primitives.wrap(beanFieldType)
                        || beanFieldType.isAssignableFrom(val.getClass())) {
                    methodAccess.invoke(bean, setMethodName, val);
                    continue;
                }
                // 类型不匹配需要转换调用
                try {
                    if (convertToBasicType(methodAccess, bean, setMethodName, beanFieldType, val)
                            || convertToCompositeType(methodAccess, bean, setMethodName, beanFieldType, val)) {
                        continue;
                    }
                } catch (Throwable e2) {
                    continue;
                }
            } catch (Throwable e) {
                continue;
            }
        }
        return bean;
    }

    /**
     * 转换到基础类型.
     */
    private static boolean convertToBasicType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        try {
            if (convertNumberToByteType(methodAccess, beanObj, setMethodName, expectType, realVal)
                    || convertNumberToShortType(methodAccess, beanObj, setMethodName, expectType, realVal)
                    || convertNumberToIntegerType(methodAccess, beanObj, setMethodName, expectType, realVal)
                    || convertNumberToLongType(methodAccess, beanObj, setMethodName, expectType, realVal)
                    || convertNumberToFloatType(methodAccess, beanObj, setMethodName, expectType, realVal)
                    || convertNumberToDoubleType(methodAccess, beanObj, setMethodName, expectType, realVal)) {
                finished = true;
            }
            // Date -> Long 型
            else if (unwrapExpectType == Long.TYPE && realVal instanceof Date) {
                methodAccess.invoke(beanObj, setMethodName, ((Date) realVal).getTime());
                finished = true;
            }
            // String -> 基础型
            else if (Primitives.allPrimitiveTypes()
                    .contains(unwrapExpectType) && realVal instanceof String) {
                finished = convertStringToBasicType(methodAccess, beanObj, setMethodName, unwrapExpectType,
                        (String) realVal);
            } else {
                finished = false;
            }
        } catch (Throwable e) {
            System.err.println("WARN: 转换到基础类型存在异常，原因：" + ExceptionFormater.format(e));
            finished = false;
        }
        return finished;
    }

    // Number -> Byte 型
    private static boolean convertNumberToByteType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Short -> Byte 型
        if (unwrapExpectType == Byte.TYPE && unwrapRealType == Short.TYPE) {
            Short real = (Short) realVal;
            if (real >= Byte.MIN_VALUE & real <= Byte.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Byte.parseByte(real.toString()));
                finished = true;
            }
        }
        // Integer -> Byte 型
        else if (unwrapExpectType == Byte.TYPE && unwrapRealType == Integer.TYPE) {
            Integer real = (Integer) realVal;
            if (real >= Byte.MIN_VALUE & real <= Byte.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Byte.parseByte(real.toString()));
                finished = true;
            }
        }
        // Long -> Byte 型
        else if (unwrapExpectType == Byte.TYPE && unwrapRealType == Long.TYPE) {
            Long real = (Long) realVal;
            if (real >= Byte.MIN_VALUE & real <= Byte.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Byte.parseByte(real.toString()));
                finished = true;
            }
        }
        // Float -> Byte 型
        else if (unwrapExpectType == Byte.TYPE && unwrapRealType == Float.TYPE) {
            Float real = (Float) realVal;
            if (real >= Byte.MIN_VALUE & real <= Byte.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Byte.parseByte(real.toString()));
                finished = true;
            }
        }
        // Double -> Byte 型
        else if (unwrapExpectType == Byte.TYPE && unwrapRealType == Double.TYPE) {
            Double real = (Double) realVal;
            if (real >= Byte.MIN_VALUE & real <= Byte.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Byte.parseByte(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    // Number -> Short 型
    private static boolean convertNumberToShortType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Byte -> Short 型
        if (unwrapExpectType == Short.TYPE && unwrapRealType == Byte.TYPE) {
            Byte real = (Byte) realVal;
            if (real >= Short.MIN_VALUE & real <= Short.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Short.parseShort(real.toString()));
                finished = true;
            }
        }
        // Integer -> Short 型
        else if (unwrapExpectType == Short.TYPE && unwrapRealType == Integer.TYPE) {
            Integer real = (Integer) realVal;
            if (real >= Short.MIN_VALUE & real <= Short.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Short.parseShort(real.toString()));
                finished = true;
            }
        }
        // Long -> Short 型
        else if (unwrapExpectType == Short.TYPE && unwrapRealType == Long.TYPE) {
            Long real = (Long) realVal;
            if (real >= Short.MIN_VALUE & real <= Short.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Short.parseShort(real.toString()));
                finished = true;
            }
        }
        // Float -> Short 型
        else if (unwrapExpectType == Short.TYPE && unwrapRealType == Float.TYPE) {
            Float real = (Float) realVal;
            if (real >= Short.MIN_VALUE & real <= Short.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Short.parseShort(real.toString()));
                finished = true;
            }
        }
        // Double -> Short 型
        else if (unwrapExpectType == Short.TYPE && unwrapRealType == Double.TYPE) {
            Double real = (Double) realVal;
            if (real >= Short.MIN_VALUE & real <= Short.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Short.parseShort(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    // Number -> Integer 型
    private static boolean convertNumberToIntegerType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Byte -> Integer 型
        if (unwrapExpectType == Integer.TYPE && unwrapRealType == Byte.TYPE) {
            Byte real = (Byte) realVal;
            if (real >= Integer.MIN_VALUE & real <= Integer.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Integer.parseInt(real.toString()));
                finished = true;
            }
        }
        // Short -> Integer 型
        else if (unwrapExpectType == Integer.TYPE && unwrapRealType == Short.TYPE) {
            Short real = (Short) realVal;
            if (real >= Integer.MIN_VALUE & real <= Integer.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Integer.parseInt(real.toString()));
                finished = true;
            }
        }
        // Long -> Integer 型
        else if (unwrapExpectType == Integer.TYPE && unwrapRealType == Long.TYPE) {
            Long real = (Long) realVal;
            if (real >= Integer.MIN_VALUE & real <= Integer.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Integer.parseInt(real.toString()));
                finished = true;
            }
        }
        // Float -> Integer 型
        else if (unwrapExpectType == Integer.TYPE && unwrapRealType == Float.TYPE) {
            Float real = (Float) realVal;
            if (real >= Integer.MIN_VALUE & real <= Integer.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Integer.parseInt(real.toString()));
                finished = true;
            }
        }
        // Double -> Integer 型
        else if (unwrapExpectType == Integer.TYPE && unwrapRealType == Double.TYPE) {
            Double real = (Double) realVal;
            if (real >= Integer.MIN_VALUE & real <= Integer.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Integer.parseInt(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    // Number -> Long 型
    private static boolean convertNumberToLongType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Byte -> Long 型
        if (unwrapExpectType == Long.TYPE && unwrapRealType == Byte.TYPE) {
            Byte real = (Byte) realVal;
            if (real >= Long.MIN_VALUE & real <= Long.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Long.parseLong(real.toString()));
                finished = true;
            }
        }
        // Short -> Long 型
        else if (unwrapExpectType == Long.TYPE && unwrapRealType == Short.TYPE) {
            Short real = (Short) realVal;
            if (real >= Long.MIN_VALUE & real <= Long.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Long.parseLong(real.toString()));
                finished = true;
            }
        }
        // Integer -> Long 型
        else if (unwrapExpectType == Long.TYPE && unwrapRealType == Integer.TYPE) {
            Integer real = (Integer) realVal;
            if (real >= Long.MIN_VALUE & real <= Long.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Long.parseLong(real.toString()));
                finished = true;
            }
        }
        // Float -> Long 型
        else if (unwrapExpectType == Long.TYPE && unwrapRealType == Float.TYPE) {
            Float real = (Float) realVal;
            if (real >= Long.MIN_VALUE & real <= Long.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Long.parseLong(real.toString()));
                finished = true;
            }
        }
        // Double -> Long 型
        else if (unwrapExpectType == Long.TYPE && unwrapRealType == Double.TYPE) {
            Double real = (Double) realVal;
            if (real >= Long.MIN_VALUE & real <= Long.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Long.parseLong(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    // Number -> Float 型
    private static boolean convertNumberToFloatType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Byte -> Float 型
        if (unwrapExpectType == Float.TYPE && unwrapRealType == Byte.TYPE) {
            Byte real = (Byte) realVal;
            if (real >= Float.MIN_VALUE & real <= Float.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Float.parseFloat(real.toString()));
                finished = true;
            }
        }
        // Short -> Float 型
        else if (unwrapExpectType == Float.TYPE && unwrapRealType == Short.TYPE) {
            Short real = (Short) realVal;
            if (real >= Float.MIN_VALUE & real <= Float.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Float.parseFloat(real.toString()));
                finished = true;
            }
        }
        // Integer -> Float 型
        else if (unwrapExpectType == Float.TYPE && unwrapRealType == Integer.TYPE) {
            Integer real = (Integer) realVal;
            if (real >= Float.MIN_VALUE & real <= Float.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Float.parseFloat(real.toString()));
                finished = true;
            }
        }
        // Long -> Float 型
        else if (unwrapExpectType == Float.TYPE && unwrapRealType == Long.TYPE) {
            Long real = (Long) realVal;
            if (real >= Float.MIN_VALUE & real <= Float.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Float.parseFloat(real.toString()));
                finished = true;
            }
        }
        // Double -> Float 型
        else if (unwrapExpectType == Float.TYPE && unwrapRealType == Double.TYPE) {
            Double real = (Double) realVal;
            if (real >= Float.MIN_VALUE & real <= Float.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Float.parseFloat(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    // Number -> Double 型
    private static boolean convertNumberToDoubleType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> unwrapExpectType = Primitives.unwrap(expectType);
        Class<?> unwrapRealType = Primitives.unwrap(realVal.getClass());
        // Byte -> Double 型
        if (unwrapExpectType == Double.TYPE && unwrapRealType == Byte.TYPE) {
            Byte real = (Byte) realVal;
            if (real >= Double.MIN_VALUE & real <= Double.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Double.parseDouble(real.toString()));
                finished = true;
            }
        }
        // Short -> Double 型
        else if (unwrapExpectType == Double.TYPE && unwrapRealType == Short.TYPE) {
            Short real = (Short) realVal;
            if (real >= Double.MIN_VALUE & real <= Double.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Double.parseDouble(real.toString()));
                finished = true;
            }
        }
        // Integer -> Double 型
        else if (unwrapExpectType == Double.TYPE && unwrapRealType == Integer.TYPE) {
            Integer real = (Integer) realVal;
            if (real >= Double.MIN_VALUE & real <= Double.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Double.parseDouble(real.toString()));
                finished = true;
            }
        }
        // Long -> Double 型
        else if (unwrapExpectType == Double.TYPE && unwrapRealType == Long.TYPE) {
            Long real = (Long) realVal;
            if (real >= Double.MIN_VALUE & real <= Double.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Double.parseDouble(real.toString()));
                finished = true;
            }
        }
        // Float -> Double 型
        else if (unwrapExpectType == Double.TYPE && unwrapRealType == Float.TYPE) {
            Float real = (Float) realVal;
            if (real >= Double.MIN_VALUE & real <= Double.MAX_VALUE) {
                methodAccess.invoke(beanObj, setMethodName, Double.parseDouble(real.toString()));
                finished = true;
            }
        } else {
            finished = false;
        }
        return finished;
    }

    /**
     * 转换String类型到基础类型.
     */
    private static boolean convertStringToBasicType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, String realVal) {
        boolean finished = false;
        Class<?> wrapExpectType = Primitives.wrap(expectType);
        try {
            Object getMethodVal = null;
            if (wrapExpectType == Integer.class) {
                getMethodVal = MethodAccess.get(wrapExpectType)
                        .invoke(null, "parseInt", realVal);
            } else if (wrapExpectType == Boolean.class) {
                getMethodVal = Boolean.parseBoolean(realVal);
            } else {
                getMethodVal = MethodAccess.get(wrapExpectType)
                        .invoke(null, "parse" + wrapExpectType.getSimpleName(), realVal);
            }
            methodAccess.invoke(beanObj, setMethodName, getMethodVal);
            finished = true;
        } catch (Throwable e) {
            System.err.println("WARN: 转换String类型到基础类型存在异常，原因：" + ExceptionFormater.format(e));
            finished = false;
        }
        return finished;
    }

    /**
     * 转换到复合类型.
     * 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> boolean convertToCompositeType(MethodAccess methodAccess, Object beanObj, String setMethodName,
            Class<?> expectType, Object realVal) {
        boolean finished = false;
        Class<?> wrapExpectType = Primitives.wrap(expectType);
        Class<?> wrapRealType = Primitives.wrap(realVal.getClass());
        try {
            // List -> Set 型
            if (Set.class.isAssignableFrom(wrapExpectType) && List.class.isAssignableFrom(wrapRealType)) {
                methodAccess.invoke(beanObj, setMethodName, convertListToSet((List<?>) realVal, wrapExpectType));
                finished = true;
            }
            // Set -> List 型
            else if (List.class.isAssignableFrom(wrapExpectType) && Set.class.isAssignableFrom(wrapRealType)) {
                methodAccess.invoke(beanObj, setMethodName, convertSetToList((Set<?>) realVal, wrapExpectType));
                finished = true;
            }
            // Array -> List 型
            else if (List.class.isAssignableFrom(wrapExpectType) && wrapRealType.isArray()) {
                methodAccess.invoke(beanObj, setMethodName, Arrays.asList(realVal));
                finished = true;
            }
            // List -> Array 型
            else if (wrapExpectType.isArray() && List.class.isAssignableFrom(wrapRealType)) {
                // 此处 methodAccess.invoke(beanObj, setMethodName,
                // arr);存在BUG，故采用java原生的反射方式来调用
                javaReflectInvokeMethod(beanObj, setMethodName, wrapExpectType, ((List<?>) realVal).toArray());
                finished = true;
            }
            // Array -> Set 型
            else if (Set.class.isAssignableFrom(wrapExpectType) && wrapRealType.isArray()) {
                methodAccess.invoke(beanObj, setMethodName, convertListToSet(Arrays.asList(realVal), wrapExpectType));
                finished = true;
            }
            // Set -> Array 型
            else if (wrapExpectType.isArray() && Set.class.isAssignableFrom(wrapRealType)) {
                // 此处 methodAccess.invoke(beanObj,
                // setMethodName,arr);存在BUG，故采用java原生的反射方式来调用
                javaReflectInvokeMethod(beanObj, setMethodName, wrapExpectType, ((Set<?>) realVal).toArray());
                finished = true;
            }
            // Map -> Object(javabean) 型
            else if (wrapExpectType != null && Map.class.isAssignableFrom(wrapRealType)) {
                methodAccess.invoke(beanObj, setMethodName, getBean((Map<String, ?>) realVal, expectType));
                finished = true;
            }
            // Object(javabean) -> Map 型
            else if (Map.class.isAssignableFrom(wrapExpectType) && wrapRealType != null) {
                methodAccess.invoke(beanObj, setMethodName, BeanToMap.getMap(realVal));
                finished = true;
            }
            // String -> Enum 型
            else if (Enum.class.isAssignableFrom(wrapExpectType) && String.class.isAssignableFrom(wrapRealType)) {
                methodAccess.invoke(beanObj, setMethodName,
                        Enum.valueOf((Class<Enum>) wrapExpectType, (String) realVal));
                finished = true;
            }
            // Enum -> String 型
            else if (String.class.isAssignableFrom(wrapExpectType) && Enum.class.isAssignableFrom(wrapRealType)) {
                methodAccess.invoke(beanObj, setMethodName, ((Enum) realVal).name());
                finished = true;
            }
            // String -> byte[] 型
            else if (byte[].class.isAssignableFrom(wrapExpectType) && wrapRealType == String.class) {
                finished = autoRecognizeEncodeForString2Bytes(methodAccess, beanObj, setMethodName, realVal);
            }
            // Long -> Date 型
            else if (Date.class.isAssignableFrom(wrapExpectType) && wrapRealType == Long.class) {
                methodAccess.invoke(beanObj, setMethodName, new Date((Long) realVal));
                finished = true;
            } else {
                finished = false;
            }
        } catch (Throwable e) {
            System.err.println("WARN: 转换到复合类型存在异常，原因：" + ExceptionFormater.format(e));
            finished = false;
        }
        return finished;
    }

    // 自动识别不同编码下的 String -> byte[] 型
    private static boolean autoRecognizeEncodeForString2Bytes(MethodAccess methodAccess, Object beanObj,
            String setMethodName, Object realVal) {
        if (Validator.isBase64(realVal)) { // 自动识别 BASE64 String -> byte[] 型
            methodAccess.invoke(beanObj, setMethodName, DatatypeConverter.parseBase64Binary((String) realVal));
        } else if (Validator.isHex(realVal)) { // 自动识别 Hex String -> byte[] 型
            methodAccess.invoke(beanObj, setMethodName, DatatypeConverter.parseHexBinary((String) realVal));
        } else { // 默认直接获取字符串的byte[]
            methodAccess.invoke(beanObj, setMethodName, ((String) realVal).getBytes());
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private static <T> Set<T> convertListToSet(List<?> list, Class<T> expectSetType)
            throws InstantiationException, IllegalAccessException {
        Set<T> setVals = null;
        if (expectSetType == null || expectSetType == Set.class) {
            setVals = new HashSet<T>();
        } else {
            setVals = (Set<T>) expectSetType.newInstance();
        }
        setVals.addAll((Collection<? extends T>) list);
        return setVals;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> convertSetToList(Set<?> set, Class<T> expectListType)
            throws InstantiationException, IllegalAccessException {
        List<T> listVals = null;
        if (expectListType == null || expectListType == List.class) {
            listVals = new ArrayList<T>();
        } else {
            listVals = (List<T>) expectListType.newInstance();
        }
        listVals.addAll((Collection<? extends T>) set);
        return listVals;
    }

    private static void javaReflectInvokeMethod(Object beanObj, String setMethodName, Class<?> expectType,
            Object realVal) throws SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Method m = beanObj.getClass()
                .getMethod(setMethodName, expectType);
        m.invoke(beanObj, realVal);
    }

    private static Map<String, Class<?>> getBeanSetMethodNameAndTypeMap(MethodAccess methodAccess) {
        String[] methodNames = methodAccess.getMethodNames();
        Class<?>[] returnTypes = methodAccess.getReturnTypes();
        int len = methodNames.length;
        Map<String, Class<?>> targetMap = new HashMap<String, Class<?>>(len >> 1);
        for (int i = 0; i < len; i++) {
            String setMethodName = methodNames[i];
            if (setMethodName.startsWith("set")) {
                String tempFieldName = setMethodName.substring(3);
                for (int j = 0; j < len; j++) {
                    String isOrGetMethodName = methodNames[j];
                    if (isOrGetMethodName.startsWith("get") && isOrGetMethodName.substring(3)
                            .equals(tempFieldName)) {
                        // 确定该方法为bean方法
                        targetMap.put(setMethodName, returnTypes[j]);
                        break;
                    }
                    if (isOrGetMethodName.startsWith("is") && isOrGetMethodName.substring(2)
                            .equals(tempFieldName)) {
                        // 确定该方法为bean方法
                        targetMap.put(setMethodName, returnTypes[j]);
                        break;
                    }

                }
            }
        }
        return targetMap;
    }
}