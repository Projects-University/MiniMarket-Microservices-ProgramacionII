package com.minimarket.product.repository;

import com.minimarket.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la gestión persistente de la entidad Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCodigoBarras(String codigoBarras);

    Optional<Product> findByCodigoBarras(String codigoBarras);
}
