package io.github.autumnforest.boot.commons.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisAutoConfig {

    @Bean
    @ConditionalOnBean
    public RedisService redisService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        return new RedisService(stringRedisTemplate, objectMapper);
    }
}
