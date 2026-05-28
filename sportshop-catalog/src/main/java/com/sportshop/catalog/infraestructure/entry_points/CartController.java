package com.sportshop.catalog.infraestructure.entry_points;

import com.sportshop.catalog.application.dto.ApiResponse;
import com.sportshop.catalog.application.dto.CartRequestDTO;
import com.sportshop.catalog.application.dto.CartResponseDTO;
import com.sportshop.catalog.domain.model.CartItem;
import com.sportshop.catalog.domain.usecase.CartUseCase;
import com.sportshop.catalog.infraestructure.mapper.CartMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador del carrito de compras.
 * Un usuario solo puede ver/modificar SU carrito.
 */
@RestController
@RequestMapping("/api/sportshop/catalog/cart")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Endpoints del carrito de compras del usuario")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartUseCase cartUseCase;
    private final CartMapper cartMapper;

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Ver mi carrito")
    public ResponseEntity<ApiResponse<List<CartResponseDTO>>> getMyCart(Authentication auth) {
        String document = getDocument(auth);
        List<CartResponseDTO> items = cartUseCase.getCart(document)
                .stream().map(cartMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Carrito del usuario " + document, items));
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Agregar producto al carrito")
    public ResponseEntity<ApiResponse<CartResponseDTO>> addToCart(
            @Valid @RequestBody CartRequestDTO dto,
            Authentication auth) {
        String document = getDocument(auth);
        CartItem item = cartUseCase.addToCart(document, dto.getProductId(), dto.getQuantity());
        return new ResponseEntity<>(
                ApiResponse.created("Producto agregado al carrito", cartMapper.toResponseDTO(item)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Actualizar cantidad de un ítem del carrito")
    public ResponseEntity<ApiResponse<CartResponseDTO>> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestParam Integer quantity,
            Authentication auth) {
        String document = getDocument(auth);
        CartItem item = cartUseCase.updateCartItem(cartItemId, quantity, document);
        return ResponseEntity.ok(ApiResponse.ok("Ítem actualizado", cartMapper.toResponseDTO(item)));
    }

    @DeleteMapping("/{cartItemId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Eliminar un ítem del carrito")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long cartItemId,
            Authentication auth) {
        String document = getDocument(auth);
        cartUseCase.removeFromCart(cartItemId, document);
        return ResponseEntity.ok(ApiResponse.ok("Ítem removido del carrito", null));
    }

    @PostMapping("/purchase")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Comprar todo el carrito", description = "Descuenta stock y vacía el carrito")
    public ResponseEntity<ApiResponse<Void>> purchase(Authentication auth) {
        String document = getDocument(auth);
        cartUseCase.purchaseCart(document);
        return ResponseEntity.ok(ApiResponse.ok("¡Compra realizada exitosamente!", null));
    }

    private String getDocument(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof String doc && !doc.isBlank()) return doc;
        if (auth != null) return auth.getName();
        return "unknown";
    }
}
