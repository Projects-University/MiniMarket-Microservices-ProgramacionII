package com.minimarket.product.service;

import com.minimarket.product.dto.ProductRequestDTO;
import com.minimarket.product.dto.ProductResponseDTO;
import com.minimarket.product.exception.BusinessRuleException;
import com.minimarket.product.exception.ResourceNotFoundException;
import com.minimarket.product.model.Product;
import com.minimarket.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias de Product-Service con JUnit 5 y Mockito (Fase 4).
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product productoEjemplo;

    @BeforeEach
    void setUp() {
        productoEjemplo = new Product(
                1L,
                "Arroz Diana Premium 1kg",
                "Granos y Cereales",
                new BigDecimal("4800.00"),
                "Arroz blanco de grano entero",
                "7702001001018",
                "ACTIVO"
        );
    }

    @Test
    @DisplayName("Creación exitosa de un producto válido")
    void crearProductoExitoso() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Arroz Diana Premium 1kg",
                "Granos y Cereales",
                new BigDecimal("4800.00"),
                "Arroz blanco de grano entero",
                "7702001001018",
                "ACTIVO"
        );

        when(productRepository.findByCodigoBarras("7702001001018")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(productoEjemplo);

        ProductResponseDTO respuesta = productService.crearProducto(request);

        assertNotNull(respuesta);
        assertEquals(1L, respuesta.getId());
        assertEquals("Arroz Diana Premium 1kg", respuesta.getNombre());
        assertEquals("7702001001018", respuesta.getCodigoBarras());
    }

    @Test
    @DisplayName("Rechazar producto con precio negativo (Regla de Negocio)")
    void rechazarPrecioNegativo() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Producto Inválido",
                "Bebidas",
                new BigDecimal("-500.00"),
                "Descripción",
                "7709999999991",
                "ACTIVO"
        );

        assertThrows(BusinessRuleException.class, () -> productService.crearProducto(request));
    }

    @Test
    @DisplayName("Rechazar código de barras duplicado (Regla de Negocio)")
    void rechazarCodigoBarrasDuplicado() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Otro Arroz",
                "Granos y Cereales",
                new BigDecimal("5000.00"),
                "Descripción",
                "7702001001018",
                "ACTIVO"
        );

        when(productRepository.findByCodigoBarras("7702001001018")).thenReturn(Optional.of(productoEjemplo));

        assertThrows(BusinessRuleException.class, () -> productService.crearProducto(request));
    }

    @Test
    @DisplayName("Consultar producto por identificador existente")
    void obtenerProductoPorIdExitoso() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));

        ProductResponseDTO respuesta = productService.obtenerPorId(1L);

        assertEquals(1L, respuesta.getId());
        assertEquals("Granos y Cereales", respuesta.getCategoria());
    }

    @Test
    @DisplayName("Actualizar producto existente")
    void actualizarProductoExitoso() {
        ProductRequestDTO request = new ProductRequestDTO(
                "Arroz Diana Premium 1kg Especial",
                "Granos y Cereales",
                new BigDecimal("5100.00"),
                "Actualizado",
                "7702001001018",
                "ACTIVO"
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(productoEjemplo));
        when(productRepository.findByCodigoBarras("7702001001018")).thenReturn(Optional.of(productoEjemplo));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductResponseDTO actualizado = productService.actualizarProducto(1L, request);

        assertEquals("Arroz Diana Premium 1kg Especial", actualizado.getNombre());
        assertEquals(new BigDecimal("5100.00"), actualizado.getPrecio());
    }

    @Test
    @DisplayName("Eliminar producto por identificador")
    void eliminarProductoExitoso() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.eliminarProducto(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Lanzar ResourceNotFoundException al consultar ID inexistente")
    void obtenerProductoInexistente() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.obtenerPorId(99L));
    }
}
