-- 仅更新测试账号密码（BCrypt加密的 123456）
UPDATE `sys_user` SET `password` = '$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y' WHERE `username` IN ('admin', 'user1', 'doctor_zs', 'doctor_ls', 'user_w5', 'user_zl');
