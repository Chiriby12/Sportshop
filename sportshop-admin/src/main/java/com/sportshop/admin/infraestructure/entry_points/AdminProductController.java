package com.sportshop.admin.infraestructure.entry_points;

import com.sportshop.admin.application.dto.*;
import com.sportshop.admin.domain.model.AdminProduct;
import com.sportshop.admin.domain.usecase.AdminProductUseCase;
import com.sportshop.admin.infraestructure.mapper.AdminProductMapper;
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
 * Adaptador conductor (Driving Adapter) - CRUD de productos para el Admin.
 * Arquitectura Hexagonal: punto de entrada al hexágono vía HTTP.
 * Todos los endpoints requieren rol ADMIN.
 */
@RestController
@RequestMapping("/api/sportshop/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin - Productos", description = "CRUD completo de productos del catálogo. Solo ADMIN.")
public class AdminProductController {

    private final AdminProductUseCase productUseCase;
    private final AdminProductMapper productMapper;

    @PostMapping
    @Operation(summary = "Crear producto")
    public ResponseEntity<ApiResponse<AdminProduct>> createProduct(
            @Valid @RequestBody AdminProductRequestDTO dto,
            Authentication auth) {
        AdminProduct product = dtoToDomain(dto);
        AdminProduct saved = productUseCase.createProduct(product, getDocument(auth));
        return new ResponseEntity<>(ApiResponse.created("Producto creado exitosamente", saved), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Listar todos los productos (incluye inactivos)")
    public ResponseEntity<ApiResponse<List<AdminProduct>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.ok("Productos encontrados", productUseCase.getAllProducts()));
    }

    @GetMapping("/active")
    @Operation(summary = "Listar productos activos")
    public ResponseEntity<ApiResponse<List<AdminProduct>>> getActiveProducts() {
        return ResponseEntity.ok(ApiResponse.ok("Productos activos encontrados", productUseCase.getActiveProducts()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ApiResponse<AdminProduct>> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Producto encontrado", productUseCase.getProductById(id)));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Productos por categoría")
    public ResponseEntity<ApiResponse<List<AdminProduct>>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(ApiResponse.ok("Productos de categoría: " + category,
                productUseCase.getProductsByCategory(category)));
    }

    @GetMapping("/sport/{sport}")
    @Operation(summary = "Productos por deporte")
    public ResponseEntity<ApiResponse<List<AdminProduct>>> getBySport(@PathVariable String sport) {
        return ResponseEntity.ok(ApiResponse.ok("Productos del deporte: " + sport,
                productUseCase.getProductsBySport(sport)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar producto completo")
    public ResponseEntity<ApiResponse<AdminProduct>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody AdminProductRequestDTO dto,
            Authentication auth) {
        AdminProduct updated = productUseCase.updateProduct(id, dtoToDomain(dto), getDocument(auth));
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado exitosamente", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            Authentication auth) {
        productUseCase.deleteProduct(id, getDocument(auth));
        return ResponseEntity.ok(ApiResponse.ok("Producto " + id + " eliminado correctamente", null));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private AdminProduct dtoToDomain(AdminProductRequestDTO dto) {
        AdminProduct p = new AdminProduct();
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setBrand(dto.getBrand());
        p.setCategory(dto.getCategory());
        p.setSport(dto.getSport());
        p.setPrice(dto.getPrice());
        p.setStock(dto.getStock());
        p.setImageUrl(dto.getImageUrl());
        p.setActive(dto.getActive() != null ? dto.getActive() : true);
        return p;
    }

    private String getDocument(Authentication auth) {
        if (auth != null && auth.getDetails() instanceof String doc && !doc.isBlank()) return doc;
        if (auth != null) return auth.getName();
        return "unknown-admin";
    }
}
