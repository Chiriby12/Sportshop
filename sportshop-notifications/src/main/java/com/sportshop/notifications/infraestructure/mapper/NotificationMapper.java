package com.sportshop.notifications.infraestructure.mapper;

import com.sportshop.notifications.application.dto.NotificationResponseDTO;
import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository.NotificationData;
import org.springframework.stereotype.Component;


@Component
public class NotificationMapper {

    public Notification toDomain(NotificationData data) {
        if (data == null) return null;
        Notification n = new Notification();
        n.setId(data.getId());
        n.setType(data.getType());
        n.setTitle(data.getTitle());
        n.setMessage(data.getMessage());
        n.setPerformedBy(data.getPerformedBy());
        n.setSourceService(data.getSourceService());
        n.setStatus(data.getStatus());
        n.setCreatedAt(data.getCreatedAt());
        return n;
    }

    public NotificationData toData(Notification n) {
        if (n == null) return null;
        return NotificationData.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .performedBy(n.getPerformedBy())
                .sourceService(n.getSourceService())
                .status(n.getStatus())
                .createdAt(n.getCreatedAt())
                .build();
    }

    public NotificationResponseDTO toResponseDTO(Notification n) {
        if (n == null) return null;
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(n.getId());
        dto.setType(n.getType());
        dto.setTitle(n.getTitle());
        dto.setMessage(n.getMessage());
        dto.setPerformedBy(n.getPerformedBy());
        dto.setSourceService(n.getSourceService());
        dto.setStatus(n.getStatus());
        dto.setCreatedAt(n.getCreatedAt());
        return dto;
    }
}
