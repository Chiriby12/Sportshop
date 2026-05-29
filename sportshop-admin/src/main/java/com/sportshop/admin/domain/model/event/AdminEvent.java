package com.sportshop.admin.domain.model.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminEvent {

    public enum EventType {

        PRODUCT_CREATED,
        PRODUCT_UPDATED,
        PRODUCT_DELETED,

        USER_CREATED,
        USER_UPDATED,
        USER_DELETED,
        USER_ROLE_CHANGED
    }

    private EventType type;
    private String title;
    private String message;
    private String performedBy;
    private Object payload;
    private LocalDateTime timestamp;

    public static AdminEvent of(EventType type, String title, String message,
                                String performedBy, Object payload) {
        return new AdminEvent(type, title, message, performedBy, payload, LocalDateTime.now());
    }
}
