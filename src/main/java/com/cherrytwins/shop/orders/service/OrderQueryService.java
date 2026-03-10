package com.cherrytwins.shop.orders.service;

import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.common.pagination.PageResponse;
import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderItem;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.repository.OrderItemRepository;
import com.cherrytwins.shop.orders.repository.OrderRepository;
import com.cherrytwins.shop.orders.web.dto.OrderDetailResponse;
import com.cherrytwins.shop.orders.web.dto.OrderItemResponse;
import com.cherrytwins.shop.orders.web.dto.OrderSummaryResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderQueryService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderQueryService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public PageResponse<OrderSummaryResponse> myOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<OrderSummaryResponse> p = orderRepository.findAllByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(o -> new OrderSummaryResponse(o.getId(), o.getStatus().name(), o.getTotalCents(), o.getCurrency(), o.getCreatedAt()));
        return PageResponse.from(p);
    }

    public OrderDetailResponse myOrderDetail(Long userId, Long orderId) {
        Order o = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException("Order not found"));
        if (o.getUserId() == null || !o.getUserId().equals(userId)) throw new NotFoundException("Order not found");

        List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(orderId);
        return toDetail(o, items);
    }

    public PageResponse<OrderSummaryResponse> adminOrders(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Order> orders = (status == null)
                ? orderRepository.findAll(pageable)
                : orderRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable);

        Page<OrderSummaryResponse> mapped = orders.map(o ->
                new OrderSummaryResponse(o.getId(), o.getStatus().name(), o.getTotalCents(), o.getCurrency(), o.getCreatedAt())
        );

        return PageResponse.from(mapped);
    }

    private OrderDetailResponse toDetail(Order o, List<OrderItem> items) {
        List<OrderItemResponse> dtoItems = items.stream()
                .map(i -> new OrderItemResponse(
                        i.getId(), i.getVariantId(), i.getProductName(), i.getSku(),
                        i.getVariantSnapshot(), i.getUnitPriceCents(), i.getQuantity(), i.getLineTotalCents()
                )).toList();

        return new OrderDetailResponse(
                o.getId(), o.getUserId(), o.getStatus().name(), o.getCurrency(),
                o.getSubtotalCents(), o.getDiscountCents(), o.getShippingCents(), o.getTaxCents(), o.getTotalCents(),
                o.getCouponId(), o.getShippingAddressId(), o.getBillingAddressId(),
                o.getPlacedAt(), o.getCreatedAt(),
                dtoItems
        );
    }
}