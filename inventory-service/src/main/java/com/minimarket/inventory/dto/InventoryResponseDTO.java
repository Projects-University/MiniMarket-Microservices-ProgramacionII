package com.minimarket.inventory.dto;

import java.time.LocalDateTime;

public class InventoryResponseDTO {

    private Long id;
    private Long productoId;
    private Integer cantidad;
    private String ubicacion;
    private LocalDateTime fechaActualizacion;

    public InventoryResponseDTO() {
    }

    public InventoryResponseDTO(Long id, Long productoId, Integer cantidad,
                                String ubicacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
