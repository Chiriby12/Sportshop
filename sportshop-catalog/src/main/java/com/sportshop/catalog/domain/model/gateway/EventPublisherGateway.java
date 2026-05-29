package com.sportshop.catalog.domain.model.gateway;

import com.sportshop.catalog.domain.model.event.CatalogEvent;


public interface EventPublisherGateway {
    void publish(CatalogEvent event);
}
