package io.github.autumnforest.boot.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil implements ApplicationContextAware {
    private static ObjectMapper mapper = new ObjectMapper();

    public static void setMapper(ObjectMapper objectMapper) {
        mapper = objectMapper;
    }

    @SneakyThrows
    public static String obj2Str(Object o) {
        return mapper.writeValueAsString(o);
    }

    @SneakyThrows
    public static <T> T str2Obj(String s, Class<T> clazz) {
        return mapper.readValue(s, clazz);
    }

    @SneakyThrows
    public static <T> T str2Obj(String s, TypeReference<T> ref) {
        return mapper.readValue(s, ref);
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mapper = applicationContext.getBean(ObjectMapper.class);
    }
}
