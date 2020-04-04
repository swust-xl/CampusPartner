package org.campus.partner.util.type;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 原始类型和包装类型的转换.
 * <p/>
 * 代码参考{@link https://github.com/google/guava}中的实现.
 *
 * @see <a href=
 *      "https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Primitives.java">
 *      https://github.com/google/guava/blob/master/guava/src/com/google/common/primitives/Primitives.java</a>
 * @author xl
 * @since 1.0.0
 */
public abstract class Primitives {

    /**
     * 构建原始类型到包装类型的映射. KEY - 原始类型. VALUE - 包装类型.
     * 
     */
    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;

    /**
     * 构建包装类型到原始类型的映射. KEY - 包装类型. VALUE - 原始类型.
     * 
     */
    private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

    static {
        Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
        Map<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>(16);

        add(primToWrap, wrapToPrim, boolean.class, Boolean.class);
        add(primToWrap, wrapToPrim, byte.class, Byte.class);
        add(primToWrap, wrapToPrim, char.class, Character.class);
        add(primToWrap, wrapToPrim, double.class, Double.class);
        add(primToWrap, wrapToPrim, float.class, Float.class);
        add(primToWrap, wrapToPrim, int.class, Integer.class);
        add(primToWrap, wrapToPrim, long.class, Long.class);
        add(primToWrap, wrapToPrim, short.class, Short.class);
        add(primToWrap, wrapToPrim, void.class, Void.class);

        PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
        WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
    }

    private static void add(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key,
            Class<?> value) {
        forward.put(key, value);
        backward.put(value, key);
    }

    /**
     * 返回所有原始类型(包含{@code void})的集合.
     *
     * @return 原始类型的集合.
     * @author xl
     * @since 1.0.0
     */
    public static Set<Class<?>> allPrimitiveTypes() {
        return PRIMITIVE_TO_WRAPPER_TYPE.keySet();
    }

    /**
     * 返回所有包装类型(包含{@code Void})的集合.
     *
     * @return 包装类型的集合.
     * @author xl
     * @since 1.0.0
     */
    public static Set<Class<?>> allWrapperTypes() {
        return WRAPPER_TO_PRIMITIVE_TYPE.keySet();
    }

    /**
     * 判断是否为包装类型.
     *
     * @param type
     *            待判断的类型字节码对象
     * @see Class#isPrimitive
     * @return 如果是包装类型，返回{@code true}；否则返回{@code false}
     * @author xl
     * @since 1.0.0
     */
    public static boolean isWrapperType(Class<?> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(type);
    }

    /**
     * 如果{@code type}为原始类型，则返回 {@code type} 对应的包装类型; 否则返回{@code type}本身.
     *
     * <pre>
     *     wrap(int.class) == Integer.class
     *     wrap(Integer.class) == Integer.class
     *     wrap(String.class) == String.class
     * </pre>
     * 
     * @param type
     *            待包装的类型
     * @param <T>
     *            参见 {@link the type of the class modeled by this Class object.
     *            For example, the type of String.class is Class<String>. Use
     *            Class<?> if the class being modeled is unknown}
     * @return 包装后的类型
     * @throws NullPointerException
     *             如果{@code type}为null，则抛出该异常.
     * @author xl
     * @since 1.0.0
     */
    public static <T> Class<T> wrap(Class<T> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        // cast is safe: long.class and Long.class are both of type Class<Long>
        @SuppressWarnings("unchecked")
        Class<T> wrapped = (Class<T>) PRIMITIVE_TO_WRAPPER_TYPE.get(type);
        return (wrapped == null) ? type : wrapped;
    }

    /**
     * 如果{@code type}为包装类型，则返回 {@code type} 对应的原始类型; 否则返回{@code type}本身.
     *
     * <pre>
     *     unwrap(Integer.class) == int.class
     *     unwrap(int.class) == int.class
     *     unwrap(String.class) == String.class
     * </pre>
     * 
     * @param type
     *            待解包的类型
     * @param <T>
     *            参见{@link the type of the class modeled by this Class object.
     *            For example, the type of String.class is Class<String>. Use
     *            Class<?> if the class being modeled is unknown}
     * @return 原始类型
     * @throws NullPointerException
     *             如果{@code type}为null，则抛出该异常.
     * @author xl
     * @since 1.0.0
     */
    public static <T> Class<T> unwrap(Class<T> type) {
        if (type == null) {
            throw new NullPointerException();
        }
        // cast is safe: long.class and Long.class are both of type Class<Long>
        @SuppressWarnings("unchecked")
        Class<T> unwrapped = (Class<T>) WRAPPER_TO_PRIMITIVE_TYPE.get(type);
        return (unwrapped == null) ? type : unwrapped;
    }

    /**
     * 判断指定类型是否为基本类型.
     *
     * @param type
     *            待判断的类型字节码对象
     * @return 如果是基本类型，返回{@code true}；否则返回{@code false}
     * @author xl
     * @since 1.0.0
     */
    public static <T> boolean isBasicType(Class<T> type) {
        return PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type) || PRIMITIVE_TO_WRAPPER_TYPE.containsValue(type);
    }
}