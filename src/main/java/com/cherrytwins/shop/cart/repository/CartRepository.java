package com.cherrytwins.shop.cart.repository;

import com.cherrytwins.shop.cart.domain.Cart;
import com.cherrytwins.shop.cart.domain.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findFirstByUserIdAndStatus(Long userId, CartStatus status);
}