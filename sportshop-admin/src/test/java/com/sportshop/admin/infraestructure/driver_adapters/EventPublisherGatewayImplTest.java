package com.sportshop.admin.infraestructure.driver_adapters;

import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.infraestructure.event_publisher.EventPublisherGatewayImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherGatewayImplTest {

    private EventPublisherGatewayImpl buildPublisher(WebClient.ResponseSpec responseSpec) {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        WebClient webClient = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);

        when(builder.baseUrl(any())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(uriSpec);
        when(uriSpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        return new EventPublisherGatewayImpl(builder, "http://localhost:8083");
    }

    @Test
    @DisplayName("publish: envía evento correctamente y no falla si el servicio responde")
    void publish_exitoso() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Void.class)).thenReturn(Mono.empty());

        EventPublisherGatewayImpl publisher = buildPublisher(responseSpec);

        AdminEvent event = AdminEvent.of(
                AdminEvent.EventType.USER_CREATED,
                "Test", "Test msg", "admin", null
        );

        assertDoesNotThrow(() -> publisher.publish(event));
    }

    @Test
    @DisplayName("publish: no lanza excepción si el servicio de notificaciones no está disponible")
    void publish_fallaServicioNotificaciones() {
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Conexión rechazada")));

        EventPublisherGatewayImpl publisher = buildPublisher(responseSpec);

        AdminEvent event = AdminEvent.of(
                AdminEvent.EventType.PRODUCT_DELETED,
                "Delete", "Producto eliminado", "admin", null
        );

        assertDoesNotThrow(() -> publisher.publish(event));
    }

    @Test
    @DisplayName("AdminEvent.of: crea evento con timestamp")
    void adminEvent_of_creaEventoConTimestamp() {
        AdminEvent event = AdminEvent.of(
                AdminEvent.EventType.USER_ROLE_CHANGED,
                "Rol cambiado", "USER -> ADMIN", "doc-admin", "payload"
        );

        assert event.getType() == AdminEvent.EventType.USER_ROLE_CHANGED;
        assert event.getTitle().equals("Rol cambiado");
        assert event.getTimestamp() != null;
    }
}