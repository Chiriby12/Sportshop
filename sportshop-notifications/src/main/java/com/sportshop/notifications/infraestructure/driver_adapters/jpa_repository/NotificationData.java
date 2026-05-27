package com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA - Adaptador conducido (Driven Adapter).
 * Arquitectura Hexagonal: capa de infraestructura, fuera del hexágono.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "performed_by")
    private String performedBy;

    @Column(name = "source_service")
    private String sourceService;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
