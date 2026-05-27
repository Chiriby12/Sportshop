package com.sportshop.catalog.domain.usecase;

import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.model.event.CatalogEvent;
import com.sportshop.catalog.domain.model.gateway.CartGateway;
import com.sportshop.catalog.domain.model.gateway.EventPublisherGateway;
import com.sportshop.catalog.domain.model.gateway.ProductGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Caso de uso del carrito de compras.
 * Núcleo del hexágono: sin dependencias de infraestructura.
 */
@RequiredArgsConstructor
public class CartUseCase {

    private final CartGateway cartGateway;
    private final ProductGateway productGateway;
    private final EventPublisherGateway eventPublisher;

    public CartItem addToCart(String userDocument, Long productId, Integer quantity) {
        if (userDocument == null || userDocument.trim().isEmpty())
            throw new RuntimeException("El documento del usuario no puede estar vacío");
        if (quantity == null || quantity <= 0)
            throw new RuntimeException("La cantidad debe ser mayor a 0");

        Product product = productGateway.findById(productId)
                .orElseThrow(() -> new RuntimeException("No existe un producto con id: " + productId));

        if (!Boolean.TRUE.equals(product.getActive()))
            throw new RuntimeException("El producto no está disponible");
        if (product.getStock() < quantity)
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock());

        CartItem cartItem = new CartItem(null, userDocument, productId, product.getName(), product.getPrice(), quantity);
        CartItem saved = cartGateway.save(cartItem);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.CART_ITEM_ADDED,
                "Producto agregado al carrito",
                "El usuario " + userDocument + " agregó " + quantity + "x " + product.getName() + " al carrito",
                userDocument,
                Map.of("cartItem", saved, "product", product)
        ));

        return saved;
    }

    public List<CartItem> getCart(String userDocument) {
        if (userDocument == null || userDocument.trim().isEmpty())
            throw new RuntimeException("El documento del usuario no puede estar vacío");
        return cartGateway.findByUserDocument(userDocument);
    }

    public CartItem updateCartItem(Long cartItemId, Integer quantity, String userDocument) {
        if (quantity == null || quantity <= 0)
            throw new RuntimeException("La cantidad debe ser mayor a 0");

        CartItem cartItem = cartGateway.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("No existe el ítem en el carrito con id: " + cartItemId));

        if (!cartItem.getUserDocument().equals(userDocument))
            throw new RuntimeException("No tienes permiso para modificar este ítem del carrito");

        Product product = productGateway.findById(cartItem.getProductId())
                .orElseThrow(() -> new RuntimeException("El producto asociado ya no existe"));

        if (product.getStock() < quantity)
            throw new RuntimeException("Stock insuficiente. Disponible: " + product.getStock());

        cartItem.setQuantity(quantity);
        CartItem updated = cartGateway.save(cartItem);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.CART_ITEM_UPDATED,
                "Carrito actualizado",
                "El usuario " + userDocument + " actualizó la cantidad de " + cartItem.getProductName() + " a " + quantity,
                userDocument,
                updated
        ));

        return updated;
    }

    public void removeFromCart(Long cartItemId, String userDocument) {
        CartItem cartItem = cartGateway.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("No existe el ítem en el carrito con id: " + cartItemId));

        if (!cartItem.getUserDocument().equals(userDocument))
            throw new RuntimeException("No tienes permiso para eliminar este ítem del carrito");

        cartGateway.deleteById(cartItemId);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.CART_ITEM_REMOVED,
                "Producto removido del carrito",
                "El usuario " + userDocument + " removió " + cartItem.getProductName() + " del carrito",
                userDocument,
                cartItem
        ));
    }

    public void purchaseCart(String userDocument) {
        List<CartItem> items = cartGateway.findByUserDocument(userDocument);
        if (items.isEmpty())
            throw new RuntimeException("El carrito está vacío");

        // Validar stock para todos los items antes de confirmar
        for (CartItem item : items) {
            Product product = productGateway.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getProductName() + " ya no existe"));
            if (product.getStock() < item.getQuantity())
                throw new RuntimeException("Stock insuficiente para: " + product.getName());
        }

        // Descontar stock
        for (CartItem item : items) {
            Product product = productGateway.findById(item.getProductId()).get();
            product.setStock(product.getStock() - item.getQuantity());
            productGateway.save(product);
        }

        // Vaciar carrito
        cartGateway.deleteAllByUserDocument(userDocument);

        eventPublisher.publish(CatalogEvent.of(
                CatalogEvent.EventType.CART_PURCHASED,
                "¡Compra realizada!",
                "El usuario " + userDocument + " realizó una compra con " + items.size() + " producto(s)",
                userDocument,
                items
        ));
    }
}
