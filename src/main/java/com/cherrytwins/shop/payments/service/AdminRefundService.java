package com.cherrytwins.shop.payments.service;

import com.cherrytwins.shop.catalog.domain.ProductVariant;
import com.cherrytwins.shop.catalog.repository.ProductVariantRepository;
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
import com.cherrytwins.shop.payments.domain.Payment;
import com.cherrytwins.shop.payments.domain.PaymentStatus;
import com.cherrytwins.shop.payments.repository.PaymentRepository;
import com.cherrytwins.shop.payments.service.provider.StripeRefundClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class AdminRefundService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;

    private final StripeRefundClient stripeRefundClient;

    private final ProductVariantRepository variantRepository;
    private final InventoryMovementRepository movementRepository;

    public AdminRefundService(OrderRepository orderRepository,
                              OrderItemRepository orderItemRepository,
                              PaymentRepository paymentRepository,
                              StripeRefundClient stripeRefundClient,
                              ProductVariantRepository variantRepository,
                              InventoryMovementRepository movementRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.stripeRefundClient = stripeRefundClient;
        this.variantRepository = variantRepository;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public void refundOrder(Long orderId, boolean restock) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.FULFILLED) {
            throw new BadRequestException("Order cannot be refunded (status=" + order.getStatus() + ")");
        }

        // Buscar último pago CAPTURED
        List<Payment> payments = paymentRepository.findAllByOrderIdOrderByCreatedAtDesc(orderId);
        Payment captured = payments.stream()
                .filter(p -> p.getStatus() == PaymentStatus.CAPTURED)
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No CAPTURED payment found for this order"));

        if (!"STRIPE".equalsIgnoreCase(captured.getProvider())) {
            throw new BadRequestException("Refund supported only for STRIPE provider for now");
        }

        // providerRef = payment_intent_id
        stripeRefundClient.refundPaymentIntent(captured.getProviderRef());

        // Cambiar estados (idempotente)
        captured.setStatus(PaymentStatus.REFUNDED);
        // paidAt se queda; opcional podríamos agregar refundedAt pero no hay columna
        order.setStatus(OrderStatus.REFUNDED);

        // Restock opcional
        if (restock) {
            List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(orderId);
            for (OrderItem it : items) {
                if (it.getVariantId() == null) continue;

                ProductVariant v = variantRepository.findById(it.getVariantId()).orElse(null);
                if (v == null) continue;

                v.setStockOnHand(v.getStockOnHand() + it.getQuantity());

                InventoryMovement m = new InventoryMovement();
                m.setVariantId(v.getId());
                m.setMovementType(MovementType.IN);
                m.setQuantity(it.getQuantity());
                m.setReason("REFUND_RESTOCK");
                m.setReferenceId("order:" + orderId + ":refund@" + OffsetDateTime.now());
                movementRepository.save(m);
            }
        }
    }
}