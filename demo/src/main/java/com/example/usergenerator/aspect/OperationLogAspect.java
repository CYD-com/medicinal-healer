package com.example.usergenerator.aspect;

import com.example.usergenerator.annotation.LogOperation;
import com.example.usergenerator.service.OperationLogService;
import com.example.usergenerator.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final JwtUtil jwtUtil;

    @Around("@annotation(com.example.usergenerator.annotation.LogOperation)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String status = "success";
        String errorMsg = null;

        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            status = "fail";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            try {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                LogOperation annotation = method.getAnnotation(LogOperation.class);

                Long userId = null;
                String username = null;
                String role = null;
                String ipAddress = null;
                String userAgent = null;

                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    ipAddress = request.getRemoteAddr();
                    userAgent = request.getHeader("User-Agent");
                    String token = request.getHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    } else {
                        token = request.getHeader("X-Token");
                    }
                    if (token != null) {
                        userId = jwtUtil.getUserId(token);
                        username = jwtUtil.getUsername(token);
                        role = jwtUtil.getRole(token);
                    }
                }

                String content = String.format("%s 耗时: %dms", annotation.description(), System.currentTimeMillis() - startTime);

                operationLogService.recordLog(
                        userId, username, role,
                        annotation.operationType(),
                        annotation.targetType(),
                        null,
                        content,
                        ipAddress, userAgent,
                        status, errorMsg
                );
            } catch (Exception e) {
                log.error("记录操作日志异常", e);
            }
        }
    }
}
