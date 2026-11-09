package com.minimarket.inventory.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.inventory.client.ProductClient;
import com.minimarket.inventory.dto.InventoryRequestDTO;
import com.minimarket.inventory.dto.MovementRequestDTO;
import com.minimarket.inventory.dto.ProductDTO;
import com.minimarket.inventory.exception.ResourceNotFoundException;
import com.minimarket.inventory.model.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas de integración completas de los 4 Casos de Uso de Fase 4.
 */
@SpringBootTest
@AutoConfigureMockMvc
class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductClient productClient;

    @Test
    @DisplayName("Flujo Integrado Casos 1, 3 y 4: Crear stock, validar bloqueo por sobregiro y consultar unificado")
    void flujoIntegradoCompleto() throws Exception {
        ProductDTO prod = new ProductDTO(
                1L, "Arroz Diana Premium 1kg", "Granos y Cereales",
                new BigDecimal("4800.00"), "Arroz blanco seleccionado", "7702001001018", "ACTIVO"
        );
        when(productClient.obtenerProductoValidado(1L)).thenReturn(prod);

        // Caso 1: Crear inventario inicial (stock = 50)
        InventoryRequestDTO crearReq = new InventoryRequestDTO(1L, 50, "Pasillo A - Estante 1");
        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(50));

        // Caso 3: Intentar realizar salida superior al stock (80 > 50) -> HTTP 400 BAD REQUEST
        MovementRequestDTO salidaInvalida = new MovementRequestDTO(1L, MovementType.SALIDA, 80, "Salida excesiva");
        mockMvc.perform(post("/inventory/movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(salidaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        // Caso 4: Consultar un producto y sus existencias -> Información unificada correctamente
        mockMvc.perform(get("/inventory/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.producto.nombre").value("Arroz Diana Premium 1kg"))
                .andExpect(jsonPath("$.stockDisponible").value(50))
                .andExpect(jsonPath("$.disponible").value(true));
    }

    @Test
    @DisplayName("Caso 2: Intentar registrar inventario para producto inexistente -> HTTP 404 NOT FOUND")
    void caso2ProductoInexistenteRetorna404() throws Exception {
        when(productClient.obtenerProductoValidado(999L))
                .thenThrow(new ResourceNotFoundException("No se puede operar inventario: el producto con ID 999 no existe"));

        InventoryRequestDTO req = new InventoryRequestDTO(999L, 30, "Pasillo Z");
        mockMvc.perform(post("/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
