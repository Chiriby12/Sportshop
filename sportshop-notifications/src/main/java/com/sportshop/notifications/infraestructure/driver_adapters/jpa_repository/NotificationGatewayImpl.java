package com.sportshop.notifications.infraestructure.driver_adapters.jpa_repository;

import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.gateway.NotificationGateway;
import com.sportshop.notifications.infraestructure.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador conducido (Driven Adapter) - Persistencia en PostgreSQL.
 * Arquitectura Hexagonal: implementa el puerto NotificationGateway.
 */
@Repository
@RequiredArgsConstructor
public class NotificationGatewayImpl implements NotificationGateway {

    private final NotificationJpaRepository repository;
    private final NotificationMapper mapper;

    @Override
    public Notification save(Notification notification) {
        return mapper.toDomain(repository.save(mapper.toData(notification)));
    }

    @Override
    public List<Notification> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByPerformedBy(String performedBy) {
        return repository.findByPerformedBy(performedBy).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByType(String type) {
        return repository.findByType(type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findByStatus(String status) {
        return repository.findByStatus(status).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notification> findBySourceService(String sourceService) {
        return repository.findBySourceService(sourceService).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Notification markAsRead(Long id) {
        NotificationData data = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe notificación con id: " + id));
        data.setStatus("READ");
        return mapper.toDomain(repository.save(data));
    }
}
