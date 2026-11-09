package com.minimarket.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventoryRequestDTO {

    @NotNull(message = "El identificador del producto (productoId) es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad de existencias es obligatoria")
    @Min(value = 0, message = "No se permiten cantidades negativas en el inventario")
    private Integer cantidad;

    @NotBlank(message = "La ubicación en bodega o estantería es obligatoria")
    private String ubicacion;

    public InventoryRequestDTO() {
    }

    public InventoryRequestDTO(Long productoId, Integer cantidad, String ubicacion) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.ubicacion = ubicacion;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
