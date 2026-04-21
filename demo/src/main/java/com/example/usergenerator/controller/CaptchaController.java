package com.example.usergenerator.controller;

import com.example.usergenerator.common.Result;
import com.example.usergenerator.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 生成验证码
     *
     * @return 包含UUID和Base64格式验证码图片的结果对象
     */
    @GetMapping("/generate")
    public Result<Map<String, String>> generateCaptcha() {
        // 生成唯一标识符用于关联验证码
        String uuid = UUID.randomUUID().toString();
        // 调用服务生成验证码图片并转为Base64
        String imageBase64 = captchaService.generateCaptcha(uuid);
        
        // 组装返回结果
        Map<String, String> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("image", imageBase64);
        
        return Result.success("验证码生成成功", result);
    }
}
