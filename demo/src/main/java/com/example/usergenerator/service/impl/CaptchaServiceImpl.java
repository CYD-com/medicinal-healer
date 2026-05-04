package com.example.usergenerator.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.example.usergenerator.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";
    private static final long CAPTCHA_EXPIRE_TIME = 5;

    @Override
    public String generateCaptcha(String uuid) {
        // 创建验证码对象：宽120，高40，4位字符，50条干扰线
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 50);
        // 获取验证码文本
        String code = lineCaptcha.getCode();
        // 构建Redis存储key
        String key = CAPTCHA_KEY_PREFIX + uuid;
        // 将验证码存入Redis，设置过期时间为5分钟
        stringRedisTemplate.opsForValue().set(key, code.toLowerCase(), CAPTCHA_EXPIRE_TIME, TimeUnit.MINUTES);
        log.info("生成验证码成功，uuid: {}", uuid);
        // 返回验证码图片的Base64编码
        return lineCaptcha.getImageBase64();
    }

    @Override
    public boolean verifyCaptcha(String uuid, String code) {
        // 构建Redis key并获取存储的验证码
        String key = CAPTCHA_KEY_PREFIX + uuid;
        String storedCode = stringRedisTemplate.opsForValue().get(key);
        
        // 验证码不存在（已过期或不存在）
        if (storedCode == null) {
            log.warn("验证码已过期或不存在，uuid: {}", uuid);
            return false;
        }
        
        // 验证码比较（不区分大小写）
        boolean result = storedCode.equalsIgnoreCase(code);
        // 无论验证成功还是失败，都删除验证码，确保每个验证码只能使用一次
        stringRedisTemplate.delete(key);
        
        if (result) {
            log.info("验证码验证成功，uuid: {}", uuid);
        } else {
            log.warn("验证码验证失败，uuid: {}, 输入code: {}, 存储code: {}", uuid, code, storedCode);
        }
        
        return result;
    }
}
