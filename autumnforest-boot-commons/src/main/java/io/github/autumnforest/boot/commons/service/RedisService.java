package io.github.autumnforest.boot.commons.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

public class RedisService {
    final
    StringRedisTemplate stringRedisTemplate;
    final
    ObjectMapper objectMapper;

    public RedisService(StringRedisTemplate stringRedisTemplate, ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }


    public <V> V get(String key, Class<V> clazz) throws JsonProcessingException {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = StringUtils.strip(value, "\"");
        }
        return objectMapper.readValue(value, clazz);
    }

    public String get(String key) throws JsonProcessingException {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void set(String key, Object value) throws JsonProcessingException {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, (String) value);
        } else {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value));
        }
    }

    public void set(String key, Object value, long timeout, TimeUnit unit) throws JsonProcessingException {
        if (value instanceof String) {
            stringRedisTemplate.opsForValue().set(key, (String) value, timeout, unit);
        } else {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(value), timeout, unit);
        }
    }
}
