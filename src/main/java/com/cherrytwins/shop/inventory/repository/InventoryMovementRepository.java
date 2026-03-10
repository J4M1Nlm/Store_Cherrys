package com.cherrytwins.shop.inventory.repository;

import com.cherrytwins.shop.inventory.domain.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}