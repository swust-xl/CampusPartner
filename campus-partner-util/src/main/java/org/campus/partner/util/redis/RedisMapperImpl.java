package org.campus.partner.util.redis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.campus.partner.util.Validator;
import org.campus.partner.util.string.JsonConverter;
import org.campus.partner.util.type.BeanToMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

/**
 * 
 * 非关系数据库Redis的CRUD实现.
 * </p>
 * 
 * @author xl
 * @since 1.0.0
 */
@Repository
public class RedisMapperImpl implements RedisMapper {
    @Autowired
    private RedisTemplate<String, String> rawJsonStringRedisTemplate;

    @Autowired
    @SuppressWarnings("rawtypes")
    private RedisScript<List> script;

    private static final Logger LOG = LoggerFactory.getLogger(RedisMapperImpl.class);

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
    @Override
    @SuppressWarnings("unchecked")
    public <T> T insert(String keyPrefix, String keySuffix, Object value, long expiredMillis, Class<?> clazz) {
        String key = getKey(keyPrefix, keySuffix);
        if (Validator.isEmpty(key)) {
            LOG.error("keyPrefix和keySuffix不可同时为空");
            return null;
        }
        BoundValueOperations<String, String> valOps = rawJsonStringRedisTemplate.boundValueOps(key);
        if (expiredMillis <= 0) {
            valOps.set(JsonConverter.encodeAsStringWithDefaultModules(value));
        } else {
            valOps.set(JsonConverter.encodeAsStringWithDefaultModules(value), expiredMillis, TimeUnit.MILLISECONDS);
        }
        Object obj = valOps.get();
        Validator.notNull(obj);
        return (T) value;
    }

    /**
     * 
     * 插入指定的bean类型.
     *
     * @param bean
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @param expiredMillis
     *            过期毫秒数.若小于等于0则不过期
     * @param <T>
     *            泛型对象
     * @return 返回插入完成后的对象。若失败，则返回null
     * @author xl
     * @since 1.0.0
     */
    @Override
    public <T> T insert(Object bean, String keySuffix, long expiredMillis) {
        if (Validator.isEmpty(keySuffix) || bean == null) {
            return null;
        }
        return insert(bean.getClass()
                .getSimpleName(),
                keySuffix.replace(bean.getClass()
                        .getSimpleName()
                        .concat(":"), ""),
                bean, expiredMillis, bean.getClass());
    }

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
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String keyPrefix, String keySuffix) {
        return (T) JsonConverter.decode(getRawJsonStringValue(keyPrefix, keySuffix));
    }

    // 获取原始JSON字符串值
    private String getRawJsonStringValue(String keyPrefix, String keySuffix) {
        String key = getKey(keyPrefix, keySuffix);
        if (Validator.isEmpty(key)) {
            LOG.error("keyPrefix和keySuffix不可同时为空");
            return null;
        }
        return rawJsonStringRedisTemplate.boundValueOps(key)
                .get();
    }

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
    @Override
    public Map<String, Object> getAsMap(String keyPrefix, String keySuffix) {
        String ret = getRawJsonStringValue(keyPrefix, keySuffix);
        if (Validator.isEmpty(ret)) {
            return null;
        }
        return JsonConverter.decodeAsMap(ret);
    }

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
     * @see cn.signit.wesign.mss.common.dao.redis.mapper.CommonRedisMapper#getAsMap(java.lang.Class,
     *      java.lang.String).
     * @author xl
     * @since 1.3.1
     */
    @Override
    public Map<String, Object> getAsMap(Class<?> beanClass, String keySuffixChain) {
        return BeanToMap.getMap(get(beanClass, keySuffixChain));
    }

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
    @Override
    public <T> T get(Class<?> beanClass, String keySuffixChain) {
        if (Validator.isEmpty(keySuffixChain) || beanClass == null) {
            return null;
        }
        String keyPrefix = beanClass.getSimpleName();
        String ret = null;
        if (!keySuffixChain.contains("*")) {
            ret = getRawJsonStringValue(keyPrefix, keySuffixChain.replace(keyPrefix.concat(":"), ""));
        } else {
            List<String> mapList = rawJsonStringMultiGet(keyPrefix.concat(":")
                    .concat(keySuffixChain.replace(keyPrefix.concat(":"), "")));
            if (Validator.isEmpty(mapList)) {
                return null;
            }
            ret = mapList.get(0);
        }
        if (Validator.isEmpty(ret)) {
            return null;
        }

        return JsonConverter.decodeAsBean(ret, beanClass);
    }

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
    @Override
    public <T> List<T> multiGet(String keyPrefixChain) {
        List<String> keys = getKeys(keyPrefixChain);
        return multiGet(keys);
    }

    // 获取原始JSON字符串值列表
    private List<String> rawJsonStringMultiGet(String keyPrefixChain) {
        List<String> keys = getKeys(keyPrefixChain);
        return rawJsonStringMultiGet(keys);
    }

    /**
     * 批量获取指定key列表的所有值.
     *
     * @param keys
     *            key列表
     * @param <T>
     *            泛型对象
     * @return Object类型的列表.获取失败返回空列表
     * @author xl
     * @since 1.2.22
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> multiGet(List<String> keys) {
        List<String> beans = rawJsonStringMultiGet(keys);
        return (List<T>) beans.stream()
                .map(json -> JsonConverter.decode(json))
                .collect(Collectors.toList());
    }

    // 获取原始JSON字符串值列表
    private List<String> rawJsonStringMultiGet(List<String> keys) {
        List<String> beanJsonList = rawJsonStringRedisTemplate.opsForValue()
                .multiGet(keys);
        if (Validator.isEmpty(beanJsonList)) {
            return Collections.emptyList();
        }
        return beanJsonList;
    }

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
     * @author xl
     * @since 1.2.22
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> multiGet(List<String> keys, Class<?> beanClass) {
        List<String> beanJsonList = rawJsonStringMultiGet(keys);
        if (Validator.isEmpty(beanJsonList)) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<T>(beanJsonList.size());
        for (String json : beanJsonList) {
            if (Validator.isEmpty(json)) {
                continue;
            }
            Object bean = JsonConverter.decodeAsBean(json, beanClass);
            if (bean != null) {
                resultList.add((T) bean);
            }
        }
        return resultList;
    }

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
    @Override
    public List<Map<String, Object>> multiGetAsMap(String keyPrefixChain) {
        if (Validator.isEmpty(keyPrefixChain)) {
            LOG.error("keyPrefixChain为空");
            return Collections.emptyList();
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<String> objectList = rawJsonStringMultiGet(keyPrefixChain);
        for (String json : objectList) {
            if (Validator.isEmpty(json)) {
                continue;
            }
            Map<String, Object> map = JsonConverter.decodeAsMap(json);
            if (!Validator.isEmpty(map)) {
                resultList.add(map);
            }
        }
        return resultList;
    }

    /**
     * 
     * 批量获取指定bean的所有值.
     *
     * @param beanClass
     *            指定的bean对象
     * @param <T>
     *            泛型对象
     * @return bean类型的列表
     * @author xl
     * @since 1.0.0
     */
    @Override
    public <T> List<T> multiGet(Class<?> beanClass) {
        return multiGet(beanClass, null);
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public <T> List<T> multiGet(Class<?> beanClass, String keySuffixChain) {
        if (beanClass == null) {
            return Collections.emptyList();
        }
        String keyPrefix = beanClass.getSimpleName();
        if (!Validator.isEmpty(keySuffixChain)) {
            keyPrefix = keyPrefix.concat(":")
                    .concat(keySuffixChain.replace(keyPrefix.concat(":"), ""));
        }
        List<String> beanJsonList = rawJsonStringMultiGet(keyPrefix);
        if (Validator.isEmpty(beanJsonList)) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<T>(beanJsonList.size());
        for (String beanJson : beanJsonList) {
            if (Validator.isEmpty(beanJson)) {
                continue;
            }
            Object bean = JsonConverter.decodeAsBean(beanJson, beanClass);
            if (bean != null) {
                resultList.add((T) bean);
            }
        }
        return resultList;
    }

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
    @Override
    public boolean upsert(String keyPrefix, String keySuffix, Object value) {
        return upsert(keyPrefix, keySuffix, value, -1L);
    }

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
    @Override
    public boolean upsert(Object bean, String keySuffix) {
        return upsert(bean, keySuffix, -1L);
    }

    /**
     * 
     * 通用更新插入操作，如果不存在，则插入；如果存在，则更新.同时指定新的过期时间
     *
     * @param keyPrefix
     *            键的前缀
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @param value
     *            更新的值
     * @param expireMillis
     *            过期毫秒数
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @see cn.signit.wesign.mss.common.dao.redis.mapper.CommonRedisMapper#upsert(java.lang.String,
     *      java.lang.String, java.lang.Object, java.lang.long).
     * @author xl
     * @since 1.0.1
     */
    @Override
    public boolean upsert(String keyPrefix, String keySuffix, Object value, long expireMillis) {
        String key = getKey(keyPrefix, keySuffix);
        if (Validator.isEmpty(key)) {
            LOG.error("keyPrefix和keySuffix不可同时为空");
            return false;
        }
        if (!rawJsonStringRedisTemplate.hasKey(key)) {
            return insert(keyPrefix, keySuffix, value, expireMillis, value.getClass()) != null;
        } else {
            BoundValueOperations<String, String> valOps = rawJsonStringRedisTemplate.boundValueOps(key);
            if (expireMillis > 0) {
                valOps.set(JsonConverter.encodeAsStringWithDefaultModules(value), expireMillis, TimeUnit.MILLISECONDS);
                return true;
            } else {
                long currentExpireMillis = rawJsonStringRedisTemplate.getExpire(key, TimeUnit.MILLISECONDS);
                if (currentExpireMillis > 0) {
                    valOps.set(JsonConverter.encodeAsStringWithDefaultModules(value), currentExpireMillis,
                            TimeUnit.MILLISECONDS);
                    return true;
                } else {
                    valOps.set(JsonConverter.encodeAsStringWithDefaultModules(value));
                    return true;
                }
            }
        }
    }

    /**
     * 
     * 更新插入指定的bean，如果不存在，则插入；如果存在，则更新.同时指定新的过期时间.
     *
     * @param bean
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @param expireMillis
     *            过期毫秒数
     * @return 返回更新（或插入结果），成功则返回true，失败则返回false
     * @see cn.signit.wesign.mss.common.dao.redis.mapper.CommonRedisMapper#upsert(java.lang.Object,
     *      java.lang.String, java.lang.long).
     * @author xl
     * @since 1.0.1
     */
    @Override
    public boolean upsert(Object bean, String keySuffix, long expireMillis) {
        if (Validator.isEmpty(keySuffix) || bean == null) {
            return false;
        }
        return upsert(bean.getClass()
                .getSimpleName(),
                keySuffix.replace(bean.getClass()
                        .getSimpleName()
                        .concat(":"), ""),
                bean, expireMillis);
    }

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
    @Override
    public boolean delete(String keyPrefix, String keySuffix) {
        String key = getKey(keyPrefix, keySuffix);
        if (Validator.isEmpty(key)) {
            LOG.error("keyPrefix和keySuffix不可同时为空");
            return false;
        }
        rawJsonStringRedisTemplate.delete(key);
        return true;
    }

    /**
     * 
     * 单个指定bean类型精确删除操作.
     *
     * @param beanClass
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    @Override
    public boolean delete(Class<?> beanClass, String keySuffix) {
        if (Validator.isEmpty(keySuffix) || beanClass == null) {
            return false;
        }
        return delete(beanClass.getSimpleName(), keySuffix.replace(beanClass.getSimpleName()
                .concat(":"), ""));
    }

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
    @Override
    @SuppressWarnings("unchecked")
    public boolean multiDelete(String keyPrefixChain) {
        if (Validator.isEmpty(keyPrefixChain)) {
            return false;
        }
        PatternKeys callBack = new PatternKeys(script.getScriptAsString(), 1);
        callBack.setParams(keyPrefixChain);
        List<String> keys = rawJsonStringRedisTemplate.execute(callBack);
        if (!keys.isEmpty()) {
            rawJsonStringRedisTemplate.delete(keys);
            return true;
        } else {
            return false;
        }
    }

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
    public boolean multiDelete(List<String> keys) {
        if (Validator.isEmpty(keys)) {
            return false;
        }
        rawJsonStringRedisTemplate.delete(keys);
        return true;
    }

    /**
     * 
     * 批量删除bean为前缀的所有数据.
     *
     * @param beanClass
     *            指定的bean对象
     * @param keySuffixChain
     *            将要删除的key的后缀链.若为null或空，则删除beanClassName为前缀KEY的所有值.
     *            生成的最终KEY值会默认追加bean的类型名称，即：key=beanClassName:keySuffixChain，
     *            若此keySuffixChain已追加了bean的类型名称，则自动替换掉.
     *            支持KEY后缀链的形式，如："1","1:2","1:2:3:...","1:*:3:..."
     * @return 返回删除结果，成功则返回true，失败返回false
     * @author xl
     * @since 1.0.0
     */
    @Override
    public boolean multiDelete(Class<?> beanClass, String keySuffixChain) {
        if (beanClass == null) {
            LOG.error("beanClass为空");
            return false;
        }
        String key = beanClass.getSimpleName();
        if (!Validator.isEmpty(keySuffixChain)) {
            key = key.concat(":")
                    .concat(keySuffixChain.replace(key.concat(":"), ""));
        }
        return multiDelete(key);
    }

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
    @Override
    public boolean hasKey(String keyPrefix, String keySuffix) {
        String key = getKey(keyPrefix, keySuffix);
        if (Validator.isEmpty(key)) {
            LOG.error("keyPrefix和keySuffix不可同时为空");
            return false;
        }
        Boolean hasKey = rawJsonStringRedisTemplate.hasKey(key);
        return hasKey != null && hasKey.booleanValue();
    }

    /**
     * 
     * 判断指定的bean类型中是否存在指定的key.
     *
     * @param beanClass
     *            指定的bean对象
     * @param keySuffix
     *            键KEY的后缀, 生成的最终KEY值会默认追加bean的类型名称，
     *            即：key=beanCalssName:keySuffix，若此id已追加了bean类型的名称，则自动替换掉
     * @return 返回判断结果，存在则返回true，不存在则返回false
     * @author xl
     * @since 1.0.0
     */
    @Override
    public boolean hasKey(Class<?> beanClass, String keySuffix) {
        if (Validator.isEmpty(keySuffix) || beanClass == null) {
            LOG.error("beanClass或id为空");
            return false;
        }
        return hasKey(beanClass.getSimpleName(), keySuffix.replace(beanClass.getSimpleName()
                .concat(":"), ""));
    }

    /**
     * 
     * 键的构造.
     *
     * @param keyPrefix
     *            键KEY的前缀(非必填)
     * @param keySuffix
     *            键KEY的后缀(必填)
     * @return 返回由prefix和id合成的键
     * @author xl
     * @since 1.0.0
     */
    private String getKey(String keyPrefix, String keySuffix) {
        if (Validator.isEmpty(keySuffix)) {
            return null;
        }
        String key = null;
        if (keyPrefix == null || keyPrefix.trim()
                .isEmpty()) {
            key = keySuffix;
        } else {
            key = keyPrefix.concat(":")
                    .concat(keySuffix);
        }
        return key;
    }

    /**
     * 获取指定bean的所有key.
     *
     * @param beanClass
     *            指定的bean字节码对象
     * @return key的列表
     * @author xl
     * @since 1.2.22
     */
    @Override
    public List<String> getKeys(Class<?> beanClass) {
        return getKeys(beanClass.getSimpleName());
    }

    /**
     * 获取指定前缀的所有key.
     *
     * @param keyPrefixChain
     *            键KEY的前缀链.支持的前缀链形式，如："1","1:2:*","1:2:3:...","1:*:3:..."
     * @return key的列表
     * @author xl
     * @since 1.2.22
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getKeys(String keyPrefixChain) {
        if (Validator.isEmpty(keyPrefixChain)) {
            return Collections.emptyList();
        }
        PatternKeys callback = new PatternKeys(script.getScriptAsString(), 1);
        callback.setParams(keyPrefixChain);
        List<String> keys = rawJsonStringRedisTemplate.execute(callback);
        return keys;
    }

}