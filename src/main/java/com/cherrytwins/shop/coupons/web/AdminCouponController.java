package com.cherrytwins.shop.coupons.web;

import com.cherrytwins.shop.coupons.service.AdminCouponService;
import com.cherrytwins.shop.coupons.web.dto.AdminCouponRequest;
import com.cherrytwins.shop.coupons.web.dto.CouponResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Coupons (Admin)", description = "CRUD admin para cupones")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;

    public AdminCouponController(AdminCouponService adminCouponService) {
        this.adminCouponService = adminCouponService;
    }

    @Operation(summary = "Listar cupones (paginado)")
    @GetMapping
    public Page<CouponResponse> list(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "20") int size) {
        return adminCouponService.list(page, size);
    }

    @Operation(summary = "Obtener cupón por id")
    @GetMapping("/{id}")
    public CouponResponse get(@PathVariable Long id) {
        return adminCouponService.get(id);
    }

    @Operation(summary = "Crear cupón")
    @PostMapping
    public CouponResponse create(@Valid @RequestBody AdminCouponRequest req) {
        return adminCouponService.create(req);
    }

    @Operation(summary = "Actualizar cupón")
    @PutMapping("/{id}")
    public CouponResponse update(@PathVariable Long id, @Valid @RequestBody AdminCouponRequest req) {
        return adminCouponService.update(id, req);
    }

    @Operation(summary = "Activar/Desactivar cupón")
    @PutMapping("/{id}/active")
    public CouponResponse setActive(@PathVariable Long id, @RequestParam boolean value) {
        return adminCouponService.setActive(id, value);
    }

    @Operation(summary = "Eliminar cupón")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        adminCouponService.delete(id);
        return ResponseEntity.noContent().build();
    }
}