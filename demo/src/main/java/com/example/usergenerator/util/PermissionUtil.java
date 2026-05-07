package com.example.usergenerator.util;

import com.example.usergenerator.common.ResultCode;
import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class PermissionUtil {

    private final JwtUtil jwtUtil;

    public boolean hasPermission(String userRole, String requiredRole) {
        if (StringUtils.isBlank(userRole)) {
            return false;
        }
        if (RoleConstants.ADMIN.equals(userRole)) {
            return true;
        }
        return userRole.equals(requiredRole);
    }

    public boolean hasAnyPermission(String userRole, String[] requiredRoles) {
        if (StringUtils.isBlank(userRole)) {
            return false;
        }
        if (RoleConstants.ADMIN.equals(userRole)) {
            return true;
        }
        for (String role : requiredRoles) {
            if (userRole.equals(role)) {
                return true;
            }
        }
        return false;
    }

    public Long getCurrentUserId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = request.getHeader("X-Token");
        }
        
        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return userId;
    }

    public String getCurrentUserRole() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            token = request.getHeader("X-Token");
        }

        String role = jwtUtil.getRole(token);
        if (role == null) {
            throw new BusinessException(ResultCode.FORBIDDEN, "Token中未包含角色信息");
        }
        return role;
    }
}