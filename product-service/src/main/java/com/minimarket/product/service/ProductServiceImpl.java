package com.minimarket.product.service;

import com.minimarket.product.dto.ProductRequestDTO;
import com.minimarket.product.dto.ProductResponseDTO;
import com.minimarket.product.exception.BusinessRuleException;
import com.minimarket.product.exception.ResourceNotFoundException;
import com.minimarket.product.model.Product;
import com.minimarket.product.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de las reglas de negocio del catálogo de productos (Fase 1).
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDTO> obtenerTodos() {
        return productRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO obtenerPorId(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));
        return mapearAResponse(product);
    }

    @Override
    public ProductResponseDTO crearProducto(ProductRequestDTO request) {
        validarReglasDeNegocio(request, null);

        Product product = new Product();
        product.setNombre(request.getNombre().trim());
        product.setCategoria(request.getCategoria().trim());
        product.setPrecio(request.getPrecio());
        product.setDescripcion(request.getDescripcion());
        product.setCodigoBarras(request.getCodigoBarras().trim());
        product.setEstado(request.getEstado() != null && !request.getEstado().isBlank()
                ? request.getEstado().trim().toUpperCase()
                : "ACTIVO");

        Product guardado = productRepository.save(product);
        return mapearAResponse(guardado);
    }

    @Override
    public ProductResponseDTO actualizarProducto(Long id, ProductRequestDTO request) {
        Product existente = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + id));

        validarReglasDeNegocio(request, id);

        existente.setNombre(request.getNombre().trim());
        existente.setCategoria(request.getCategoria().trim());
        existente.setPrecio(request.getPrecio());
        existente.setDescripcion(request.getDescripcion());
        existente.setCodigoBarras(request.getCodigoBarras().trim());
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            existente.setEstado(request.getEstado().trim().toUpperCase());
        }

        Product actualizado = productRepository.save(existente);
        return mapearAResponse(actualizado);
    }

    @Override
    public void eliminarProducto(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con ID: " + id);
        }
        productRepository.deleteById(id);
    }

    private void validarReglasDeNegocio(ProductRequestDTO request, Long idActual) {
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new BusinessRuleException("No se permiten productos con nombres vacíos");
        }
        if (request.getCategoria() == null || request.getCategoria().trim().isEmpty()) {
            throw new BusinessRuleException("Todo producto debe pertenecer a una categoría");
        }
        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessRuleException("No se permiten precios negativos");
        }
        if (request.getCodigoBarras() == null || request.getCodigoBarras().trim().isEmpty()) {
            throw new BusinessRuleException("El código de barras es obligatorio");
        }

        String codigo = request.getCodigoBarras().trim();
        productRepository.findByCodigoBarras(codigo).ifPresent(p -> {
            if (idActual == null || !p.getId().equals(idActual)) {
                throw new BusinessRuleException("No se permiten códigos de barras duplicados: " + codigo);
            }
        });
    }

    private ProductResponseDTO mapearAResponse(Product p) {
        return new ProductResponseDTO(
                p.getId(),
                p.getNombre(),
                p.getCategoria(),
                p.getPrecio(),
                p.getDescripcion(),
                p.getCodigoBarras(),
                p.getEstado()
        );
    }
}
