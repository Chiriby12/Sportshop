package com.sportshop.admin.domain.usecase;

import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.model.event.AdminEvent;
import com.sportshop.admin.domain.model.gateway.AdminProductGateway;
import com.sportshop.admin.domain.model.gateway.CatalogSyncGateway;
import com.sportshop.admin.domain.model.gateway.EventPublisherGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
public class AdminProductUseCase {

    private final AdminProductGateway productGateway;
    private final EventPublisherGateway eventPublisher;
    private final CatalogSyncGateway catalogSync;



    public AdminProduct createProduct(AdminProduct product, String adminDocument) {
        validarProducto(product);
        if (product.getActive() == null) product.setActive(true);

        AdminProduct saved = productGateway.save(product);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.PRODUCT_CREATED,
                "Nuevo producto creado",
                "El admin creó el producto: " + saved.getName(),
                adminDocument,
                saved
        ));

        catalogSync.createOrUpdate(saved);

        return saved;
    }



    public AdminProduct getProductById(Long id) {
        if (id == null || id <= 0)
            throw new RuntimeException("El ID debe ser un número positivo");

        return productGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un producto con id: " + id));
    }

    public List<AdminProduct> getAllProducts() {
        return productGateway.findAll();
    }

    public List<AdminProduct> getActiveProducts() {
        return productGateway.findActive();
    }

    public List<AdminProduct> getProductsByCategory(String category) {
        if (category == null || category.isBlank())
            throw new RuntimeException("La categoría no puede estar vacía");
        return productGateway.findByCategory(category);
    }

    public List<AdminProduct> getProductsBySport(String sport) {
        if (sport == null || sport.isBlank())
            throw new RuntimeException("El deporte no puede estar vacío");
        return productGateway.findBySport(sport);
    }



    public AdminProduct updateProduct(Long id, AdminProduct product, String adminDocument) {
        if (!productGateway.existsById(id))
            throw new RuntimeException("No existe un producto con id: " + id);

        if (product.getPrice() != null && product.getPrice().doubleValue() <= 0)
            throw new RuntimeException("El precio debe ser mayor a 0");

        if (product.getStock() != null && product.getStock() < 0)
            throw new RuntimeException("El stock no puede ser negativo");

        product.setId(id);
        AdminProduct updated = productGateway.save(product);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.PRODUCT_UPDATED,
                "Producto actualizado",
                "El admin actualizó el producto: " + updated.getName(),
                adminDocument,
                updated
        ));

        catalogSync.createOrUpdate(updated);

        return updated;
    }



    public void deleteProduct(Long id, String adminDocument) {
        AdminProduct product = productGateway.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe un producto con id: " + id));

        productGateway.deleteById(id);

        eventPublisher.publish(AdminEvent.of(
                AdminEvent.EventType.PRODUCT_DELETED,
                "Producto eliminado",
                "El admin eliminó el producto: " + product.getName(),
                adminDocument,
                product
        ));

        catalogSync.delete(id);
    }



    private void validarProducto(AdminProduct product) {
        if (product.getName() == null || product.getName().isBlank())
            throw new RuntimeException("El nombre del producto no puede estar vacío");

        if (product.getPrice() == null || product.getPrice().doubleValue() <= 0)
            throw new RuntimeException("El precio debe ser mayor a 0");

        if (product.getStock() == null || product.getStock() < 0)
            throw new RuntimeException("El stock no puede ser negativo");

        if (product.getBrand() == null || product.getBrand().isBlank())
            throw new RuntimeException("La marca no puede estar vacía");
    }
}