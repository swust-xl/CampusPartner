package org.campus.partner.util.redis;

import java.util.List;
import java.util.Map;

/**
 * 
 * 非关系数据库Redis的CRUD接口.
 * </p>
 * 
 * @author xl
 * @since 1.0.0
 */
public interface RedisMapper {
    /**
     * 
     * 通用插入操作并设置存活时间.
     * 
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @param value
     *            需要插入的值VALUE
     * @param expiredMillis
     *            过期毫秒数.若小于等于0则不过期
     * @param <T>
     *            泛型对象
     * @param clazz
     *            需要返回的对象class
     * @return 返回插入完成后的对象。若失败，则返回null
     * @author xl
     * @since 1.0.0
     */
    <T> T insert(String keyPrefix, String keySuffix, Object value, long expiredMillis, Class<?> clazz);

    /**
     * 
     * 插入指定的bean类型.
     *
     * @param bean
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀,生成的最终KEY值会默认追加bean的类型名称，即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @param expiredMillis
     *            过期毫秒数.若小于等于0则不过期
     * @param <T>
     *            泛型对象
     * @return 返回插入完成后的对象。若失败，则返回null
     * @author xl
     * @since 1.0.0
     */
    <T> T insert(Object bean, String keySuffix, long expiredMillis);

    /**
     * 
     * 通用获取操作.
     *
     * @param keyPrefix
     *            键KEY的前缀（非必填）
     * @param keySuffix
     *            键KEY的后缀（必填）
     * @param <T>
     *            泛型对象
     * @return 返回获取到的值。若失败，则返回null
     * @author xl
     * @since 1.0.0
     */
    <T> T get(String keyPrefix, String keySuffix);

    /**
     * 
     * 通用获取Map类型操作.
     *
     * @param keyPrefix
     *            键KEY的前缀（非必填）
     * @param keySuffix
     *            键KEY的后缀（必填）
     * @return 获取到的Map类型的值
     * @author xl
     * @since 1.0.0
     */
    Map<String, Object> getAsMap(String keyPrefix, String keySuffix);

    /**
     * 
     * 通用获取Map类型操作.
     *
     * @param beanClass
     *            指定的字节码对象
     * @param keySuffixChain
     *            将要获取的key的后缀链.
     *            生成的最终KEY值会默认追加bean的类型名称，即：key=beanClassName:keySuffixChain，
     *            若此keySuffixChain已追加了bean的类型名称，则自动替换掉.
     *            支持后缀链的形式，如："1","1:2","1:2:3:...","1:*:3:..."
     * @return 获取到的Map类型的值.注：KEY后缀链的形式时,如果返回结果大于1，则返回列表中第1个Map对象
     * @author zhd
     * @since 1.3.1
     */
    Map<String, Object> getAsMap(Class<?> beanClass, String keySuffixChain);

    /**
     * 
     * 获取指定bean操作.
     *
     * @param beanClass
     *            指定的字节码对象
     * @param keySuffixChain
     *            将要获取的key的后缀链.
     *            生成的最终KEY值会默认追加bean的类型名称，即：key=beanClassName:keySuffixChain，
     *            若此keySuffixChain已追加了bean的类型名称，则自动替换掉.
     *            支持KEY后缀链的形式，如："1","1:2","1:2:3:...","1:*:3:..."
     * @param <T>
     *            泛型对象
     * @return 指定KEY后缀链的bean对象。注：KEY后缀链的形式时,如果返回结果大于1，则返回列表中第1个bean对象
     * @author xl
     * @since 1.0.0
     */
    <T> T get(Class<?> beanClass, String keySuffixChain);

    /**
     * 
     * 批量获取指定前缀的所有值.
     *
     * @param keyPrefixChain
     *            键KEY的前缀链.支持的前缀链形式，如："1","1:2:*","1:2:3:...","1:*:3:..."
     * @param <T>
     *            泛型对象
     * @return Object类型的列表.获取失败返回空列表
     * @author xl
     * @since 1.0.0
     */
    <T> List<T> multiGet(String keyPrefixChain);

    /**
     * 批量获取指定key列表的所有值.
     *
     * @param keys
     *            key列表
     * @param <T>
     *            泛型对象
     * @return Object类型的列表.获取失败返回空列表
     * @author liuqinghua
     * @since 1.2.22
     */
    <T> List<T> multiGet(List<String> keys);

    /**
     * 获取指定对象，指定key列表的所有值.
     *
     * @param keys
     *            key的列表
     * @param beanClass
     *            指定的bean对象
     * @param <T>
     *            泛型对象
     * @return keys的bean对象列表.获取失败返回空列表
     * @see cn.signit.wesign.mss.common.dao.redis.mapper.CommonRedisMapper#
     *      multiGet(java.util.List, java.lang.Class).
     * @author liuqinghua
     * @since 1.2.22
     */
    <T> List<T> multiGet(List<String> keys, Class<?> beanClass);

    /**
     * 
     * 批量获取指定前缀的Map类型所有值.
     *
     * @param keyPrefixChain
     *            键KEY的前缀链.支持的前缀链形式，如："1","1:2:*","1:2:3:...","1:*:3:..."
     * @return 获取到的Map类型的列表.获取失败返回空列表
     * @author xl
     * @since 1.0.0
     */
    List<Map<String, Object>> multiGetAsMap(String keyPrefixChain);

    /**
     * 
     * 批量获取指定bean的所有值.
     *
     * @param beanClass
     *            指定的bean对象
     * @param <T>
     *            泛型对象
     * @return bean类型的列表.获取失败返回空列表
     * @author xl
     * @since 1.0.0
     */
    <T> List<T> multiGet(Class<?> beanClass);

    /**
     * 
     * 批量获取指定bean的后缀链下所有值.
     *
     * @param beanClass
     *            指定的bean对象
     * @param keySuffixChain
     *            将要获取的key的后缀链.若为null或空，则获取beanClassName为前缀KEY的所有值
     *            生成的最终KEY值会默认追加bean的类型名称，即：key=beanClassName:keySuffixChain，
     *            若此keySuffixChain已追加了bean的类型名称，则自动替换掉.
     *            支持KEY后缀链的形式，如："1","1:2","1:2:3:...","1:*:3:..."
     * @param <T>
     *            泛型对象
     * @return bean类型的列表.获取失败返回空列表
     * @author xl
     * @since 1.0.0
     */
    <T> List<T> multiGet(Class<?> beanClass, String keySuffixChain);

    /**
     * 
     * 通用更新插入操作，如果不存在，则插入；如果存在，则更新. <br/>
     * 注：默认不更新过期时间
     * 
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @param value
     *            更新的值
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @author xl
     * @since 1.0.0
     */
    boolean upsert(String keyPrefix, String keySuffix, Object value);

    /**
     * 
     * 更新插入指定的bean，如果不存在，则插入；如果存在，则更新. <br/>
     * 注：默认不更新过期时间
     * 
     * @param bean
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @author xl
     * @since 1.0.0
     */
    boolean upsert(Object bean, String keySuffix);

    /**
     * 
     * 通用更新插入操作，如果不存在，则插入；如果存在，则更新.同时指定新的过期时间
     *
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @param value
     *            更新的值
     * @param expireMillis
     *            过期毫秒数
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @author zhd
     * @since 1.0.1
     */
    boolean upsert(String keyPrefix, String keySuffix, Object value, long expireMillis);

    /**
     * 
     * 更新插入指定的bean，如果不存在，则插入；如果存在，则更新.同时指定新的过期时间
     *
     * @param bean
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @param expireMillis
     *            过期毫秒数
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @author zhd
     * @since 1.0.1
     */
    boolean upsert(Object bean, String keySuffix, long expireMillis);

    /**
     * 
     * 通用单个精确删除操作.
     *
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    boolean delete(String keyPrefix, String keySuffix);

    /**
     * 
     * 单个指定bean类型精确删除操作.
     *
     * @param beanClass
     *            指定的字节码对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    boolean delete(Class<?> beanClass, String keySuffix);

    /**
     * 
     * 通用批量删除特定前缀模式匹配下的KEY操作.
     *
     * @param keyPrefixChain
     *            键KEY的前缀链.支持的前缀链形式，如："1","1:2:*","1:2:3:...","1:*:3:..."
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    boolean multiDelete(String keyPrefixChain);

    /**
     * 
     * 批量删除指定的key操作.
     *
     * @param keys
     *            键的列表
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.2.23
     */
    boolean multiDelete(List<String> keys);

    /**
     * 
     * 批量删除bean为前缀的所有数据.
     *
     * @param beanClass
     *            指定的字节码对象
     * @param keySuffixChain
     *            将要删除的key的后缀链.若为null或空，则删除beanClassName为前缀KEY的所有值.
     *            生成的最终KEY值会默认追加bean的类型名称，即：key=beanClassName:keySuffixChain，
     *            若此keySuffixChain已追加了bean的类型名称，则自动替换掉.
     *            支持KEY后缀链的形式，如："1","1:2","1:2:3:...","1:*:3:..."
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    boolean multiDelete(Class<?> beanClass, String keySuffixChain);

    /**
     * 
     * 通用判断是否存在指定的key.
     *
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @return 返回判断结果，存在则返回true，不存在则返回false
     * @author xl
     * @since 1.0.0
     */
    boolean hasKey(String keyPrefix, String keySuffix);

    /**
     * 
     * 判断指定的bean类型中是否存在指定的key.
     *
     * @param beanClass
     *            指定的字节码对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @return 返回判断结果，存在则返回true，不存在则返回false
     * @author xl
     * @since 1.0.0
     */
    boolean hasKey(Class<?> beanClass, String keySuffix);

    /**
     * 获取指定bean的所有key.
     *
     * @param beanClass
     *            指定的bean字节码对象
     * @return key的列表
     * @author liuqinghua
     * @since 1.0.0
     */
    List<String> getKeys(Class<?> beanClass);

    /**
     * 取指定前缀的所有key.
     *
     * @param keyPrefixChain
     *            键KEY的前缀链.支持的前缀链形式，如："1","1:2:*","1:2:3:...","1:*:3:..."
     * @return key的列表
     * @author liuqinghua
     * @since 1.0.0
     */
    List<String> getKeys(String keyPrefixChain);

}
