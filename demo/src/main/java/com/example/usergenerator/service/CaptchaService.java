package com.example.usergenerator.service;

public interface CaptchaService {

    String generateCaptcha(String uuid);

    boolean verifyCaptcha(String uuid, String code);
}
