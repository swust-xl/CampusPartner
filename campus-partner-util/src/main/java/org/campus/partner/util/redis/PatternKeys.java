package org.campus.partner.util.redis;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;

import redis.clients.jedis.Jedis;

/**
 * 
 * 自定义Redis回调.
 * </p>
 *
 * @author xl
 * @since 1.2.7
 */
@SuppressWarnings("rawtypes")
public class PatternKeys implements RedisCallback<List> {
    private String script;
    private int keyCount;
    private String params;

    /**
     * 
     * 根据脚本，参数个数，参数生成一个callback.
     *
     * @param script
     *            脚本
     * @param keyCount
     *            参数个数
     * @param params
     *            参数
     */
    public PatternKeys(String script, int keyCount, String params) {
        this.script = script;
        this.keyCount = keyCount;
        this.params = params;
    }

    /**
     * 
     * 根据脚本生成一个callback.
     *
     * @param script
     *            脚本
     */
    // 查询整个库，慎用，
    public PatternKeys(String script) {
        this(script, 0, "");
    }

    /**
     * 
     * 根据脚本，参数个数生成一个callback.
     *
     * @param script
     *            脚本
     * @param keyCount
     *            参数个数
     */
    // 查询整个库，慎用，
    public PatternKeys(String script, int keyCount) {
        this(script, keyCount, "");
    }

    public void setScript(String script) {
        this.script = script;
    }

    public void setKeyCount(int keyCount) {
        this.keyCount = keyCount;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Override
    public List doInRedis(RedisConnection connection) throws DataAccessException {
        Jedis nativeConn = (Jedis) connection.getNativeConnection();
        return (List) nativeConn.eval(script, keyCount, params);
    }
}
