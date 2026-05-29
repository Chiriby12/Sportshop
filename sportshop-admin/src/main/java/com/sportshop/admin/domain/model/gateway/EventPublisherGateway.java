package com.sportshop.admin.domain.model.gateway;

import com.sportshop.admin.domain.model.event.AdminEvent;


public interface EventPublisherGateway {
    void publish(AdminEvent event);
}
