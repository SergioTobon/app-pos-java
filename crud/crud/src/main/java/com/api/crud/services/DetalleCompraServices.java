package com.api.crud.services;

import com.api.crud.models.DetalleCompraModel;
import com.api.crud.repositories.DetalleCompraRepository;
import com.api.crud.dto.DetalleCompraResponseDTO;
import com.api.crud.dto.ProductoDTO; // Para el DTO del producto anidado
// Nuevas importaciones para mapear proveedores asociados en ProductoDTO
import com.api.crud.dto.ProductoProveedorDTO;
import java.util.stream.Collectors; // Para usar Stream API

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DetalleCompraServices {

    private final DetalleCompraRepository detalleCompraRepository;

    @Autowired
    public DetalleCompraServices(DetalleCompraRepository detalleCompraRepository) {
        this.detalleCompraRepository = detalleCompraRepository;
    }

    // --- Métodos de Mapeo (Helper Methods) ---

    // Mapea DetalleCompraModel a DetalleCompraResponseDTO
    private DetalleCompraResponseDTO mapDetalleCompraModelToResponseDTO(DetalleCompraModel detalleModel) {
        DetalleCompraResponseDTO dto = new DetalleCompraResponseDTO();
        dto.setIdDetalleCompra(detalleModel.getIdDetalleCompra());
        dto.setCantidad(detalleModel.getCantidad());
        // El precio de compra del detalle siempre viene del DetalleCompraModel mismo,
        // ya que registra el precio histórico de esa compra.
        dto.setPrecioCompra(detalleModel.getPrecioCompra());

        // Aseguramos que la compra no sea nula para obtener su ID
        if (detalleModel.getCompra() != null) {
            dto.setIdCompra(detalleModel.getCompra().getIdCompra());
        }

        // Mapeamos el Producto asociado al detalle
        if (detalleModel.getProducto() != null) {
            ProductoDTO productoDto = new ProductoDTO();
            productoDto.setId(detalleModel.getProducto().getId());
            productoDto.setNombre(detalleModel.getProducto().getNombre());
            productoDto.setDescripcion(detalleModel.getProducto().getDescripcion());
            productoDto.setStock(detalleModel.getProducto().getStock());
            // ERROR CORREGIDO: 'precioCompra' ya no existe en ProductoModel ni se mapea a ProductoDTO de esta forma.
            // La línea `productoDto.setPrecioCompra(detalleModel.getProducto().getPrecioCompra());` se ELIMINA.
            // Si necesitas el precio de compra histórico, ya lo tienes en `dto.setPrecioCompra(detalleModel.getPrecioCompra());`
            productoDto.setPrecioVenta(detalleModel.getProducto().getPrecioVenta()); // Asumiendo que PrecioVenta sigue en ProductoModel

            // ERROR CORREGIDO: 'getProveedor()' ya no existe directamente en ProductoModel.
            // Mapeamos la lista de proveedores asociados al producto, si es necesario para el DTO.
            if (detalleModel.getProducto().getProductoProveedores() != null) {
                productoDto.setProveedoresAsociados(
                        detalleModel.getProducto().getProductoProveedores().stream()
                                .map(ppModel -> new ProductoProveedorDTO(
                                        ppModel.getProveedor().getId(),
                                        ppModel.getProveedor().getNombre(),
                                        ppModel.getProducto().getId(), // Id del producto
                                        ppModel.getProducto().getNombre(), // Nombre del producto
                                        ppModel.getPrecio()
                                ))
                                .collect(Collectors.toList())
                );
            }
            dto.setProducto(productoDto);
        }
        return dto;
    }


    // --- Métodos de Lectura ---

    public List<DetalleCompraResponseDTO> obtenerTodosLosDetallesCompra() {
        List<DetalleCompraModel> detalles = detalleCompraRepository.findAll();
        List<DetalleCompraResponseDTO> detallesDTO = new ArrayList<>();
        for (DetalleCompraModel detalle : detalles) {
            detallesDTO.add(mapDetalleCompraModelToResponseDTO(detalle));
        }
        return detallesDTO;
    }

    public Optional<DetalleCompraResponseDTO> obtenerDetalleCompraPorId(Integer id) {
        Optional<DetalleCompraModel> detalleOptional = detalleCompraRepository.findById(id);
        return detalleOptional.map(this::mapDetalleCompraModelToResponseDTO);
    }

    // Método opcional: Obtener detalles de compra por ID de compra
    public List<DetalleCompraResponseDTO> obtenerDetallesPorIdCompra(Integer idCompra) {
        // Necesitarás un método en DetalleCompraRepository como:
        // List<DetalleCompraModel> findByCompra_IdCompra(Integer idCompra);
        // Asegúrate de haber añadido este método en tu DetalleCompraRepository si aún no lo has hecho.
        List<DetalleCompraModel> detalles = detalleCompraRepository.findByCompra_IdCompra(idCompra);
        List<DetalleCompraResponseDTO> detallesDTO = new ArrayList<>();
        for (DetalleCompraModel detalle : detalles) {
            detallesDTO.add(mapDetalleCompraModelToResponseDTO(detalle));
        }
        return detallesDTO;
    }
}