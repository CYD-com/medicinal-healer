package com.example.usergenerator.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.usergenerator.service.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_TIME = 5;
    
    private final Map<String, CaptchaEntry> captchaStore = new ConcurrentHashMap<>();

    private static class CaptchaEntry {
        String code;
        long expireTime;
        
        CaptchaEntry(String code, long ttlMillis) {
            this.code = code;
            this.expireTime = System.currentTimeMillis() + ttlMillis;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    @Override
    public String generateCaptcha(String uuid) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
        String code = lineCaptcha.getCode();
        String key = CAPTCHA_KEY_PREFIX + uuid;
        captchaStore.put(key, new CaptchaEntry(code.toLowerCase(), TimeUnit.MINUTES.toMillis(CAPTCHA_EXPIRE_TIME)));
        log.info("生成验证码成功，uuid: {}", uuid);
        return lineCaptcha.getImageBase64();
    }

    @Override
    public boolean verifyCaptcha(String uuid, String code) {
        String key = CAPTCHA_KEY_PREFIX + uuid;
        CaptchaEntry entry = captchaStore.get(key);
        
        if (entry == null || entry.isExpired()) {
            log.warn("验证码已过期或不存在，uuid: {}", uuid);
            captchaStore.remove(key);
            return false;
        }
        
        boolean result = entry.code.equalsIgnoreCase(code);
        captchaStore.remove(key);
        
        if (result) {
            log.info("验证码验证成功，uuid: {}", uuid);
        } else {
            log.warn("验证码验证失败，uuid: {}, 输入code: {}, 存储code: {}", uuid, code, entry.code);
        }
        
        return result;
    }
}
