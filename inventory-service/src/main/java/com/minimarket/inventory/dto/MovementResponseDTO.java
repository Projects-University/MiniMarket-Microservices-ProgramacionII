package com.minimarket.inventory.dto;

import com.minimarket.inventory.model.MovementType;

import java.time.LocalDateTime;

public class MovementResponseDTO {

    private Long id;
    private Long productoId;
    private MovementType tipoMovimiento;
    private Integer cantidad;
    private Integer stockResultante;
    private LocalDateTime fechaMovimiento;
    private String observacion;

    public MovementResponseDTO() {
    }

    public MovementResponseDTO(Long id, Long productoId, MovementType tipoMovimiento,
                               Integer cantidad, Integer stockResultante,
                               LocalDateTime fechaMovimiento, String observacion) {
        this.id = id;
        this.productoId = productoId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.stockResultante = stockResultante;
        this.fechaMovimiento = fechaMovimiento;
        this.observacion = observacion;
    }

    public Long getId() {
        return id;
    }

    public Long getProductoId() {
        return productoId;
    }

    public MovementType getTipoMovimiento() {
        return tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Integer getStockResultante() {
        return stockResultante;
    }

    public LocalDateTime getFechaMovimiento() {
        return fechaMovimiento;
    }

    public String getObservacion() {
        return observacion;
    }
}
