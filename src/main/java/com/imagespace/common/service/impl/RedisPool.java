package com.imagespace.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Slf4j
public class RedisPool {

    private Jedis jedis;

    public RedisPool(String redisUrl) {
        this.jedis = new Jedis(redisUrl);
    }

    public void set(String key, String value, int seconds) {
        if (!valid(key, value)) {
            return;
        }
        jedis.setex(key, seconds, value);
    }

    public String get(String key) {
        if (!valid(key)) {
            return null;
        }
        return jedis.get(key);
    }

    public void del(String key) {
        if (!valid(key)) {
            return;
        }
        jedis.del(key);
    }

    public boolean keyExist(String key) {
        Boolean exists = jedis.exists(key);
        return exists == null ? false : exists;
    }

    public int listLength(String key) {
        Long length = jedis.llen(key);
        return length == null ? 0 : length.intValue();
    }

    public void setList(String key, List<String> valueList, int seconds) {
        if (!valid(key, valueList)) {
            return;
        }
        String[] tempArray = new String[valueList.size()];
        jedis.rpush(key, valueList.toArray(tempArray));
        if (seconds > 0) {
            jedis.expire(key, seconds);
        }
    }

    public List<String> getList(String key, int start, int end) {
        if (!valid(key)) {
            return null;
        }
        return jedis.lrange(key, start, end);
    }

    private boolean valid(Object... obj) {
        if (jedis == null) {
            log.warn("jedis is not initial yet");
            return false;
        }
        for (Object o : obj) {
            if (Objects.isNull(o)) {
                log.warn("redis key-value either null");
                return false;
            } else if (o instanceof List && CollectionUtils.isEmpty((List) o)) {
                log.warn("redis list empty");
                return false;
            }
        }
        return true;
    }

}
