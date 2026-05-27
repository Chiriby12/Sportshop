package com.sportshop.admin.infraestructure.driver_adapters.catalog_client;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.model.gateway.CatalogSyncGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * Adaptador conducido — replica productos en sportshop-catalog via HTTP.
 * Opera en best-effort: si catalog no responde, no falla la operación del admin.
 */
@Component
@Slf4j
public class CatalogSyncGatewayImpl implements CatalogSyncGateway {

    private final WebClient webClient;

    public CatalogSyncGatewayImpl(
            WebClient.Builder builder,
            @Value("${catalog.service.url}") String catalogUrl) {
        this.webClient = builder.baseUrl(catalogUrl).build();
    }

    @Override
    public void createOrUpdate(AdminProduct p) {
        Map<String, Object> body = Map.of(
                "name",        p.getName(),
                "description", p.getDescription() != null ? p.getDescription() : "",
                "brand",       p.getBrand(),
                "category",    p.getCategory(),
                "sport",       p.getSport() != null ? p.getSport() : "GENERAL",
                "price",       p.getPrice(),
                "stock",       p.getStock(),
                "imageUrl",    p.getImageUrl() != null ? p.getImageUrl() : "",
                "active",      p.getActive() != null ? p.getActive() : true
        );

        webClient.post()
                .uri("/api/sportshop/catalog/products/sync/" + p.getId())
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Producto {} sincronizado en catalog", p.getId()))
                .doOnError(e -> log.warn("No se pudo sincronizar producto {} en catalog: {}", p.getId(), e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }

    @Override
    public void delete(Long productId) {
        webClient.delete()
                .uri("/api/sportshop/catalog/products/sync/" + productId)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Producto {} eliminado de catalog", productId))
                .doOnError(e -> log.warn("No se pudo eliminar producto {} de catalog: {}", productId, e.getMessage()))
                .onErrorComplete()
                .subscribe();
    }
}