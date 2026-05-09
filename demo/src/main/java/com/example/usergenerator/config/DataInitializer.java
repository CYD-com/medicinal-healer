package com.example.usergenerator.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.entity.SysUser;
import com.example.usergenerator.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
        migrateDoctorTable();
        insertDoctorSchedules();
        insertSampleAppointments();
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

    private void migrateDoctorTable() {
        try {
            jdbcTemplate.execute("ALTER TABLE t_doctor DROP COLUMN name");
            log.info("t_doctor 表删除 name 字段成功");
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("Unknown column") || e.getMessage().contains("check that column/key exists"))) {
                log.info("t_doctor 表已无 name 字段，跳过");
            } else {
                log.warn("删除 name 字段失败: {}", e.getMessage());
            }
        }
        try {
            jdbcTemplate.execute("ALTER TABLE t_doctor DROP COLUMN avatar");
            log.info("t_doctor 表删除 avatar 字段成功");
        } catch (Exception e) {
            if (e.getMessage() != null && (e.getMessage().contains("Unknown column") || e.getMessage().contains("check that column/key exists"))) {
                log.info("t_doctor 表已无 avatar 字段，跳过");
            } else {
                log.warn("删除 avatar 字段失败: {}", e.getMessage());
            }
        }

        try {
            List<String> doctorNames = jdbcTemplate.queryForList(
                    "SELECT u.real_name FROM t_doctor d LEFT JOIN sys_user u ON d.user_id = u.id WHERE d.user_id IS NULL LIMIT 1",
                    String.class);
            if (!doctorNames.isEmpty()) {
                log.info("发现无 user_id 的医生记录，开始创建关联用户...");
                List<Map<String, Object>> orphanDoctors = jdbcTemplate.queryForList(
                        "SELECT d.doctor_id, d.title FROM t_doctor d WHERE d.user_id IS NULL");
                for (Map<String, Object> doc : orphanDoctors) {
                    Long doctorId = ((Number) doc.get("doctor_id")).longValue();
                    String title = (String) doc.get("title");
                    String username = "doctor_" + doctorId;
                    String password = "$2b$10$uRAS68xyAueDj1s/o0UZjO.k4JOW1.QWWwajImr/d1RiFx2z9l47y";
                    jdbcTemplate.update(
                            "INSERT INTO sys_user (username, password, real_name, gender, age, phone, email, id_card, address, role, status, create_time, update_time) VALUES (?, ?, ?, '未知', 0, '', '', '', '', 'doctor', 1, NOW(), NOW())",
                            username, password, "医生" + doctorId);
                    Long newUserId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
                    jdbcTemplate.update("UPDATE t_doctor SET user_id = ? WHERE doctor_id = ?", newUserId, doctorId);
                    log.info("为医生 {} 创建用户账号 {} (user_id={})", doctorId, username, newUserId);
                }
            }
        } catch (Exception e) {
            log.warn("医生用户关联迁移失败: {}", e.getMessage());
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

    private void insertSampleAppointments() {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM t_appointment WHERE appointment_date >= DATE_ADD(CURDATE(), INTERVAL -1 DAY) AND appointment_date <= DATE_ADD(CURDATE(), INTERVAL 1 DAY)", Integer.class);
            if (count != null && count >= 5) {
                log.info("预约表已有近期数据（{}条），跳过插入", count);
                return;
            }
        } catch (Exception e) {
            log.warn("查询预约表失败，跳过预约初始化: {}", e.getMessage());
            return;
        }

        try {
            jdbcTemplate.execute("DELETE FROM t_appointment");
            log.info("已清理旧预约数据");
        } catch (Exception e) {
            log.warn("清理旧预约数据失败: {}", e.getMessage());
        }

        LocalDate today = LocalDate.now();
        String sql = "INSERT INTO t_appointment " +
                "(user_id, doctor_id, department_id, appointment_date, start_time, end_time, status, patient_name, patient_phone, symptoms, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        Object[][] appointments = {
            {4L, 1L, 2L, today,                "09:00", "09:30", "confirmed",  "王五", "13800000004", "头晕、胸闷，持续三天，伴有轻微心悸"},
            {5L, 2L, 2L, today,                "09:30", "10:00", "pending",    "赵六", "13800000005", "咳嗽、咳痰一周，夜间加重，无发热"},
            {4L, 3L, 1L, today,                "10:00", "10:30", "pending",    "王五", "13800000004", "近两周感觉乏力、食欲不振，偶有低热"},
            {5L, 1L, 2L, today.minusDays(1),   "14:00", "14:30", "completed",  "赵六", "13800000005", "高血压复查，近期血压波动较大，伴有头痛"},
            {4L, 5L, 5L, today.plusDays(1),    "08:30", "09:00", "pending",    "王五", "13800000004", "失眠多梦两周，工作压力大，腰膝酸软"},
            {5L, 1L, 2L, today.plusDays(1),    "10:00", "10:30", "pending",    "赵六", "13800000005", "反复感冒两周，鼻塞流涕，咽痛"},
            {4L, 4L, 3L, today.plusDays(1),    "08:30", "09:00", "pending",    "王五", "13800000004", "腰痛一周，弯腰时加重，活动受限"},
        };

        for (Object[] a : appointments) {
            jdbcTemplate.update(sql, a);
        }
        log.info("预约示例数据初始化完成，共插入 {} 条预约记录", appointments.length);
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
