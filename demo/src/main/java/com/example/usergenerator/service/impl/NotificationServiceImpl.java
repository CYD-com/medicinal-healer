package com.example.usergenerator.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.usergenerator.entity.Notification;
import com.example.usergenerator.exception.BusinessException;
import com.example.usergenerator.mapper.NotificationMapper;
import com.example.usergenerator.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;

    @Override
    public void sendNotification(Long userId, String title, String content, String type, Long relatedId, String relatedType) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setRelatedType(relatedType);
        notification.setIsRead(0);
        notificationMapper.insert(notification);
        log.info("发送通知成功，用户ID: {}, 类型: {}, 标题: {}", userId, type, title);
    }

    @Override
    public Map<String, Object> getNotifications(Long userId, Integer page, Integer size) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        List<Notification> all = notificationMapper.selectList(wrapper);
        int total = all.size();
        int from = (page - 1) * size;
        int to = Math.min(from + size, total);
        List<Notification> records = from < total ? all.subList(from, to) : Collections.emptyList();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }

    @Override
    public Map<String, Object> getUnreadCount(Long userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).eq("is_read", 0);
        long count = notificationMapper.selectCount(wrapper);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @Override
    public boolean markAsRead(Long id, Long userId) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }
        notification.setIsRead(1);
        return notificationMapper.updateById(notification) > 0;
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        return notificationMapper.markAllAsRead(userId) >= 0;
    }

    @Override
    public boolean deleteNotification(Long id, Long userId) {
        Notification notification = notificationMapper.selectById(id);
        if (notification == null || !notification.getUserId().equals(userId)) {
            throw new BusinessException("通知不存在");
        }
        return notificationMapper.deleteById(id) > 0;
    }
}
