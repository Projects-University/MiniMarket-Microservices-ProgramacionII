package com.minimarket.inventory.dto;

import java.math.BigDecimal;

/**
 * DTO compartido para deserializar la respuesta REST proveniente de Product-Service (Fase 3).
 */
public class ProductDTO {

    private Long id;
    private String nombre;
    private String categoria;
    private BigDecimal precio;
    private String descripcion;
    private String codigoBarras;
    private String estado;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String nombre, String categoria, BigDecimal precio,
                      String descripcion, String codigoBarras, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
        this.codigoBarras = codigoBarras;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
