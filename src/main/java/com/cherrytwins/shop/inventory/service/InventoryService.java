package com.cherrytwins.shop.inventory.service;

import com.cherrytwins.shop.catalog.domain.ProductVariant;
import com.cherrytwins.shop.catalog.repository.ProductVariantRepository;
import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.inventory.domain.InventoryMovement;
import com.cherrytwins.shop.inventory.domain.MovementType;
import com.cherrytwins.shop.inventory.repository.InventoryMovementRepository;
import com.cherrytwins.shop.inventory.web.dto.InventoryAdjustRequest;
import com.cherrytwins.shop.inventory.web.dto.InventoryMovementResponse;
import com.cherrytwins.shop.inventory.web.dto.InventoryVariantStockResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class InventoryService {

    private final ProductVariantRepository variantRepository;
    private final InventoryMovementRepository movementRepository;

    public InventoryService(ProductVariantRepository variantRepository,
                            InventoryMovementRepository movementRepository) {
        this.variantRepository = variantRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public InventoryVariantStockResponse adjustStock(InventoryAdjustRequest req) {
        MovementType type = parseType(req.getMovementType());
        int qty = req.getQuantity();

        // lock variant row
        ProductVariant v = variantRepository.findById(req.getVariantId())
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        if (qty <= 0) throw new BadRequestException("Quantity must be >= 1");

        int deltaSigned = switch (type) {
            case IN -> qty;          // +qty
            case OUT -> -qty;        // -qty
            case ADJUST -> {         // puede ser + o -, pero request siempre positivo
                // ADJUST lo tratamos como "set stock by delta"? aquí lo usamos como delta positivo/negativo controlado por reason:
                // para hacerlo completo, permitimos que si reason contiene "NEG" -> resta; si no -> suma
                // pero mejor: agregamos un prefix estándar: "ADJUST_NEG" o "ADJUST_POS"
                String r = req.getReason() == null ? "" : req.getReason().trim().toUpperCase(Locale.ROOT);
                if (r.startsWith("NEG:") || r.startsWith("ADJUST_NEG")) yield -qty;
                yield qty;
            }
        };

        int newStock = v.getStockOnHand() + deltaSigned;
        if (newStock < 0) {
            throw new BadRequestException("Stock cannot go below 0. current=" + v.getStockOnHand() + " delta=" + deltaSigned);
        }

        v.setStockOnHand(newStock);

        InventoryMovement m = new InventoryMovement();
        m.setVariantId(v.getId());
        m.setMovementType(type);
        m.setQuantity(deltaSigned);
        m.setReason(req.getReason());
        m.setReferenceId(req.getReferenceId());
        movementRepository.save(m);

        return new InventoryVariantStockResponse(
                v.getId(),
                v.getSku(),
                v.getVariantName(),
                v.getAttributes(),
                v.getStockOnHand(),
                v.isActive()
        );
    }

    public Page<InventoryMovementResponse> movements(Long variantId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return movementRepository.findAllByVariantIdOrderByCreatedAtDesc(variantId, pageable)
                .map(m -> new InventoryMovementResponse(
                        m.getId(),
                        m.getVariantId(),
                        m.getMovementType().name(),
                        m.getQuantity(),
                        m.getReason(),
                        m.getReferenceId(),
                        m.getCreatedAt()
                ));
    }

    public InventoryVariantStockResponse getVariantStock(Long variantId) {
        ProductVariant v = variantRepository.findById(variantId)
                .orElseThrow(() -> new NotFoundException("Variant not found"));

        return new InventoryVariantStockResponse(
                v.getId(),
                v.getSku(),
                v.getVariantName(),
                v.getAttributes(),
                v.getStockOnHand(),
                v.isActive()
        );
    }

    private MovementType parseType(String s) {
        if (s == null) throw new BadRequestException("movementType is required");
        try {
            return MovementType.valueOf(s.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new BadRequestException("Invalid movementType. Use IN, OUT, ADJUST");
        }
    }
}