package org.campus.partner.util.type;

import java.lang.reflect.Field;

import org.campus.partner.util.ExceptionFormater;

import sun.misc.Unsafe;

/**
 * sun.misc.Unsafe的简单封装.
 * </p>
 * 注意：在后面的JDK版本中可能会禁用.
 * 
 * @author xl
 * @since 1.0.0
 */
@SuppressWarnings("restriction")
public class UnsafeUtil {
    private UnsafeUtil() {}

    /**
     * Unsafe单例模式获取.
     * 
     * @author xl
     * @since 1.0.0
     */
    private static enum UnsafeUtilSingle {
        INSTANCE;
        private Unsafe unsafe;

        UnsafeUtilSingle() {
            try {
                Field f = Unsafe.class.getDeclaredField("theUnsafe");
                f.setAccessible(true);
                unsafe = (Unsafe) f.get(null);
            } catch (Throwable e) {
                throw new ExceptionInInitializerError(ExceptionFormater.format(e));
            }
        }

        public Unsafe getUnsafe() {
            return this.unsafe;
        }
    }

    /**
     * 获取Unsafe对象.
     *
     * @return 单例模式下的Unsafe对象.
     * @author xl
     * @since 1.0.0
     */
    public static Unsafe getUnsafe() {
        return UnsafeUtilSingle.INSTANCE.getUnsafe();
    }

    /**
     * 强制实例化一个对象（不需要通过构造方法，但存在一些安全风险）.
     *
     * @param objClass
     *            待实例化的字节码类型
     * @return 实例化的对象
     * @throws InstantiationException
     *             实例化异常.
     * @author xl
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    public static <T> T forceInstance(Class<?> objClass) throws InstantiationException {
        return (T) getUnsafe().allocateInstance(objClass);
    }
}
