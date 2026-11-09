package com.minimarket.inventory.repository;

import com.minimarket.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductoId(Long productoId);

    boolean existsByProductoId(Long productoId);
}
