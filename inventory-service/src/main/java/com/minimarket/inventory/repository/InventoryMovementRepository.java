package com.minimarket.inventory.repository;

import com.minimarket.inventory.model.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findByProductoIdOrderByFechaMovimientoDesc(Long productoId);
}
