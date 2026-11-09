package com.minimarket.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Respuesta unificada que integra los datos del producto en Product-Service
 * con las existencias e historial de movimientos en Inventory-Service (Fase 3 y Fase 4 Caso 4).
 */
public class ProductInventoryUnifiedDTO {

    private ProductDTO producto;
    private Long inventarioId;
    private Integer stockDisponible;
    private String ubicacion;
    private boolean disponible;
    private LocalDateTime ultimaActualizacion;
    private List<MovementResponseDTO> historialMovimientos;

    public ProductInventoryUnifiedDTO() {
    }

    public ProductInventoryUnifiedDTO(ProductDTO producto, Long inventarioId, Integer stockDisponible,
                                      String ubicacion, boolean disponible, LocalDateTime ultimaActualizacion,
                                      List<MovementResponseDTO> historialMovimientos) {
        this.producto = producto;
        this.inventarioId = inventarioId;
        this.stockDisponible = stockDisponible;
        this.ubicacion = ubicacion;
        this.disponible = disponible;
        this.ultimaActualizacion = ultimaActualizacion;
        this.historialMovimientos = historialMovimientos;
    }

    public ProductDTO getProducto() {
        return producto;
    }

    public Long getInventarioId() {
        return inventarioId;
    }

    public Integer getStockDisponible() {
        return stockDisponible;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public List<MovementResponseDTO> getHistorialMovimientos() {
        return historialMovimientos;
    }
}
