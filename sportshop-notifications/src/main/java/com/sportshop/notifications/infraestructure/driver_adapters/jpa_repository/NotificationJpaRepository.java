package com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationData, Long> {
    List<NotificationData> findByPerformedBy(String performedBy);
    List<NotificationData> findByType(String type);
    List<NotificationData> findByStatus(String status);
    List<NotificationData> findBySourceService(String sourceService);
}
