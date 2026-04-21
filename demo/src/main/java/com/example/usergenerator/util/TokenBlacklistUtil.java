package com.example.usergenerator.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class TokenBlacklistUtil {

    // 使用 ConcurrentHashMap 存储黑名单 token，线程安全
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * 将 token 加入黑名单
     */
    public void addToBlacklist(String token) {
        if (token != null && !token.isEmpty()) {
            blacklistedTokens.add(token);
            log.info("Token 已加入黑名单");
        }
    }

    /**
     * 检查 token 是否在黑名单中
     */
    public boolean isBlacklisted(String token) {
        return token != null && blacklistedTokens.contains(token);
    }

    /**
     * 从黑名单中移除 token（可选，用于清理过期 token）
     */
    public void removeFromBlacklist(String token) {
        if (token != null) {
            blacklistedTokens.remove(token);
        }
    }

    /**
     * 获取黑名单大小
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }

    /**
     * 清空黑名单
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
        log.info("Token 黑名单已清空");
    }
}
