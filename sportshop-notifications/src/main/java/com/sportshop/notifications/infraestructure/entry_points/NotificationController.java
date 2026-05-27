package com.sportshop.notifications.infraestructure.entry_points;

import com.sportshop.notifications.application.dto.ApiResponse;
import com.sportshop.notifications.application.dto.NotificationResponseDTO;
import com.sportshop.notifications.domain.model.Notification;
import com.sportshop.notifications.domain.model.event.CatalogEvent;
import com.sportshop.notifications.domain.usecase.NotificationUseCase;
import com.sportshop.notifications.infraestructure.mapper.NotificationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Adaptador conductor (Driving Adapter) - Endpoints de notificaciones.
 *
 * Flujo automático:
 *  1. catalog-service crea/actualiza/elimina un producto o el usuario agrega al carrito
 *  2. EventPublisherGatewayImpl del catálogo hace POST /api/sportshop/notifications/receive
 *  3. Este endpoint persiste la notificación en la BD
 *  4. El frontend consulta GET /api/sportshop/notifications para mostrarlas en pantalla
 */
@RestController
@RequestMapping("/api/sportshop/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Historial de eventos del sistema SportShop")
public class NotificationController {

    private final NotificationUseCase notificationUseCase;
    private final NotificationMapper notificationMapper;

    /**
     * Endpoint público - recibe eventos automáticos de otros microservicios.
     * Llamado por: catalog-service (EventPublisherGatewayImpl)
     */
    @PostMapping("/receive")
    @Operation(
        summary = "Recibir evento (interno)",
        description = "Endpoint público. Los microservicios lo llaman automáticamente al crear/actualizar/eliminar productos o realizar acciones de carrito."
    )
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> receiveEvent(
            @RequestBody CatalogEvent event) {
        Notification notification = notificationUseCase.receiveEvent(event);
        return new ResponseEntity<>(
                ApiResponse.created("Notificación registrada", notificationMapper.toResponseDTO(notification)),
                HttpStatus.CREATED
        );
    }

    /**
     * Listar todas las notificaciones - solo ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar todas las notificaciones", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getAllNotifications() {
        List<NotificationResponseDTO> notifications = notificationUseCase.getAllNotifications()
                .stream().map(notificationMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones encontradas", notifications));
    }

    /**
     * Obtener notificación por ID - ADMIN o USER autenticado.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Obtener notificación por ID")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> getById(@PathVariable Long id) {
        Notification n = notificationUseCase.getNotificationById(id);
        return ResponseEntity.ok(ApiResponse.ok("Notificación encontrada", notificationMapper.toResponseDTO(n)));
    }

    /**
     * Notificaciones de un usuario específico (por documento o email).
     * Un USER solo debería ver las suyas; ADMIN puede ver de cualquiera.
     */
    @GetMapping("/user/{performedBy}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Notificaciones por usuario", description = "Filtra por documento o email del usuario")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getByUser(
            @PathVariable String performedBy) {
        List<NotificationResponseDTO> notifications = notificationUseCase.getNotificationsByUser(performedBy)
                .stream().map(notificationMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones del usuario: " + performedBy, notifications));
    }

    /**
     * Filtrar por tipo de evento.
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Notificaciones por tipo", description = "Ej: PRODUCT_CREATED, CART_PURCHASED")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getByType(
            @PathVariable String type) {
        List<NotificationResponseDTO> notifications = notificationUseCase.getNotificationsByType(type)
                .stream().map(notificationMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones de tipo: " + type, notifications));
    }

    /**
     * Filtrar por estado: RECEIVED o READ.
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Notificaciones por estado", description = "RECEIVED o READ")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getByStatus(
            @PathVariable String status) {
        List<NotificationResponseDTO> notifications = notificationUseCase.getNotificationsByStatus(status)
                .stream().map(notificationMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Notificaciones con estado: " + status, notifications));
    }

    /**
     * Marcar notificación como leída.
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Marcar notificación como leída")
    public ResponseEntity<ApiResponse<NotificationResponseDTO>> markAsRead(@PathVariable Long id) {
        Notification n = notificationUseCase.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.ok("Notificación marcada como leída", notificationMapper.toResponseDTO(n)));
    }
}
