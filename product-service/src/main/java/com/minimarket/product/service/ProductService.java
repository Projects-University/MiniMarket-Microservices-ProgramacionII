package com.minimarket.product.service;

import com.minimarket.product.dto.ProductRequestDTO;
import com.minimarket.product.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    List<ProductResponseDTO> obtenerTodos();

    ProductResponseDTO obtenerPorId(Long id);

    ProductResponseDTO crearProducto(ProductRequestDTO request);

    ProductResponseDTO actualizarProducto(Long id, ProductRequestDTO request);

    void eliminarProducto(Long id);
}
