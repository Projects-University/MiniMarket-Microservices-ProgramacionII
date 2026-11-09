package com.minimarket.inventory.service;

import com.minimarket.inventory.client.ProductClient;
import com.minimarket.inventory.dto.InventoryRequestDTO;
import com.minimarket.inventory.dto.InventoryResponseDTO;
import com.minimarket.inventory.dto.MovementRequestDTO;
import com.minimarket.inventory.dto.MovementResponseDTO;
import com.minimarket.inventory.dto.ProductDTO;
import com.minimarket.inventory.dto.ProductInventoryUnifiedDTO;
import com.minimarket.inventory.exception.InsufficientStockException;
import com.minimarket.inventory.exception.ResourceNotFoundException;
import com.minimarket.inventory.model.Inventory;
import com.minimarket.inventory.model.InventoryMovement;
import com.minimarket.inventory.model.MovementType;
import com.minimarket.inventory.repository.InventoryMovementRepository;
import com.minimarket.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la lógica de control de existencias, movimientos e integración REST (Fases 2 y 3).
 */
@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ProductClient productClient;

    public InventoryServiceImpl(
            InventoryRepository inventoryRepository,
            InventoryMovementRepository movementRepository,
            ProductClient productClient) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.productClient = productClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDTO> listarInventarios() {
        return inventoryRepository.findAll()
                .stream()
                .map(this::mapearInventario)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO obtenerInventarioPorId(Long id) {
        Inventory inv = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de inventario no encontrado con ID: " + id));
        return mapearInventario(inv);
    }

    @Override
    public InventoryResponseDTO registrarInventarioInicial(InventoryRequestDTO request) {
        if (request.getCantidad() == null || request.getCantidad() < 0) {
            throw new InsufficientStockException("No se permiten cantidades negativas en el inventario");
        }

        // Fase 3: Verificar en Product-Service que el producto exista antes de crear su inventario
        ProductDTO producto = productClient.obtenerProductoValidado(request.getProductoId());

        LocalDateTime ahora = LocalDateTime.now();
        Inventory inventario = inventoryRepository.findByProductoId(producto.getId())
                .orElseGet(Inventory::new);

        inventario.setProductoId(producto.getId());
        inventario.setCantidad(request.getCantidad());
        inventario.setUbicacion(request.getUbicacion().trim());
        inventario.setFechaActualizacion(ahora);

        Inventory guardado = inventoryRepository.save(inventario);

        // Registrar movimiento inicial en el historial
        InventoryMovement mov = new InventoryMovement(
                null,
                producto.getId(),
                MovementType.ENTRADA,
                request.getCantidad(),
                ahora,
                "Asignación de stock inicial para " + producto.getNombre()
        );
        movementRepository.save(mov);

        return mapearInventario(guardado);
    }

    @Override
    public InventoryResponseDTO actualizarInventario(Long id, InventoryRequestDTO request) {
        Inventory existente = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de inventario no encontrado con ID: " + id));

        if (request.getCantidad() == null || request.getCantidad() < 0) {
            throw new InsufficientStockException("No se permiten cantidades negativas en el inventario");
        }

        productClient.obtenerProductoValidado(request.getProductoId());

        LocalDateTime ahora = LocalDateTime.now();
        existente.setProductoId(request.getProductoId());
        existente.setCantidad(request.getCantidad());
        existente.setUbicacion(request.getUbicacion().trim());
        existente.setFechaActualizacion(ahora);

        Inventory actualizado = inventoryRepository.save(existente);

        InventoryMovement ajuste = new InventoryMovement(
                null,
                existente.getProductoId(),
                MovementType.AJUSTE,
                request.getCantidad(),
                ahora,
                "Ajuste directo de inventario en ubicación " + existente.getUbicacion()
        );
        movementRepository.save(ajuste);

        return mapearInventario(actualizado);
    }

    @Override
    public void eliminarInventario(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registro de inventario no encontrado con ID: " + id);
        }
        inventoryRepository.deleteById(id);
    }

    @Override
    public MovementResponseDTO registrarMovimiento(MovementRequestDTO request) {
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new InsufficientStockException("La cantidad del movimiento debe ser mayor a cero");
        }

        // Validar disponibilidad y existencia del producto en Product-Service
        productClient.obtenerProductoValidado(request.getProductoId());

        Inventory inventario = inventoryRepository.findByProductoId(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe inventario inicial registrado para el producto con ID: " + request.getProductoId()));

        int stockActual = inventario.getCantidad();
        int nuevoStock;

        switch (request.getTipoMovimiento()) {
            case ENTRADA -> nuevoStock = stockActual + request.getCantidad();
            case SALIDA -> {
                if (request.getCantidad() > stockActual) {
                    throw new InsufficientStockException(
                            "Stock insuficiente: no se permite una salida (" + request.getCantidad()
                                    + ") superior al stock disponible (" + stockActual + ")");
                }
                nuevoStock = stockActual - request.getCantidad();
            }
            case AJUSTE -> nuevoStock = request.getCantidad();
            default -> throw new InsufficientStockException("Tipo de movimiento no soportado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        inventario.setCantidad(nuevoStock);
        inventario.setFechaActualizacion(ahora);
        inventoryRepository.save(inventario);

        InventoryMovement movimiento = new InventoryMovement(
                null,
                request.getProductoId(),
                request.getTipoMovimiento(),
                request.getCantidad(),
                ahora,
                request.getObservacion() != null ? request.getObservacion() : "Movimiento registrado"
        );
        InventoryMovement movGuardado = movementRepository.save(movimiento);

        return new MovementResponseDTO(
                movGuardado.getId(),
                movGuardado.getProductoId(),
                movGuardado.getTipoMovimiento(),
                movGuardado.getCantidad(),
                nuevoStock,
                movGuardado.getFechaMovimiento(),
                movGuardado.getObservacion()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovementResponseDTO> listarMovimientosPorProducto(Long productoId) {
        Inventory inv = inventoryRepository.findByProductoId(productoId).orElse(null);
        Integer stockActual = inv != null ? inv.getCantidad() : 0;

        return movementRepository.findByProductoIdOrderByFechaMovimientoDesc(productoId)
                .stream()
                .map(m -> new MovementResponseDTO(
                        m.getId(),
                        m.getProductoId(),
                        m.getTipoMovimiento(),
                        m.getCantidad(),
                        stockActual,
                        m.getFechaMovimiento(),
                        m.getObservacion()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductInventoryUnifiedDTO consultarDisponibilidadUnificada(Long productoId) {
        ProductDTO producto = productClient.obtenerProductoValidado(productoId);
        Inventory inventario = inventoryRepository.findByProductoId(productoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El producto existe en catálogo pero aún no tiene inventario asignado (productoId=" + productoId + ")"));

        List<MovementResponseDTO> movimientos = listarMovimientosPorProducto(productoId);

        return new ProductInventoryUnifiedDTO(
                producto,
                inventario.getId(),
                inventario.getCantidad(),
                inventario.getUbicacion(),
                inventario.getCantidad() > 0,
                inventario.getFechaActualizacion(),
                movimientos
        );
    }

    private InventoryResponseDTO mapearInventario(Inventory i) {
        return new InventoryResponseDTO(
                i.getId(),
                i.getProductoId(),
                i.getCantidad(),
                i.getUbicacion(),
                i.getFechaActualizacion()
        );
    }
}
