package com.cherrytwins.shop.orders.service;

import com.cherrytwins.shop.cart.domain.Cart;
import com.cherrytwins.shop.cart.domain.CartItem;
import com.cherrytwins.shop.cart.domain.CartStatus;
import com.cherrytwins.shop.cart.repository.CartItemRepository;
import com.cherrytwins.shop.cart.repository.CartRepository;
import com.cherrytwins.shop.catalog.domain.Product;
import com.cherrytwins.shop.catalog.domain.ProductVariant;
import com.cherrytwins.shop.catalog.repository.ProductRepository;
import com.cherrytwins.shop.catalog.repository.ProductVariantRepository;
import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.coupons.service.CouponService;
import com.cherrytwins.shop.inventory.domain.InventoryMovement;
import com.cherrytwins.shop.inventory.domain.MovementType;
import com.cherrytwins.shop.inventory.repository.InventoryMovementRepository;
import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderItem;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.repository.OrderItemRepository;
import com.cherrytwins.shop.orders.repository.OrderRepository;
import com.cherrytwins.shop.orders.web.dto.CheckoutRequest;
import com.cherrytwins.shop.orders.web.dto.OrderDetailResponse;
import com.cherrytwins.shop.orders.web.dto.OrderItemResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CheckoutService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final ProductVariantRepository variantRepository;
    private final ProductRepository productRepository;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final CouponService couponService;

    private final InventoryMovementRepository inventoryMovementRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public CheckoutService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductVariantRepository variantRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CouponService couponService,
            InventoryMovementRepository inventoryMovementRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.variantRepository = variantRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.couponService = couponService;
        this.inventoryMovementRepository = inventoryMovementRepository;
    }

    @Transactional
    public OrderDetailResponse checkout(Long userId, CheckoutRequest req) {
        Cart cart = cartRepository.findFirstByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Active cart not found"));

        List<CartItem> cartItems = cartItemRepository.findAllByCartIdOrderByIdAsc(cart.getId());
        if (cartItems.isEmpty()) throw new BadRequestException("Cart is empty");

        List<Long> variantIds = cartItems.stream().map(CartItem::getVariantId).distinct().toList();

        // ✅ lock variants to avoid oversell
        List<ProductVariant> lockedVariants = variantRepository.findAllByIdIn(variantIds);
        Map<Long, ProductVariant> variantMap = lockedVariants.stream()
                .collect(Collectors.toMap(ProductVariant::getId, v -> v));

        // load products for snapshot
        List<Long> productIds = lockedVariants.stream().map(ProductVariant::getProductId).distinct().toList();
        Map<Long, Product> productMap = productRepository.findAllById(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // validate all items
        String currency = lockedVariants.get(0).getCurrency();
        int subtotal = 0;

        // compute requested qty per variant
        Map<Long, Integer> qtyByVariant = new HashMap<>();
        for (CartItem ci : cartItems) {
            qtyByVariant.merge(ci.getVariantId(), ci.getQuantity(), Integer::sum);
        }

        for (CartItem ci : cartItems) {
            ProductVariant v = variantMap.get(ci.getVariantId());
            if (v == null) throw new BadRequestException("Variant not found in cart: " + ci.getVariantId());
            if (!v.isActive()) throw new BadRequestException("Variant inactive: " + v.getId());
            if (!v.getCurrency().equalsIgnoreCase(currency)) throw new BadRequestException("Mixed currencies not supported");
        }

        // validate stock
        for (var e : qtyByVariant.entrySet()) {
            ProductVariant v = variantMap.get(e.getKey());
            int requested = e.getValue();
            if (requested > v.getStockOnHand()) {
                throw new BadRequestException("Insufficient stock for variant " + v.getId()
                        + ". requested=" + requested + " available=" + v.getStockOnHand());
            }
        }

        // subtotal from current prices
        for (CartItem ci : cartItems) {
            ProductVariant v = variantMap.get(ci.getVariantId());
            subtotal += v.getPriceCents() * ci.getQuantity();
        }

        // coupon
        var couponApp = couponService.validateAndCompute(req.getCouponCode(), userId, subtotal, currency);
        int discount = couponApp.discountCents();

        int shipping = 0;
        int tax = 0;
        int total = Math.max(0, subtotal - discount + shipping + tax);

        // create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setCurrency(currency);
        order.setSubtotalCents(subtotal);
        order.setDiscountCents(discount);
        order.setShippingCents(shipping);
        order.setTaxCents(tax);
        order.setTotalCents(total);
        order.setCouponId(couponApp.couponId());
        order.setShippingAddressId(req.getShippingAddressId());
        order.setBillingAddressId(req.getBillingAddressId());
        order.setPlacedAt(OffsetDateTime.now());

        order = orderRepository.save(order);

        // create order items (snapshot)
        List<OrderItem> savedItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            ProductVariant v = variantMap.get(ci.getVariantId());
            Product p = productMap.get(v.getProductId());
            if (p == null) throw new BadRequestException("Product not found for variant " + v.getId());

            int unit = v.getPriceCents();
            int line = unit * ci.getQuantity();

            ObjectNode snapshot = objectMapper.createObjectNode();
            snapshot.put("variantName", v.getVariantName());
            snapshot.set("attributes", objectMapper.valueToTree(v.getAttributes()));
            snapshot.put("currency", v.getCurrency());
            snapshot.put("productId", v.getProductId());

            OrderItem oi = new OrderItem();
            oi.setOrderId(order.getId());
            oi.setVariantId(v.getId());
            oi.setProductName(p.getName());
            oi.setSku(v.getSku());
            oi.setVariantSnapshot(snapshot);
            oi.setUnitPriceCents(unit);
            oi.setQuantity(ci.getQuantity());
            oi.setLineTotalCents(line);

            savedItems.add(orderItemRepository.save(oi));
        }

        // ✅ Reserve stock (decrement) + inventory movement OUT
        for (var e : qtyByVariant.entrySet()) {
            ProductVariant v = variantMap.get(e.getKey());
            int requested = e.getValue();

            v.setStockOnHand(v.getStockOnHand() - requested);

            InventoryMovement m = new InventoryMovement();
            m.setVariantId(v.getId());
            m.setMovementType(MovementType.OUT);
            m.setQuantity(-requested);
            m.setReason("CHECKOUT_RESERVE");
            m.setReferenceId("order:" + order.getId());
            inventoryMovementRepository.save(m);
        }

        // coupon redemption
        couponService.redeem(order.getCouponId(), userId, order.getId());

        // cart -> checked_out
        cart.setStatus(CartStatus.CHECKED_OUT);

        return toDetail(order, savedItems);
    }

    private OrderDetailResponse toDetail(Order o, List<OrderItem> items) {
        List<OrderItemResponse> dtoItems = items.stream()
                .map(i -> new OrderItemResponse(
                        i.getId(), i.getVariantId(), i.getProductName(), i.getSku(),
                        i.getVariantSnapshot(), i.getUnitPriceCents(), i.getQuantity(), i.getLineTotalCents()
                ))
                .toList();

        return new OrderDetailResponse(
                o.getId(), o.getUserId(), o.getStatus().name(), o.getCurrency(),
                o.getSubtotalCents(), o.getDiscountCents(), o.getShippingCents(), o.getTaxCents(), o.getTotalCents(),
                o.getCouponId(), o.getShippingAddressId(), o.getBillingAddressId(),
                o.getPlacedAt(), o.getCreatedAt(),
                dtoItems
        );
    }
}
