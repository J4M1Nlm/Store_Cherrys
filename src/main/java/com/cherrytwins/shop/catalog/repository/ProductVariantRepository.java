package com.cherrytwins.shop.catalog.repository;

import com.cherrytwins.shop.catalog.domain.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findAllByProductIdOrderByIdAsc(Long productId);
    List<ProductVariant> findAllByProductIdAndActiveTrueOrderByIdAsc(Long productId);
    boolean existsBySku(String sku);
}
