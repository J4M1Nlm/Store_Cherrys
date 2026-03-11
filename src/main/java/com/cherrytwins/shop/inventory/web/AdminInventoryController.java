package com.cherrytwins.shop.inventory.web;

import com.cherrytwins.shop.inventory.service.InventoryService;
import com.cherrytwins.shop.inventory.web.dto.InventoryAdjustRequest;
import com.cherrytwins.shop.inventory.web.dto.InventoryMovementResponse;
import com.cherrytwins.shop.inventory.web.dto.InventoryVariantStockResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Inventory (Admin)", description = "Gestión de stock y movimientos")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Ver stock de una variante")
    @GetMapping("/variants/{variantId}")
    public InventoryVariantStockResponse getVariant(@PathVariable Long variantId) {
        return inventoryService.getVariantStock(variantId);
    }

    @Operation(summary = "Ajustar stock (IN/OUT/ADJUST)", description = "Actualiza stock_on_hand y crea un inventory_movement (auditoría).")
    @PostMapping("/adjust")
    public InventoryVariantStockResponse adjust(@Valid @RequestBody InventoryAdjustRequest req) {
        return inventoryService.adjustStock(req);
    }

    @Operation(summary = "Historial de movimientos por variante (paginado)")
    @GetMapping("/variants/{variantId}/movements")
    public Page<InventoryMovementResponse> movements(@PathVariable Long variantId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "50") int size) {
        return inventoryService.movements(variantId, page, size);
    }

    @Operation(summary = "Guía ADJUST negativo", description = "Para restar con ADJUST: usa reason='NEG:...' o 'ADJUST_NEG ...'.")
    @GetMapping("/adjust/help")
    public ResponseEntity<String> help() {
        return ResponseEntity.ok("ADJUST positive: reason='ADJUST_POS ...' (or any). ADJUST negative: reason='NEG: ...' or 'ADJUST_NEG ...'.");
    }
}