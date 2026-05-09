package com.example.usergenerator.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SysUserMapper sysUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        resetPassword("admin", "123456");
        addPatientMessageColumn();
        createMessageTable();
        insertDoctorSchedules();
    }

    private void addPatientMessageColumn() {
        try {
            jdbcTemplate.execute("ALTER TABLE consultation ADD COLUMN patient_message TEXT COMMENT '患者最新消息' AFTER doctor_reply");
            log.info("consultation 表添加 patient_message 字段成功");
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Duplicate column")) {
                log.info("consultation 表已存在 patient_message 字段，跳过");
            } else {
                log.warn("添加 patient_message 字段失败: {}", e.getMessage());
            }
        }
    }

    private void createMessageTable() {
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS consultation_message (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "consultation_id BIGINT NOT NULL COMMENT '问诊ID', " +
                    "sender_role VARCHAR(20) NOT NULL COMMENT '发送者角色: patient/doctor', " +
                    "sender_id BIGINT NOT NULL COMMENT '发送者ID', " +
                    "content TEXT NOT NULL COMMENT '消息内容', " +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间', " +
                    "INDEX idx_consultation_id (consultation_id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊聊天消息记录'");
            log.info("consultation_message 表创建成功或已存在");
        } catch (Exception e) {
            log.warn("创建 consultation_message 表失败: {}", e.getMessage());
        }
    }

    private void insertDoctorSchedules() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM doctor_schedule", Integer.class);
            if (count != null && count > 0) {
                log.info("排班表已有数据，跳过插入");
                return;
            }
        } catch (Exception e) {
            log.warn("查询排班表失败，跳过排班初始化: {}", e.getMessage());
            return;
        }

        String sql = "INSERT INTO doctor_schedule " +
                "(doctor_id, department_id, work_date, start_time, end_time, max_appointments, current_appointments, status, remark, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, 1, ?, NOW(), NOW())";

        LocalDate today = LocalDate.now();

        Object[][] schedules = {
            // 张三 - 内科主任医师，周一/三/五上午，每次限号15
            {1L, 2L, today.plusDays(1),  "08:00", "12:00", 15, 0, "上午门诊"},
            {1L, 2L, today.plusDays(3),  "08:00", "12:00", 15, 0, "上午门诊"},
            {1L, 2L, today.plusDays(5),  "08:00", "12:00", 15, 0, "上午门诊"},
            {1L, 2L, today.plusDays(8),  "08:00", "12:00", 15, 0, "上午门诊"},
            {1L, 2L, today.plusDays(10), "08:00", "12:00", 15, 0, "上午门诊"},
            {1L, 2L, today.plusDays(12), "08:00", "12:00", 15, 0, "上午门诊"},

            // 李四 - 内科副主任医师，周一/二/四/五上午+下午，每次限号20
            {2L, 2L, today.plusDays(1),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(1),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(2),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(2),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(4),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(4),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(5),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(5),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(8),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(8),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(9),  "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(9),  "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(11), "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(11), "14:00", "17:30", 15, 0, "下午门诊"},
            {2L, 2L, today.plusDays(12), "08:00", "12:00", 20, 0, "上午门诊"},
            {2L, 2L, today.plusDays(12), "14:00", "17:30", 15, 0, "下午门诊"},

            // 王芳 - 全科门诊主治医师，周一到周六上午+下午，每次限号25
            {3L, 1L, today.plusDays(1),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(1),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(2),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(2),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(3),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(3),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(4),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(4),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(5),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(5),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(6),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(6),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(8),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(8),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(9),  "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(9),  "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(10), "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(10), "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(11), "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(11), "14:00", "17:30", 20, 0, "下午门诊"},
            {3L, 1L, today.plusDays(12), "08:00", "12:00", 25, 0, "上午门诊"},
            {3L, 1L, today.plusDays(12), "14:00", "17:30", 20, 0, "下午门诊"},

            // 陈强 - 外科主任医师，周二/四上午，每次限号10
            {4L, 3L, today.plusDays(2),  "08:00", "12:00", 10, 0, "上午门诊"},
            {4L, 3L, today.plusDays(4),  "08:00", "12:00", 10, 0, "上午门诊"},
            {4L, 3L, today.plusDays(9),  "08:00", "12:00", 10, 0, "上午门诊"},
            {4L, 3L, today.plusDays(11), "08:00", "12:00", 10, 0, "上午门诊"},

            // 刘梅 - 中医科副主任医师，周一/三/五上午+下午，每次限号18
            {5L, 5L, today.plusDays(1),  "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(1),  "14:00", "17:30", 15, 0, "下午门诊"},
            {5L, 5L, today.plusDays(3),  "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(3),  "14:00", "17:30", 15, 0, "下午门诊"},
            {5L, 5L, today.plusDays(5),  "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(5),  "14:00", "17:30", 15, 0, "下午门诊"},
            {5L, 5L, today.plusDays(8),  "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(8),  "14:00", "17:30", 15, 0, "下午门诊"},
            {5L, 5L, today.plusDays(10), "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(10), "14:00", "17:30", 15, 0, "下午门诊"},
            {5L, 5L, today.plusDays(12), "08:00", "12:00", 18, 0, "上午门诊"},
            {5L, 5L, today.plusDays(12), "14:00", "17:30", 15, 0, "下午门诊"},
        };

        for (Object[] s : schedules) {
            jdbcTemplate.update(sql, s[0], s[1], s[2], s[3], s[4], s[5], s[6], s[7]);
        }
        log.info("医生排班数据初始化完成，共插入 {} 条排班记录", schedules.length);
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
