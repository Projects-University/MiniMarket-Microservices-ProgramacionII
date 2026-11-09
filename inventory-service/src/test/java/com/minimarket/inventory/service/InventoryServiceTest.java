package com.minimarket.inventory.service;

import com.minimarket.inventory.client.ProductClient;
import com.minimarket.inventory.dto.InventoryRequestDTO;
import com.minimarket.inventory.dto.InventoryResponseDTO;
import com.minimarket.inventory.dto.MovementRequestDTO;
import com.minimarket.inventory.dto.MovementResponseDTO;
import com.minimarket.inventory.dto.ProductDTO;
import com.minimarket.inventory.dto.ProductInventoryUnifiedDTO;
import com.minimarket.inventory.exception.InsufficientStockException;
import com.minimarket.inventory.exception.ResourceNotFoundException;
import com.minimarket.inventory.model.Inventory;
import com.minimarket.inventory.model.InventoryMovement;
import com.minimarket.inventory.model.MovementType;
import com.minimarket.inventory.repository.InventoryMovementRepository;
import com.minimarket.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias e integradas de Inventory-Service validando los 4 Casos de Prueba (Fase 4).
 */
@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private ProductDTO productoMock;
    private Inventory inventarioMock;

    @BeforeEach
    void setUp() {
        productoMock = new ProductDTO(
                1L,
                "Arroz Diana Premium 1kg",
                "Granos y Cereales",
                new BigDecimal("4800.00"),
                "Arroz blanco seleccionado",
                "7702001001018",
                "ACTIVO"
        );

        inventarioMock = new Inventory(
                10L,
                1L,
                50,
                "Pasillo A - Estante 1",
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Caso 1: Crear producto y registrar existencias iniciales correctamente")
    void caso1RegistrarExistenciasProductoValido() {
        InventoryRequestDTO request = new InventoryRequestDTO(1L, 50, "Pasillo A - Estante 1");

        when(productClient.obtenerProductoValidado(1L)).thenReturn(productoMock);
        when(inventoryRepository.findByProductoId(1L)).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventarioMock);
        when(movementRepository.save(any(InventoryMovement.class))).thenAnswer(i -> i.getArgument(0));

        InventoryResponseDTO response = inventoryService.registrarInventarioInicial(request);

        assertNotNull(response);
        assertEquals(1L, response.getProductoId());
        assertEquals(50, response.getCantidad());
        assertEquals("Pasillo A - Estante 1", response.getUbicacion());
    }

    @Test
    @DisplayName("Caso 2: Intentar registrar inventario para un producto inexistente -> HTTP 404 NOT FOUND")
    void caso2RegistrarInventarioProductoInexistente() {
        InventoryRequestDTO request = new InventoryRequestDTO(999L, 25, "Pasillo Z");

        when(productClient.obtenerProductoValidado(999L))
                .thenThrow(new ResourceNotFoundException("No se puede operar inventario: el producto con ID 999 no existe"));

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.registrarInventarioInicial(request));
    }

    @Test
    @DisplayName("Caso 3: Intentar realizar una salida superior al stock disponible -> HTTP 400 BAD REQUEST")
    void caso3SalidaSuperiorAlStockDisponible() {
        MovementRequestDTO salidaExcesiva = new MovementRequestDTO(
                1L,
                MovementType.SALIDA,
                80,
                "Intento de venta mayor al stock actual (50)"
        );

        when(productClient.obtenerProductoValidado(1L)).thenReturn(productoMock);
        when(inventoryRepository.findByProductoId(1L)).thenReturn(Optional.of(inventarioMock));

        assertThrows(InsufficientStockException.class, () -> inventoryService.registrarMovimiento(salidaExcesiva));
    }

    @Test
    @DisplayName("Movimientos de ENTRADA y SALIDA actualizan automáticamente las existencias")
    void movimientosEntradaYSalidaActualizanStock() {
        MovementRequestDTO entrada = new MovementRequestDTO(1L, MovementType.ENTRADA, 20, "Reposición proveedor");

        when(productClient.obtenerProductoValidado(1L)).thenReturn(productoMock);
        when(inventoryRepository.findByProductoId(1L)).thenReturn(Optional.of(inventarioMock));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
        when(movementRepository.save(any(InventoryMovement.class))).thenAnswer(i -> {
            InventoryMovement m = i.getArgument(0);
            m.setId(100L);
            return m;
        });

        MovementResponseDTO respEntrada = inventoryService.registrarMovimiento(entrada);
        assertEquals(70, respEntrada.getStockResultante());
        assertEquals(MovementType.ENTRADA, respEntrada.getTipoMovimiento());
    }

    @Test
    @DisplayName("Caso 4: Consultar un producto y sus existencias -> Información unificada correctamente")
    void caso4ConsultarProductoYExistenciasUnificadas() {
        InventoryMovement mov = new InventoryMovement(
                100L, 1L, MovementType.ENTRADA, 50, LocalDateTime.now(), "Stock inicial"
        );

        when(productClient.obtenerProductoValidado(1L)).thenReturn(productoMock);
        when(inventoryRepository.findByProductoId(1L)).thenReturn(Optional.of(inventarioMock));
        when(movementRepository.findByProductoIdOrderByFechaMovimientoDesc(1L)).thenReturn(List.of(mov));

        ProductInventoryUnifiedDTO unificado = inventoryService.consultarDisponibilidadUnificada(1L);

        assertNotNull(unificado);
        assertEquals("Arroz Diana Premium 1kg", unificado.getProducto().getNombre());
        assertEquals(50, unificado.getStockDisponible());
        assertTrue(unificado.isDisponible());
        assertEquals(1, unificado.getHistorialMovimientos().size());
    }
}
