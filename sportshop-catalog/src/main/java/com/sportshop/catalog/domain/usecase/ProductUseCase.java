package com.sportshop.catalog.domain.usecase;

import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.model.event.CatalogEvent;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Caso de uso del dominio de Catálogo.
 * Arquitectura Hexagonal: núcleo del hexágono. Sin dependencias de Spring ni JPA.
 * Publica eventos al microservicio de notificaciones en cada operación.
 */
@RequiredArgsConstructor
public class ProductUseCase {

    private final ProductGateway productGateway;
    private final EventPublisherGateway eventPublisher;

    public Product createProduct(Product product, String adminDocument) {
        if (product.getName() == null || product.getName().trim().isEmpty())
            throw new RuntimeException("El nombre del producto no puede estar vacío");
        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0)
            throw new RuntimeException("El precio debe ser mayor a 0");
        if (product.getStock() == null || product.getStock() < 0)
            throw new RuntimeException("El stock no puede ser negativo");

        if (product.getActive() == null) product.setActive(true);

        Product saved = productGateway.save(product);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.PRODUCT_CREATED,
                "Nuevo producto creado",
                "El admin creó el producto: " + saved.getName(),
                adminDocument,
                saved
        ));

        return saved;
    }

    public Product getProductById(Long id) {
        return productGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un producto con id: " + id));
    }

    public List<Product> getAllProducts() {
        return productGateway.findAll();
    }

    public List<Product> getActiveProducts() {
        return productGateway.findActive();
    }

    public List<Product> getProductsByCategory(String category) {
        if (category == null || category.trim().isEmpty())
            throw new RuntimeException("La categoría no puede estar vacía");
        return productGateway.findByCategory(category);
    }

    public List<Product> getProductsBySport(String sport) {
        if (sport == null || sport.trim().isEmpty())
            throw new RuntimeException("El deporte no puede estar vacío");
        return productGateway.findBySport(sport);
    }

    public Product updateProduct(Long id, Product product, String adminDocument) {
        if (!productGateway.existsById(id))
            throw new RuntimeException("No existe un producto con id: " + id);

        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0)
            throw new RuntimeException("El precio debe ser mayor a 0");
        if (product.getStock() != null && product.getStock() < 0)
            throw new RuntimeException("El stock no puede ser negativo");

        product.setId(id);
        Product updated = productGateway.save(product);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.PRODUCT_UPDATED,
                "Producto actualizado",
                "El admin actualizó el producto: " + updated.getName(),
                adminDocument,
                updated
        ));

        return updated;
    }

    public void deleteProduct(Long id, String adminDocument) {
        Product product = productGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un producto con id: " + id));

        productGateway.deleteById(id);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.PRODUCT_DELETED,
                "Producto eliminado",
                "El admin eliminó el producto: " + product.getName(),
                adminDocument,
                product
        ));
    }

    // ─── Sincronización desde sportshop-admin ─────────────────────────────────

    public Product syncFromAdmin(Long adminId, Product product) {
        product.setAdminId(adminId);
        if (product.getActive() == null) product.setActive(true);
        return productGateway.findByAdminId(adminId)
                .map(existing -> {
                    product.setId(existing.getId());
                    return productGateway.save(product);
                })
                .orElseGet(() -> productGateway.save(product));
    }

    public void deleteByAdminId(Long adminId) {
        productGateway.findByAdminId(adminId)
                .ifPresent(p -> productGateway.deleteById(p.getId()));
    }
}