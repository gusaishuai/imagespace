package com.imagespace.common.service.impl;

import com.imagespace.login.controller.CaptchaController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Component
public class RedisPool {

    private static final Logger logger = LoggerFactory.getLogger(RedisPool.class);

    private static Jedis jedis = null;

    @Value("redis.url")
    private String redisUrl;
    @Value("redis.port")
    private String redisPort;

    RedisPool() {
        if (jedis != null) {
            return;
        }
        if (StringUtils.isBlank(redisPort)) {
            jedis = new Jedis(redisUrl);
        } else {
            jedis = new Jedis(redisUrl, Integer.valueOf(redisPort));
        }
    }

    public void set(String key, String value) {
        if (jedis == null) {
            logger.warn("jedis is not initial yet");
            return;
        }
        jedis.set(key, value);
    }

    public void set(String key, String value, int seconds) {
        if (jedis == null) {
            logger.warn("jedis is not initial yet");
            return;
        }
        jedis.setex(key, seconds, value);
    }

    public String get(String key) {
        if (jedis == null) {
            logger.warn("jedis is not initial yet");
            return null;
        }
        return jedis.get(key);
    }

}
