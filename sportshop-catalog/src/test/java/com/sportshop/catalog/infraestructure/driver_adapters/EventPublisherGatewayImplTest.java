package com.sportshop.catalog.infraestructure.driver_adapters;

import com.sportshop.catalog.domain.model.event.CatalogEvent;
import com.sportshop.catalog.infraestructure.event_publisher.EventPublisherGatewayImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventPublisherGatewayImpl - Tests del publicador de eventos")
class EventPublisherGatewayImplTest {

    @Test
    @DisplayName("publish: no propaga errores si el servicio de notificaciones falla")
    @SuppressWarnings("unchecked")
    void publish_doesNotThrowOnError() {
        WebClient.Builder builder          = mock(WebClient.Builder.class);
        WebClient webClient                = mock(WebClient.class);
        WebClient.RequestBodyUriSpec uriSpec  = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec    = mock(WebClient.RequestBodySpec.class);
        // Fix: bodyValue() retorna RequestHeadersSpec, no RequestBodySpec
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec      = mock(WebClient.ResponseSpec.class);

        when(builder.baseUrl(any())).thenReturn(builder);
        when(builder.build()).thenReturn(webClient);
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);   // Fix aquí
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Void.class))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        EventPublisherGatewayImpl publisher =
                new EventPublisherGatewayImpl(builder, "http://localhost:9999");

        CatalogEvent event = CatalogEvent.of(
                CatalogEvent.EventType.PRODUCT_CREATED,
                "Test", "Test message", "ADMIN001", null);

        assertThatCode(() -> publisher.publish(event)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("CatalogEvent.of: crea evento con timestamp y tipo correcto")
    void catalogEvent_of_createsCorrectly() {
        CatalogEvent event = CatalogEvent.of(
                CatalogEvent.EventType.CART_PURCHASED,
                "Compra", "Compra realizada", "USR001", "payload");

        assertThat(event.getType()).isEqualTo(CatalogEvent.EventType.CART_PURCHASED);
        assertThat(event.getTitle()).isEqualTo("Compra");
        assertThat(event.getMessage()).isEqualTo("Compra realizada");
        assertThat(event.getPerformedBy()).isEqualTo("USR001");
        assertThat(event.getPayload()).isEqualTo("payload");
        assertThat(event.getTimestamp()).isNotNull();
    }
}