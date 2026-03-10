package com.cherrytwins.shop.orders.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.inventory.domain.InventoryMovement;
import com.cherrytwins.shop.inventory.domain.MovementType;
import com.cherrytwins.shop.inventory.repository.InventoryMovementRepository;
import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderItem;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.repository.OrderItemRepository;
import com.cherrytwins.shop.orders.repository.OrderRepository;
import com.cherrytwins.shop.catalog.domain.ProductVariant;
import com.cherrytwins.shop.catalog.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductVariantRepository variantRepository;
    private final InventoryMovementRepository inventoryMovementRepository;

    public AdminOrderService(OrderRepository orderRepository,
                             OrderItemRepository orderItemRepository,
                             ProductVariantRepository variantRepository,
                             InventoryMovementRepository inventoryMovementRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.variantRepository = variantRepository;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));

        // reglas mínimas realistas
        if (o.getStatus() == OrderStatus.CANCELLED) throw new BadRequestException("Order already cancelled");
        if (o.getStatus() == OrderStatus.REFUNDED && newStatus != OrderStatus.REFUNDED) {
            throw new BadRequestException("Cannot change status after refund");
        }

        o.setStatus(newStatus);
    }

    @Transactional
    public void cancelAndRestock(Long orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        if (o.getStatus() == OrderStatus.CANCELLED) return;

        // Solo cancelar si no está fulfilled (ejemplo de regla)
        if (o.getStatus() == OrderStatus.FULFILLED) throw new BadRequestException("Cannot cancel a fulfilled order");

        List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(orderId);

        // restock variants (best-effort)
        for (OrderItem it : items) {
            if (it.getVariantId() == null) continue;
            ProductVariant v = variantRepository.findById(it.getVariantId()).orElse(null);
            if (v == null) continue;

            v.setStockOnHand(v.getStockOnHand() + it.getQuantity());

            InventoryMovement m = new InventoryMovement();
            m.setVariantId(v.getId());
            m.setMovementType(MovementType.IN);
            m.setQuantity(it.getQuantity());
            m.setReason("ORDER_CANCEL_RESTOCK");
            m.setReferenceId("order:" + o.getId());
            inventoryMovementRepository.save(m);
        }

        o.setStatus(OrderStatus.CANCELLED);
    }
}