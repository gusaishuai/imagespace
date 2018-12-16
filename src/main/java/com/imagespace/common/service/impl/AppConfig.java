package com.imagespace.common.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gusaishuai
 * @since 2018/12/16
 */
@Configuration
public class AppConfig {

    @Value("${redis.url}")
    private String redisUrl;

    @Bean
    public RedisPool initRedisPool() {
        return new RedisPool(redisUrl);
    }

}
