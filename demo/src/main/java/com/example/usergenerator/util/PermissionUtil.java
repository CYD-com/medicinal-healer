package com.example.usergenerator.util;

import com.example.usergenerator.constant.RoleConstants;
import org.apache.commons.lang3.StringUtils;

public class PermissionUtil {

    public static boolean hasPermission(String userRole, String requiredRole) {
        if (StringUtils.isBlank(userRole)) {
            return false;
        }
        if (RoleConstants.ADMIN.equals(userRole)) {
            return true;
        }
        return userRole.equals(requiredRole);
    }

    public static boolean hasAnyPermission(String userRole, String[] requiredRoles) {
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
}
