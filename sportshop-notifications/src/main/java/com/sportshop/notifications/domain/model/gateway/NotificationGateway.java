package com.sportshop.notifications.domain.model.gateway;

import com.sportshop.notifications.domain.model.Notification;

import java.util.List;
import java.util.Optional;


public interface NotificationGateway {
    Notification save(Notification notification);
    List<Notification> findAll();
    Optional<Notification> findById(Long id);
    List<Notification> findByPerformedBy(String performedBy);
    List<Notification> findByType(String type);
    List<Notification> findByStatus(String status);
    List<Notification> findBySourceService(String sourceService);
    Notification markAsRead(Long id);
}
