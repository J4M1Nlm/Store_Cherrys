package com.cherrytwins.shop.inventory.repository;

import com.cherrytwins.shop.inventory.domain.InventoryMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    Page<InventoryMovement> findAllByVariantIdOrderByCreatedAtDesc(Long variantId, Pageable pageable);
}