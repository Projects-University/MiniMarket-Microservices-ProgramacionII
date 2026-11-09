package com.minimarket.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.product.dto.ProductRequestDTO;
import com.minimarket.product.dto.ProductResponseDTO;
import com.minimarket.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("GET /products retorna 200 OK y lista de productos")
    void listarProductosRetorna200() throws Exception {
        ProductResponseDTO p = new ProductResponseDTO(
                1L, "Arroz Diana Premium 1kg", "Granos y Cereales",
                new BigDecimal("4800.00"), "Arroz blanco", "7702001001018", "ACTIVO"
        );
        when(productService.obtenerTodos()).thenReturn(List.of(p));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Arroz Diana Premium 1kg"));
    }

    @Test
    @DisplayName("POST /products con datos válidos retorna 201 CREATED")
    void crearProductoRetorna201() throws Exception {
        ProductRequestDTO req = new ProductRequestDTO(
                "Leche Entera Alquería 1.1L", "Lácteos",
                new BigDecimal("5200.00"), "Leche larga vida", "7702177002025", "ACTIVO"
        );
        ProductResponseDTO resp = new ProductResponseDTO(
                2L, "Leche Entera Alquería 1.1L", "Lácteos",
                new BigDecimal("5200.00"), "Leche larga vida", "7702177002025", "ACTIVO"
        );
        when(productService.crearProducto(any(ProductRequestDTO.class))).thenReturn(resp);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.codigoBarras").value("7702177002025"));
    }
}
