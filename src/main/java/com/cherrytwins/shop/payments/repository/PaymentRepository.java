package com.cherrytwins.shop.payments.repository;

import com.cherrytwins.shop.payments.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByOrderIdOrderByCreatedAtDesc(Long orderId);
}