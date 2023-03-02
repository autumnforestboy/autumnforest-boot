package io.github.autumnforest.boot.commons.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableAspectJAutoProxy
@Aspect
@ConditionalOnProperty(prefix="aspect.request.log", name = "enable", havingValue = "true", matchIfMissing = true)
@Slf4j
public class RequestLog  implements PriorityOrdered {
    @Autowired
    ObjectMapper objectMapper;

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Slf4j
    @Data
    private static class LogInfo {
        private long startTime;
        private String path = null;
        private String className = null;
        private String methodName = null;
        private StringBuilder args = new StringBuilder();
        private String result = null;
        private Long take = null;

        public void log() {
            log.info("className:{}, methodName:{}", className, methodName);
            log.info("take:{}, path:{}", take, path);
            log.info("args: {}", args);
            log.info("return:{}", result);
        }
    }

    private static final ThreadLocal<LogInfo> THREAD_LOCAL = new ThreadLocal<>();

    @Before(value = "@annotation(mapping)")
    public void before(JoinPoint joinPoint, PostMapping mapping) throws JsonProcessingException {
        before(joinPoint);
    }
    @Before(value = "@annotation(mapping)")
    public void before(JoinPoint joinPoint, GetMapping mapping) throws JsonProcessingException {
        before(joinPoint);
    }
    @Before(value = "@annotation(mapping)")
    public void before(JoinPoint joinPoint, DeleteMapping mapping) throws JsonProcessingException {
        before(joinPoint);
    }
    @Before(value = "@annotation(mapping)")
    public void before(JoinPoint joinPoint, PutMapping mapping) throws JsonProcessingException {
        before(joinPoint);
    }

    @Before(value = "@annotation(mapping)")
    public void before(JoinPoint joinPoint, RequestMapping mapping) throws JsonProcessingException {
        before(joinPoint);
    }

    private void before(JoinPoint joinPoint) throws JsonProcessingException {
        LogInfo logInfo = new LogInfo();
        logInfo.setStartTime(System.currentTimeMillis());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        logInfo.setPath(request.getRequestURL().toString());
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        Object[] args = joinPoint.getArgs();
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames(); // 参数名
        // 设置方法名称
        logInfo.setClassName(joinPoint.getTarget().getClass().getSimpleName());
        logInfo.setMethodName(joinPoint.getSignature().getName());
        for (int i = 0; i < args.length; i++) {
            Object o = args[i];
            if (!Objects.isNull(o) && !isFilterObject(o)) {
                logInfo.getArgs().append(" | ").append(argNames[i]).append(":").append(StringUtils.substringBefore(objectMapper.writeValueAsString(o), 2000)).append(" | ");
            }
        }
        THREAD_LOCAL.set(logInfo);
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "@annotation(mapping)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, PostMapping mapping, Object jsonResult) {
        after(jsonResult);
    }
    @AfterReturning(pointcut = "@annotation(mapping)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, GetMapping mapping, Object jsonResult) {
        after(jsonResult);
    }
    @AfterReturning(pointcut = "@annotation(mapping)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, DeleteMapping mapping, Object jsonResult) {
        after(jsonResult);
    }

    @AfterReturning(pointcut = "@annotation(mapping)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, PutMapping mapping, Object jsonResult) {
        after(jsonResult);
    }
    @AfterReturning(pointcut = "@annotation(mapping)", returning = "jsonResult")
    public void afterReturning(JoinPoint joinPoint, RequestMapping mapping, Object jsonResult) {
        after(jsonResult);
    }


    /**
     * 拦截异常操作
     *
     * @param joinPoint 切点
     * @param e         异常
     */
    @AfterThrowing(value = "@annotation(mapping)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, PostMapping mapping, Exception e) {
        after(null);
    }
    @AfterThrowing(value = "@annotation(mapping)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, GetMapping mapping, Exception e) {
        after(null);
    }
    @AfterThrowing(value = "@annotation(mapping)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, DeleteMapping mapping, Exception e) {
        after(null);
    }
    @AfterThrowing(value = "@annotation(mapping)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, PutMapping mapping, Exception e) {
        after(null);
    }
    @AfterThrowing(value = "@annotation(mapping)", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, RequestMapping mapping, Exception e) {
        after(null);
    }

    private void after(Object r) {
        try {
            LogInfo logInfo = THREAD_LOCAL.get();
            if (r != null) {
                logInfo.setResult(objectMapper.writeValueAsString(r));
            }
            logInfo.setTake(System.currentTimeMillis() - logInfo.getStartTime());

            logInfo.log();
        } catch (Exception e) {
            log.error("aop log err.", e);
        } finally {
            THREAD_LOCAL.remove();
        }
    }


    /**
     * 判断是否需要过滤的对象。
     *
     * @param o 对象信息。
     * @return 如果是需要过滤的对象，则返回true；否则返回false。
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(final Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.entrySet()) {
                Map.Entry entry = (Map.Entry) value;
                return entry.getValue() instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }
}
