package com.minimarket.inventory.service;

import com.minimarket.inventory.dto.InventoryRequestDTO;
import com.minimarket.inventory.dto.InventoryResponseDTO;
import com.minimarket.inventory.dto.MovementRequestDTO;
import com.minimarket.inventory.dto.MovementResponseDTO;
import com.minimarket.inventory.dto.ProductInventoryUnifiedDTO;

import java.util.List;

public interface InventoryService {

    List<InventoryResponseDTO> listarInventarios();

    InventoryResponseDTO obtenerInventarioPorId(Long id);

    InventoryResponseDTO registrarInventarioInicial(InventoryRequestDTO request);

    InventoryResponseDTO actualizarInventario(Long id, InventoryRequestDTO request);

    void eliminarInventario(Long id);

    MovementResponseDTO registrarMovimiento(MovementRequestDTO request);

    List<MovementResponseDTO> listarMovimientosPorProducto(Long productoId);

    ProductInventoryUnifiedDTO consultarDisponibilidadUnificada(Long productoId);
}
