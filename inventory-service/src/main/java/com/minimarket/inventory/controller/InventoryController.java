package com.minimarket.inventory.controller;

import com.minimarket.inventory.dto.InventoryRequestDTO;
import com.minimarket.inventory.dto.InventoryResponseDTO;
import com.minimarket.inventory.dto.MovementRequestDTO;
import com.minimarket.inventory.dto.MovementResponseDTO;
import com.minimarket.inventory.dto.ProductInventoryUnifiedDTO;
import com.minimarket.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST de Inventory-Service (/inventory) para existencias, movimientos y consulta unificada.
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> listarInventarios() {
        return ResponseEntity.ok(inventoryService.listarInventarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.obtenerInventarioPorId(id));
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> crearInventario(@Valid @RequestBody InventoryRequestDTO request) {
        InventoryResponseDTO creado = inventoryService.registrarInventarioInicial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> actualizarInventario(
            @PathVariable Long id,
            @Valid @RequestBody InventoryRequestDTO request) {
        return ResponseEntity.ok(inventoryService.actualizarInventario(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarInventario(@PathVariable Long id) {
        inventoryService.eliminarInventario(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Endpoint de integración REST (Fase 3): Consulta disponibilidad unificando Product-Service e Inventory-Service.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductInventoryUnifiedDTO> consultarDisponibilidadPorProducto(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.consultarDisponibilidadUnificada(productId));
    }

    /**
     * Registro de movimientos de inventario: ENTRADA, SALIDA o AJUSTE (Fase 2).
     */
    @PostMapping("/movements")
    public ResponseEntity<MovementResponseDTO> registrarMovimiento(@Valid @RequestBody MovementRequestDTO request) {
        MovementResponseDTO movimiento = inventoryService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }

    @GetMapping("/movements/product/{productId}")
    public ResponseEntity<List<MovementResponseDTO>> obtenerHistorialMovimientos(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.listarMovimientosPorProducto(productId));
    }
}
