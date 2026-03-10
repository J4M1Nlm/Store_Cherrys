package com.cherrytwins.shop.orders.repository;

import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Order> findAllByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
}