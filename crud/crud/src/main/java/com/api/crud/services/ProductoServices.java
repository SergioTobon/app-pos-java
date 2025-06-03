package com.api.crud.services;

import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProveedorModel;
import com.api.crud.repositories.ProductoRepository;
import com.api.crud.repositories.ProveedorRepository;
import com.api.crud.dto.ProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductoServices {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    @Autowired
    public ProductoServices(ProductoRepository productoRepository, ProveedorRepository proveedorRepository) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // --- Métodos de Mapeo (Helper Methods) ---

    // Mapea ProductoModel a ProductoDTO (para enviar al cliente)
    private ProductoDTO mapProductoModelToDTO(ProductoModel productoModel) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(productoModel.getId());
        dto.setNombre(productoModel.getNombre());
        dto.setStock(productoModel.getStock());
        dto.setDescripcion(productoModel.getDescripcion());
        dto.setPrecioCompra(productoModel.getPrecioCompra());
        // ¡CORRECCIÓN AQUÍ! Asigna el precioVenta real del modelo al DTO
        dto.setPrecioVenta(productoModel.getPrecioVenta());

        if (productoModel.getProveedor() != null) {
            dto.setIdProveedor(productoModel.getProveedor().getId());
            dto.setNombreProveedor(productoModel.getProveedor().getNombre());
        }
        return dto;
    }

    // Mapea ProductoDTO a ProductoModel (para guardar en la base de datos)
    private ProductoModel mapProductoDTOToModel(ProductoDTO productoDTO) {
        ProductoModel model = new ProductoModel();
        if (productoDTO.getId() != null) {
            model.setId(productoDTO.getId());
        }
        model.setNombre(productoDTO.getNombre());
        model.setStock(productoDTO.getStock());
        model.setDescripcion(productoDTO.getDescripcion());
        model.setPrecioCompra(productoDTO.getPrecioCompra());
        // ¡CORRECCIÓN AQUÍ! Asigna el precioVenta del DTO al Model
        model.setPrecioVenta(productoDTO.getPrecioVenta()); // <-- Línea agregada

        if (productoDTO.getIdProveedor() != null) {
            Optional<ProveedorModel> proveedorOptional = proveedorRepository.findById(productoDTO.getIdProveedor());
            if (proveedorOptional.isPresent()) {
                model.setProveedor(proveedorOptional.get());
            } else {
                System.out.println("Advertencia: Proveedor con ID " + productoDTO.getIdProveedor() + " no encontrado.");
                model.setProveedor(null);
            }
        }
        return model;
    }


    // --- Métodos CRUD ---

    public List<ProductoDTO> obtenerTodosLosProductos() {
        List<ProductoModel> productos = productoRepository.findAll();
        List<ProductoDTO> productosDTO = new ArrayList<>();
        for (ProductoModel producto : productos) {
            productosDTO.add(mapProductoModelToDTO(producto));
        }
        return productosDTO;
    }

    public Optional<ProductoDTO> obtenerProductoPorId(Integer id) {
        Optional<ProductoModel> productoOptional = productoRepository.findById(id);
        return productoOptional.map(this::mapProductoModelToDTO);
    }

    public ProductoDTO guardarProducto(ProductoDTO productoDTO) {
        ProductoModel productoModel = mapProductoDTOToModel(productoDTO);
        ProductoModel productoGuardado = productoRepository.save(productoModel);
        return mapProductoModelToDTO(productoGuardado);
    }

    public Optional<ProductoDTO> actualizarProducto(Integer id, ProductoDTO productoDTO) {
        Optional<ProductoModel> productoExistenteOptional = productoRepository.findById(id);

        if (productoExistenteOptional.isPresent()) {
            ProductoModel productoExistente = productoExistenteOptional.get();
            productoExistente.setNombre(productoDTO.getNombre());
            productoExistente.setStock(productoDTO.getStock());
            productoExistente.setDescripcion(productoDTO.getDescripcion());
            productoExistente.setPrecioCompra(productoDTO.getPrecioCompra());
            // ¡CORRECCIÓN AQUÍ! Actualiza el precioVenta del Model existente
            productoExistente.setPrecioVenta(productoDTO.getPrecioVenta()); // <-- Línea agregada

            if (productoDTO.getIdProveedor() != null) {
                Optional<ProveedorModel> nuevoProveedorOptional = proveedorRepository.findById(productoDTO.getIdProveedor());
                if (nuevoProveedorOptional.isPresent()) {
                    productoExistente.setProveedor(nuevoProveedorOptional.get());
                } else {
                    System.out.println("Advertencia: Nuevo Proveedor con ID " + productoDTO.getIdProveedor() + " no encontrado. El proveedor actual no será modificado.");
                }
            } else {
                // Si idProveedor es null en el DTO, puedes decidir si el proveedor se desvincula o se mantiene
            }

            ProductoModel productoActualizado = productoRepository.save(productoExistente);
            return Optional.of(mapProductoModelToDTO(productoActualizado));
        } else {
            return Optional.empty();
        }
    }

    public boolean eliminarProducto(Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<ProductoDTO> buscarPorNombre(String nombre) {
        List<ProductoModel> productos = productoRepository.findByNombreContainingIgnoreCase(nombre);
        List<ProductoDTO> productosDTO = new ArrayList<>();
        for (ProductoModel producto : productos) {
            productosDTO.add(mapProductoModelToDTO(producto));
        }
        return productosDTO;
    }
}