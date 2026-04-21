package com.example.usergenerator.util;

import com.example.usergenerator.constant.RoleConstants;
import com.example.usergenerator.entity.SysUser;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class UserGenerator {
    
    private static final String USERNAME_PREFIX = "user_";
    private static final String CHAR_POOL = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final Random RANDOM = new Random();

    public String generateUsername() {
        return USERNAME_PREFIX + generateRandomStr(6);
    }

    public String generatePassword() {
        int length = RANDOM.nextInt(9) + 8;
        return generateRandomStr(length);
    }

    private String generateRandomStr(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static SysUser generateUser() {
        SysUser user = new SysUser();
        user.setUsername(new UserGenerator().generateUsername());
        user.setPassword(new UserGenerator().generatePassword());
        user.setRole(RoleConstants.USER);
        return user;
    }
}
