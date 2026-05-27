package com.sportshop.catalog.infraestructure.entry_points;

import com.sportshop.catalog.application.dto.ApiResponse;
import com.sportshop.catalog.application.dto.ProductRequestDTO;
import com.sportshop.catalog.application.dto.ProductResponseDTO;
import com.sportshop.catalog.domain.model.Product;
import com.sportshop.catalog.domain.usecase.ProductUseCase;
import com.sportshop.catalog.infraestructure.mapper.ProductMapper;
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
 * Adaptador conductor (Driving Adapter) - Catálogo de productos.
 * - GET públicos: cualquiera puede ver el catálogo sin autenticarse
 * - POST/PUT/DELETE: solo ADMIN
 * - /sync: endpoints internos llamados por sportshop-admin
 */
@RestController
@RequestMapping("/api/sportshop/catalog")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints para gestión y consulta de productos")
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;

    // ─── Endpoints PÚBLICOS ───────────────────────────────────────────────────

    @GetMapping("/products")
    @Operation(summary = "Listar productos activos", description = "Público - cualquiera puede ver el catálogo")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getActiveProducts() {
        List<ProductResponseDTO> products = productUseCase.getActiveProducts()
                .stream().map(productMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Productos encontrados", products));
    }

    @GetMapping("/products/all")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Listar TODOS los productos", description = "Solo ADMIN - incluye inactivos")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productUseCase.getAllProducts()
                .stream().map(productMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Todos los productos encontrados", products));
    }

    @GetMapping("/products/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Público")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        Product product = productUseCase.getProductById(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto encontrado", productMapper.toResponseDTO(product)));
    }

    @GetMapping("/products/category/{category}")
    @Operation(summary = "Productos por categoría", description = "Público")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getByCategory(@PathVariable String category) {
        List<ProductResponseDTO> products = productUseCase.getProductsByCategory(category)
                .stream().map(productMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Productos de categoría: " + category, products));
    }

    @GetMapping("/products/sport/{sport}")
    @Operation(summary = "Productos por deporte", description = "Público")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getBySport(@PathVariable String sport) {
        List<ProductResponseDTO> products = productUseCase.getProductsBySport(sport)
                .stream().map(productMapper::toResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.ok("Productos del deporte: " + sport, products));
    }

    // ─── Endpoints de ADMIN (CRUD) ────────────────────────────────────────────

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO dto,
            Authentication auth) {
        String adminDoc = getDocumentFromAuth(auth);
        Product product = productUseCase.createProduct(productMapper.fromRequestDTO(dto), adminDoc);
        return new ResponseEntity<>(
                ApiResponse.created("Producto creado exitosamente", productMapper.toResponseDTO(product)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto,
            Authentication auth) {
        String adminDoc = getDocumentFromAuth(auth);
        Product product = productUseCase.updateProduct(id, productMapper.fromRequestDTO(dto), adminDoc);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado exitosamente", productMapper.toResponseDTO(product)));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto", description = "Solo ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            Authentication auth) {
        String adminDoc = getDocumentFromAuth(auth);
        productUseCase.deleteProduct(id, adminDoc);
        return ResponseEntity.ok(ApiResponse.ok("Producto " + id + " eliminado correctamente", null));
    }

    // ─── Endpoints de SINCRONIZACIÓN desde admin (internos) ──────────────────

    @PostMapping("/products/sync/{adminId}")
    @Operation(summary = "Sincronizar producto desde admin", description = "Interno - llamado por sportshop-admin")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> syncProduct(
            @PathVariable Long adminId,
            @RequestBody ProductRequestDTO dto) {
        Product product = productUseCase.syncFromAdmin(adminId, productMapper.fromRequestDTO(dto));
        return ResponseEntity.ok(ApiResponse.ok("Producto sincronizado", productMapper.toResponseDTO(product)));
    }

    @DeleteMapping("/products/sync/{adminId}")
    @Operation(summary = "Eliminar producto sincronizado", description = "Interno - llamado por sportshop-admin")
    public ResponseEntity<ApiResponse<Void>> deleteSyncedProduct(@PathVariable Long adminId) {
        productUseCase.deleteByAdminId(adminId);
        return ResponseEntity.ok(ApiResponse.ok("Producto sincronizado eliminado", null));
    }

    private String getDocumentFromAuth(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof String doc) return doc;
        if (auth != null) return auth.getName();
        return "unknown";
    }
}