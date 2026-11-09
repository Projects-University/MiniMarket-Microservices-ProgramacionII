package com.minimarket.inventory.dto;

import com.minimarket.inventory.model.MovementType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class MovementRequestDTO {

    @NotNull(message = "El identificador del producto (productoId) es obligatorio")
    private Long productoId;

    @NotNull(message = "El tipo de movimiento (ENTRADA, SALIDA, AJUSTE) es obligatorio")
    private MovementType tipoMovimiento;

    @NotNull(message = "La cantidad del movimiento es obligatoria")
    @Min(value = 1, message = "La cantidad del movimiento debe ser mayor a cero")
    private Integer cantidad;

    private String observacion;

    public MovementRequestDTO() {
    }

    public MovementRequestDTO(Long productoId, MovementType tipoMovimiento, Integer cantidad, String observacion) {
        this.productoId = productoId;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.observacion = observacion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public MovementType getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(MovementType tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
}
