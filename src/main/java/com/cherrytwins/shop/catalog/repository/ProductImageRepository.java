package com.cherrytwins.shop.catalog.repository;

import com.cherrytwins.shop.catalog.domain.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findAllByProductIdOrderBySortOrderAscIdAsc(Long productId);
    void deleteAllByProductId(Long productId);
}