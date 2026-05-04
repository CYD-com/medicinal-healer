package com.example.usergenerator.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        resetPassword("admin", "123456");
    }

    private void resetPassword(String username, String rawPassword) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        if (user != null) {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            user.setPassword(encodedPassword);
            sysUserMapper.updateById(user);
            log.info("用户 {} 密码已重置", username);
        }
    }
}
