package com.sportshop.admin.infraestructure.driver_adapters;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.infraestructure.driver_adapters.catalog_client.CatalogSyncGatewayImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@DisplayName("CatalogSyncGatewayImpl - Tests del adaptador de sincronizacion con catalogo")
class CatalogSyncGatewayImplTest {

    private AdminProduct buildProduct() {
        return new AdminProduct(1L, "Camiseta", "desc", "Nike",
                "RUNNING", "ATLETISMO", new BigDecimal("85000"), 50, "url", true);
    }

    private org.springframework.web.reactive.function.client.WebClient.Builder mockBuilder(
            org.springframework.web.reactive.function.client.WebClient webClient) {
        var builder = mock(org.springframework.web.reactive.function.client.WebClient.Builder.class);
        lenient().doReturn(builder).when(builder).baseUrl(anyString());
        lenient().doReturn(webClient).when(builder).build();
        return builder;
    }

    @Test
    @DisplayName("createOrUpdate: sincroniza correctamente cuando catalog responde OK")
    void createOrUpdate_ok() {
        var webClient = mock(org.springframework.web.reactive.function.client.WebClient.class);
        var uriSpec   = mock(org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec.class);
        var resp      = mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        doReturn(uriSpec).when(webClient).post();
        doReturn(uriSpec).when(uriSpec).uri(anyString());
        doReturn(uriSpec).when(uriSpec).bodyValue(any());
        doReturn(resp).when(uriSpec).retrieve();
        doReturn(Mono.empty()).when(resp).bodyToMono(Void.class);

        var impl = new CatalogSyncGatewayImpl(mockBuilder(webClient), "http://localhost:8081");
        assertDoesNotThrow(() -> impl.createOrUpdate(buildProduct()));
    }

    @Test
    @DisplayName("createOrUpdate: no lanza excepcion si catalog no esta disponible")
    void createOrUpdate_catalogNoDisponible() {
        var webClient = mock(org.springframework.web.reactive.function.client.WebClient.class);
        var uriSpec   = mock(org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec.class);
        var resp      = mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        doReturn(uriSpec).when(webClient).post();
        doReturn(uriSpec).when(uriSpec).uri(anyString());
        doReturn(uriSpec).when(uriSpec).bodyValue(any());
        doReturn(resp).when(uriSpec).retrieve();
        doReturn(Mono.error(new RuntimeException("Connection refused"))).when(resp).bodyToMono(Void.class);

        var impl = new CatalogSyncGatewayImpl(mockBuilder(webClient), "http://localhost:8081");
        assertDoesNotThrow(() -> impl.createOrUpdate(buildProduct()));
    }

    @Test
    @DisplayName("createOrUpdate: producto con campos null no lanza excepcion")
    void createOrUpdate_camposNull() {
        var webClient = mock(org.springframework.web.reactive.function.client.WebClient.class);
        var uriSpec   = mock(org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec.class);
        var resp      = mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        doReturn(uriSpec).when(webClient).post();
        doReturn(uriSpec).when(uriSpec).uri(anyString());
        doReturn(uriSpec).when(uriSpec).bodyValue(any());
        doReturn(resp).when(uriSpec).retrieve();
        doReturn(Mono.empty()).when(resp).bodyToMono(Void.class);

        AdminProduct p = new AdminProduct(2L, "Pelota", null,
                "Nike", "RUNNING", null, new BigDecimal("50000"), 10, null, null);

        var impl = new CatalogSyncGatewayImpl(mockBuilder(webClient), "http://localhost:8081");
        assertDoesNotThrow(() -> impl.createOrUpdate(p));
    }

    @Test
    @DisplayName("delete: elimina correctamente cuando catalog responde OK")
    void delete_ok() {
        var webClient   = mock(org.springframework.web.reactive.function.client.WebClient.class);
        var headersSpec = mock(org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec.class);
        var resp        = mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        doReturn(headersSpec).when(webClient).delete();
        doReturn(headersSpec).when(headersSpec).uri(anyString());
        doReturn(resp).when(headersSpec).retrieve();
        doReturn(Mono.empty()).when(resp).bodyToMono(Void.class);

        var impl = new CatalogSyncGatewayImpl(mockBuilder(webClient), "http://localhost:8081");
        assertDoesNotThrow(() -> impl.delete(1L));
    }

    @Test
    @DisplayName("delete: no lanza excepcion si catalog no esta disponible")
    void delete_catalogNoDisponible() {
        var webClient   = mock(org.springframework.web.reactive.function.client.WebClient.class);
        var headersSpec = mock(org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec.class);
        var resp        = mock(org.springframework.web.reactive.function.client.WebClient.ResponseSpec.class);

        doReturn(headersSpec).when(webClient).delete();
        doReturn(headersSpec).when(headersSpec).uri(anyString());
        doReturn(resp).when(headersSpec).retrieve();
        doReturn(Mono.error(new RuntimeException("Connection refused"))).when(resp).bodyToMono(Void.class);

        var impl = new CatalogSyncGatewayImpl(mockBuilder(webClient), "http://localhost:8081");
        assertDoesNotThrow(() -> impl.delete(1L));
    }
}