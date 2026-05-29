package com.sportshop.notifications.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String performedBy;
    private String sourceService;
    private String status;
    private LocalDateTime createdAt;
}
