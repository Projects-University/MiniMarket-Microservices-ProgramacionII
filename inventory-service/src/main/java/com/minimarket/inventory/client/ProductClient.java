package com.minimarket.inventory.client;

import com.minimarket.inventory.dto.ProductDTO;
import com.minimarket.inventory.exception.ExternalServiceException;
import com.minimarket.inventory.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente REST encargado de la integración HTTP síncrona con Product-Service (Fase 3).
 */
@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClient(
            RestTemplate restTemplate,
            @Value("${clients.product-service.url:http://localhost:8081}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    /**
     * Consulta GET /products/{id} en Product-Service para verificar existencia y obtener metadatos del producto.
     */
    public ProductDTO obtenerProductoValidado(Long productoId) {
        String endpoint = productServiceUrl + "/products/" + productoId;
        log.info("[Integración REST] Consultando Product-Service en {}", endpoint);
        try {
            ProductDTO producto = restTemplate.getForObject(endpoint, ProductDTO.class);
            if (producto == null || producto.getId() == null) {
                throw new ResourceNotFoundException("El producto con ID " + productoId + " no existe en Product-Service");
            }
            log.info("[Integración REST] Producto verificado exitosamente: ID={} - {}", producto.getId(), producto.getNombre());
            return producto;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("[Integración REST] Producto con ID {} no encontrado en Product-Service (HTTP 404)", productoId);
            throw new ResourceNotFoundException("No se puede operar inventario: el producto con ID " + productoId + " no existe");
        } catch (RestClientException ex) {
            log.error("[Integración REST] Error de comunicación con Product-Service: {}", ex.getMessage());
            throw new ExternalServiceException("Error de comunicación con el microservicio Product-Service", ex);
        }
    }
}
