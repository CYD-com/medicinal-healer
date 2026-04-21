package com.example.usergenerator.config;

import com.example.usergenerator.annotation.RequirePermission;
import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.util.JwtUtil;
import com.example.usergenerator.util.PermissionUtil;
import com.example.usergenerator.util.TokenBlacklistUtil;
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
public class PermissionAspect {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistUtil tokenBlacklistUtil;

    @Around("@annotation(com.example.usergenerator.annotation.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);

        if (annotation != null) {
            String[] requiredRoles = annotation.value();
            String userRole = getUserRole();

            if (!PermissionUtil.hasAnyPermission(userRole, requiredRoles)) {
                throw new BusinessException(ResultCode.FORBIDDEN);
            }
        }

        return joinPoint.proceed();
    }

    private String getUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String token = extractToken(request);
            
            if (token != null) {
                // 检查token是否在黑名单中
                if (tokenBlacklistUtil.isBlacklisted(token)) {
                    throw new BusinessException(ResultCode.INVALID_TOKEN, "Token已被注销，请重新登录");
                }
                
                // 验证token有效性和过期时间
                if (!jwtUtil.validateToken(token)) {
                    throw new BusinessException(ResultCode.INVALID_TOKEN, "Token无效或已过期");
                }
                
                // 获取token中的角色
                String tokenRole = jwtUtil.getRole(token);
                if (tokenRole == null) {
                    throw new BusinessException(ResultCode.FORBIDDEN, "Token中未包含角色信息");
                }
                
                // 验证X-User-Role与token中的角色一致
                String headerRole = request.getHeader("X-User-Role");
                if (headerRole != null && !headerRole.equals(tokenRole)) {
                    throw new BusinessException(ResultCode.FORBIDDEN, "X-User-Role与token中的角色不一致");
                }
                
                return tokenRole;
            }
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED, "未提供有效的认证信息");
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getHeader("X-Token");
    }
}
