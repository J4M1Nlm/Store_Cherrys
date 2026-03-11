package com.cherrytwins.shop.payments.service;

import com.cherrytwins.shop.common.exception.BadRequestException;
import com.cherrytwins.shop.common.exception.NotFoundException;
import com.cherrytwins.shop.orders.domain.Order;
import com.cherrytwins.shop.orders.domain.OrderStatus;
import com.cherrytwins.shop.orders.repository.OrderRepository;
import com.cherrytwins.shop.payments.domain.Payment;
import com.cherrytwins.shop.payments.domain.PaymentStatus;
import com.cherrytwins.shop.payments.repository.PaymentRepository;
import com.cherrytwins.shop.payments.service.provider.StripeClient;
import com.cherrytwins.shop.payments.service.provider.StripeSimulatedClient;
import com.cherrytwins.shop.payments.web.dto.PaymentInitResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${app.payments.provider:stripe_sim}")
    private String providerMode; // stripe | stripe_sim

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    private final StripeSimulatedClient stripeSimulatedClient;
    private final StripeClient stripeClient;

    public PaymentService(OrderRepository orderRepository,
                          PaymentRepository paymentRepository,
                          StripeSimulatedClient stripeSimulatedClient,
                          StripeClient stripeClient) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.stripeSimulatedClient = stripeSimulatedClient;
        this.stripeClient = stripeClient;
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

        List<Payment> existing = paymentRepository.findAllByOrderIdOrderByCreatedAtDesc(orderId);
        if (existing.stream().anyMatch(p -> p.getStatus() == PaymentStatus.CAPTURED)) {
            throw new BadRequestException("Order already paid");
        }

        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setStatus(PaymentStatus.INITIATED);
        payment.setAmountCents(order.getTotalCents());
        payment.setCurrency(order.getCurrency());
        // provider es obligatorio en BD: asignar antes de save
        payment.setProvider("stripe".equalsIgnoreCase(providerMode) ? "STRIPE" : "STRIPE_SIM");

        payment = paymentRepository.save(payment); // guardamos para tener paymentId

        if ("stripe".equalsIgnoreCase(providerMode)) {
            // Stripe real: PaymentIntent
            var intent = stripeClient.createPaymentIntent(
                    payment.getAmountCents(),
                    payment.getCurrency(),
                    Map.of(
                            "orderId", String.valueOf(orderId),
                            "paymentId", String.valueOf(payment.getId()),
                            "userId", String.valueOf(userId)
                    )
            );

            payment.setProvider("STRIPE");
            payment.setProviderRef(intent.getId()); // payment_intent id

            return new PaymentInitResponse(
                    payment.getId(),
                    payment.getProvider(),
                    intent.getClientSecret(),
                    payment.getAmountCents(),
                    payment.getCurrency()
            );
        }

        payment.setProvider("STRIPE_SIM");
        String clientSecret = stripeSimulatedClient.createClientSecret();
        payment.setProviderRef(clientSecret);

        return new PaymentInitResponse(payment.getId(), payment.getProvider(), clientSecret, payment.getAmountCents(), payment.getCurrency());
    }

    // ---------- Sim endpoints (se quedan para dev) ----------
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

        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setPaidAt(OffsetDateTime.now());
        if (order.getStatus() == OrderStatus.PENDING) order.setStatus(OrderStatus.PAID);
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

        if (payment.getStatus() == PaymentStatus.CAPTURED) throw new BadRequestException("Payment already captured");
        payment.setStatus(PaymentStatus.FAILED);
    }

    // ---------- Webhook updates (Stripe real) ----------
    @Transactional
    public void markCapturedByProviderRef(String providerRef) {
        Payment payment = paymentRepository.findByProviderAndProviderRef("STRIPE", providerRef)
                .orElseThrow(() -> new NotFoundException("Payment not found for providerRef"));

        // Idempotencia: si ya está CAPTURED, no haces nada
        if (payment.getStatus() == PaymentStatus.CAPTURED) return;

        // Si estaba FAILED pero llega succeeded (pasa en edge cases), permitimos CAPTURED
        payment.setStatus(PaymentStatus.CAPTURED);
        payment.setPaidAt(OffsetDateTime.now());

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.PENDING) {
            order.setStatus(OrderStatus.PAID);
        }
    }

    @Transactional
    public void markFailedByProviderRef(String providerRef) {
        Payment payment = paymentRepository.findByProviderAndProviderRef("STRIPE", providerRef)
                .orElseThrow(() -> new NotFoundException("Payment not found for providerRef"));

        // Si ya se capturó, no lo bajamos
        if (payment.getStatus() == PaymentStatus.CAPTURED) return;

        // Idempotencia: si ya está FAILED, no repitas trabajo
        if (payment.getStatus() == PaymentStatus.FAILED) return;

        payment.setStatus(PaymentStatus.FAILED);
    }
}