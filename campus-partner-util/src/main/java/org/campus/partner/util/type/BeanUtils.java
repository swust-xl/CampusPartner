package org.campus.partner.util.type;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 
 * 自定义Bean Util工具.
 * </p>
 *
 * @author xl
 * @since 1.2.7
 */
@Service
public class BeanUtils implements ApplicationContextAware {
    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    /**
     * 
     * 根据类型获取容器内的bean.
     *
     * @param beanClass
     *            bean类型
     * @return bean
     * @author xl
     * @since 1.2.7
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * 
     * 根据类型和bean名称获取容器内的bean.
     *
     * @param beanClass
     *            bean类型
     * @param name
     *            bean名称
     * @return bean
     * @author xl
     * @since 1.2.7
     */
    public static <T> T getBean(Class<T> beanClass, String name) {
        return context.getBean(name, beanClass);
    }
}