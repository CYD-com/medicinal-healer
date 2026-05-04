package com.example.usergenerator.service;

import com.example.usergenerator.entity.Notification;

import java.util.Map;

public interface NotificationService {

    void sendNotification(Long userId, String title, String content, String type, Long relatedId, String relatedType);

    Map<String, Object> getNotifications(Long userId, Integer page, Integer size);

    Map<String, Object> getUnreadCount(Long userId);

    boolean markAsRead(Long id, Long userId);

    boolean markAllAsRead(Long userId);

    boolean deleteNotification(Long id, Long userId);
}
