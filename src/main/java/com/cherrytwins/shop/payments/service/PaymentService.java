package com.cherrytwins.shop.payments.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.repository.OrderRepository;
import com.cherrytwins.shop.payments.domain.Payment;
import com.cherrytwins.shop.payments.domain.PaymentStatus;
import com.cherrytwins.shop.payments.repository.PaymentRepository;
import com.cherrytwins.shop.payments.service.provider.StripeSimulatedClient;
import com.cherrytwins.shop.payments.web.dto.PaymentInitResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class PaymentService {

    private static final String PROVIDER_STRIPE_SIM = "STRIPE_SIM";

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StripeSimulatedClient stripeSimulatedClient;

    public PaymentService(OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          StripeSimulatedClient stripeSimulatedClient) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.stripeSimulatedClient = stripeSimulatedClient;
    }

    @Transactional
    public PaymentInitResponse initPayment(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new NotFoundException("Order not found");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order is not payable (status=" + order.getStatus() + ")");
        }

        // Evita duplicados capturados
        var existing = paymentRepository.findAllByOrderIdOrderByCreatedAtDesc(orderId);
        boolean alreadyCaptured = existing.stream().anyMatch(p -> p.getStatus() == PaymentStatus.CAPTURED);
        if (alreadyCaptured) throw new BadRequestException("Order already paid");

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setProvider(PROVIDER_STRIPE_SIM);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setAmountCents(order.getTotalCents());
        payment.setCurrency(order.getCurrency());

        String clientSecret = stripeSimulatedClient.createClientSecret();
        payment.setProviderRef(clientSecret);

        payment = paymentRepository.save(payment);

        return new PaymentInitResponse(payment.getId(), payment.getProvider(), clientSecret, payment.getAmountCents(), payment.getCurrency());
    }

    @Transactional
    public void simulateSucceed(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new NotFoundException("Payment not found");
        }

        if (payment.getStatus() == PaymentStatus.CAPTURED) return;
        if (payment.getStatus() != PaymentStatus.INITIATED && payment.getStatus() != PaymentStatus.AUTHORIZED) {
            throw new BadRequestException("Payment cannot be captured (status=" + payment.getStatus() + ")");
        }

        // CAPTURE payment
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setPaidAt(OffsetDateTime.now());

        // Update order
        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PAID);
        }
    }

    @Transactional
    public void simulateFail(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Payment not found"));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUserId() == null || !order.getUserId().equals(userId)) {
            throw new NotFoundException("Payment not found");
        }

        if (payment.getStatus() == PaymentStatus.CAPTURED) {
            throw new BadRequestException("Payment already captured");
        }

        payment.setStatus(PaymentStatus.FAILED);
    }
}