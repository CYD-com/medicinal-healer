package com.example.usergenerator.controller;

import com.example.usergenerator.common.Result;
import com.example.usergenerator.entity.Notification;
import com.example.usergenerator.service.NotificationService;
import com.example.usergenerator.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PermissionUtil permissionUtil;

    @GetMapping("/list")
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = permissionUtil.getCurrentUserId();
        Map<String, Object> result = notificationService.getNotifications(userId, page, size);
        return ResponseEntity.ok(Result.success(result));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount() {
        Long userId = permissionUtil.getCurrentUserId();
        Map<String, Object> result = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(Result.success(result));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        Long userId = permissionUtil.getCurrentUserId();
        boolean success = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(Result.success("标记已读"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead() {
        Long userId = permissionUtil.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Result.success("全部已读"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable Long id) {
        Long userId = permissionUtil.getCurrentUserId();
        boolean success = notificationService.deleteNotification(id, userId);
        return ResponseEntity.ok(Result.success("删除成功"));
    }
}
