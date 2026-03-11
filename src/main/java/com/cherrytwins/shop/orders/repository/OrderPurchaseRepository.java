package com.cherrytwins.shop.orders.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface OrderPurchaseRepository extends JpaRepository<com.cherrytwins.shop.orders.domain.Order, Long> {

    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM orders o
        JOIN order_items oi ON oi.order_id = o.id
        JOIN product_variants pv ON pv.id = oi.variant_id
        WHERE o.user_id = :userId
          AND pv.product_id = :productId
          AND o.status IN ('PAID','FULFILLED')
        """, nativeQuery = true)
    boolean hasPurchasedProduct(@Param("userId") Long userId, @Param("productId") Long productId);
}