package com.sportshop.notifications.application.dto;
import lombok.*;
import java.time.LocalDateTime;
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String performedBy;
    private String sourceService;
    private String status;
    private LocalDateTime createdAt;
}
