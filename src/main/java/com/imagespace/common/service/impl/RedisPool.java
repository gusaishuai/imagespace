package com.imagespace.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

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

    private boolean valid(String... strs) {
        if (jedis == null) {
            log.warn("jedis is not initial yet");
            return false;
        } else if (StringUtils.isAnyBlank(strs)) {
            log.warn("redis key-value either blank");
            return false;
        }
        return true;
    }

}
