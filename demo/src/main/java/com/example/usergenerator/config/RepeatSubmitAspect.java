package com.example.usergenerator.config;

import com.example.usergenerator.annotation.RepeatSubmit;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.example.usergenerator.util.JwtUtil;

@Slf4j
@Aspect
@Component
public class RepeatSubmitAspect {

    private final HttpServletRequest request;
    private final JwtUtil jwtUtil;
    private final Map<String, Long> submitRecordMap = new ConcurrentHashMap<>();

    public RepeatSubmitAspect(HttpServletRequest request, JwtUtil jwtUtil) {
        this.request = request;
        this.jwtUtil = jwtUtil;
    }

    @Around("@annotation(com.example.usergenerator.annotation.RepeatSubmit)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RepeatSubmit annotation = method.getAnnotation(RepeatSubmit.class);

        if (annotation != null) {
            long timeout = annotation.timeout();
            String key = getRepeatSubmitKey(request);

            long currentTime = System.currentTimeMillis();
            Long lastSubmitTime = submitRecordMap.get(key);
            
            if (lastSubmitTime != null && (currentTime - lastSubmitTime) < timeout) {
                throw new BusinessException(ResultCode.DUPLICATE_REQUEST);
            }

            submitRecordMap.put(key, currentTime);
            
            cleanExpiredRecords(currentTime, timeout);
        }

        return joinPoint.proceed();
    }

    private String getRepeatSubmitKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String token = request.getHeader("Authorization");
        String userId = "anonymous";
        if (token != null && token.startsWith("Bearer ")) {
            try {
                userId = String.valueOf(jwtUtil.getUserId(token.substring(7)));
            } catch (Exception ignored) {
            }
        }
        return "repeat_submit:" + uri + ":" + userId;
    }

    private void cleanExpiredRecords(long currentTime, long timeout) {
        submitRecordMap.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > timeout * 2
        );
    }
}
