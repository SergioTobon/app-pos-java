package com.api.crud.services;

import com.api.crud.models.DetalleCompraModel;
import com.api.crud.repositories.DetalleCompraRepository;
import com.api.crud.dto.DetalleCompraResponseDTO;
import com.api.crud.dto.ProductoDTO; // Para el DTO del producto anidado

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
            productoDto.setPrecioCompra(detalleModel.getProducto().getPrecioCompra());
            // No asignamos precioVenta si no existe en el modelo o no se calcula aquí.
            productoDto.setPrecioVenta(null);

            // También podemos incluir el proveedor del producto si es relevante
            if (detalleModel.getProducto().getProveedor() != null) {
                productoDto.setIdProveedor(detalleModel.getProducto().getProveedor().getId());
                productoDto.setNombreProveedor(detalleModel.getProducto().getProveedor().getNombre());
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
        // Por ahora, asumiré que tienes un método de búsqueda. Si no lo tienes, deberás agregarlo.
        List<DetalleCompraModel> detalles = detalleCompraRepository.findByCompra_IdCompra(idCompra); // Asegúrate de añadir este método en tu DetalleCompraRepository
        List<DetalleCompraResponseDTO> detallesDTO = new ArrayList<>();
        for (DetalleCompraModel detalle : detalles) {
            detallesDTO.add(mapDetalleCompraModelToResponseDTO(detalle));
        }
        return detallesDTO;
    }
}