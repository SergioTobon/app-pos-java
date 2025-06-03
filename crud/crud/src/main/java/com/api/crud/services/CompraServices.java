package com.api.crud.services;

import com.api.crud.models.CompraModel;
import com.api.crud.models.DetalleCompraModel;
import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProveedorModel;
import com.api.crud.repositories.CompraRepository;
import com.api.crud.repositories.ProductoRepository;
import com.api.crud.repositories.ProveedorRepository;
import com.api.crud.dto.CompraRequestDTO;
import com.api.crud.dto.CompraResponseDTO;
import com.api.crud.dto.DetalleCompraRequestDTO;
import com.api.crud.dto.DetalleCompraResponseDTO;
import com.api.crud.dto.ProductoDTO;
import com.api.crud.dto.ProveedorDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompraServices {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;

    @Autowired
    public CompraServices(CompraRepository compraRepository,
                          ProductoRepository productoRepository,
                          ProveedorRepository proveedorRepository) {
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
    }

    // --- Métodos de Mapeo (Helper Methods) para Response DTOs ---

    private DetalleCompraResponseDTO mapDetalleCompraModelToResponseDTO(DetalleCompraModel detalleModel) {
        DetalleCompraResponseDTO dto = new DetalleCompraResponseDTO();
        dto.setIdDetalleCompra(detalleModel.getIdDetalleCompra());
        dto.setCantidad(detalleModel.getCantidad());
        dto.setPrecioCompra(detalleModel.getPrecioCompra());

        if (detalleModel.getProducto() != null) {
            ProductoDTO productoDto = new ProductoDTO();
            productoDto.setId(detalleModel.getProducto().getId());
            productoDto.setNombre(detalleModel.getProducto().getNombre());
            productoDto.setDescripcion(detalleModel.getProducto().getDescripcion());
            productoDto.setStock(detalleModel.getProducto().getStock()); // Stock actual del producto (después de la compra)
            productoDto.setPrecioCompra(detalleModel.getProducto().getPrecioCompra());
            productoDto.setPrecioVenta(null); // O el valor que corresponda, si existe en ProductoModel

            if (detalleModel.getProducto().getProveedor() != null) {
                productoDto.setIdProveedor(detalleModel.getProducto().getProveedor().getId());
                productoDto.setNombreProveedor(detalleModel.getProducto().getProveedor().getNombre());
            }
            dto.setProducto(productoDto);
        }
        return dto;
    }

    private ProveedorDTO mapProveedorModelToDTO(ProveedorModel proveedorModel) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedorModel.getId());
        dto.setNombre(proveedorModel.getNombre());
        dto.setNit(proveedorModel.getNit());
        dto.setContacto(proveedorModel.getContacto());
        dto.setDireccion(proveedorModel.getDireccion());
        dto.setEmail(proveedorModel.getEmail());
        dto.setTelefono(proveedorModel.getTelefono());
        return dto;
    }

    private CompraResponseDTO mapCompraModelToResponseDTO(CompraModel compraModel) {
        CompraResponseDTO dto = new CompraResponseDTO();
        dto.setIdCompra(compraModel.getIdCompra());
        dto.setTotal(compraModel.getTotal());
        dto.setFecha(compraModel.getFecha());

        if (compraModel.getProveedor() != null) {
            dto.setProveedor(mapProveedorModelToDTO(compraModel.getProveedor()));
        }

        List<DetalleCompraResponseDTO> detallesDTO = new ArrayList<>();
        if (compraModel.getDetalles() != null) {
            for (DetalleCompraModel detalle : compraModel.getDetalles()) {
                detallesDTO.add(mapDetalleCompraModelToResponseDTO(detalle));
            }
        }
        dto.setDetalles(detallesDTO);
        return dto;
    }

    // --- Métodos CRUD ---

    public List<CompraResponseDTO> obtenerTodasLasCompras() {
        List<CompraModel> compras = compraRepository.findAll();
        List<CompraResponseDTO> comprasDTO = new ArrayList<>();
        for (CompraModel compra : compras) {
            comprasDTO.add(mapCompraModelToResponseDTO(compra));
        }
        return comprasDTO;
    }

    public Optional<CompraResponseDTO> obtenerCompraPorId(Integer id) {
        Optional<CompraModel> compraOptional = compraRepository.findById(id);
        return compraOptional.map(this::mapCompraModelToResponseDTO);
    }

    @Transactional
    public CompraResponseDTO guardarCompra(CompraRequestDTO compraRequestDTO) {
        CompraModel compraModel = new CompraModel();
        compraModel.setFecha(LocalDateTime.now());
        double totalCompra = 0.0;

        // 1. Buscar y asignar el Proveedor
        if (compraRequestDTO.getIdProveedor() == null) {
            System.out.println("Error: ID de Proveedor es nulo en la solicitud de compra.");
            // Mejor lanzar una excepción Http status 400 Bad Request
            throw new IllegalArgumentException("ID de Proveedor es obligatorio.");
        }
        Optional<ProveedorModel> proveedorOptional = proveedorRepository.findById(compraRequestDTO.getIdProveedor());
        if (proveedorOptional.isEmpty()) {
            System.out.println("Error: Proveedor con ID " + compraRequestDTO.getIdProveedor() + " no encontrado.");
            // Lanzar una excepción específica que se mapee a 404 Not Found
            throw new RuntimeException("Proveedor no encontrado con ID: " + compraRequestDTO.getIdProveedor());
        }
        compraModel.setProveedor(proveedorOptional.get());

        // 2. Procesar los detalles de la compra (productos)
        List<DetalleCompraModel> detallesCompra = new ArrayList<>();
        if (compraRequestDTO.getProductos() == null || compraRequestDTO.getProductos().isEmpty()) {
            System.out.println("Error: La lista de productos en la compra no puede estar vacía.");
            throw new IllegalArgumentException("La compra debe contener al menos un producto.");
        }

        for (DetalleCompraRequestDTO detalleDTO : compraRequestDTO.getProductos()) {
            Optional<ProductoModel> productoOptional = productoRepository.findById(detalleDTO.getIdProducto());
            if (productoOptional.isEmpty()) {
                System.out.println("Error: Producto con ID " + detalleDTO.getIdProducto() + " no encontrado.");
                throw new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto());
            }

            ProductoModel producto = productoOptional.get();

            // ***** CORRECCIÓN APLICADA AQUÍ *****
            // Se elimina la validación de stock, ya que estás COMPRANDO (añadiendo stock)
            // if (producto.getStock() < detalleDTO.getCantidad()) {
            //     System.out.println("Error: Stock insuficiente para el producto " + producto.getNombre() + ". Stock disponible: " + producto.getStock() + ", cantidad solicitada: " + detalleDTO.getCantidad());
            //     throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            // }
            // ***** FIN CORRECCIÓN *****

            // Validar que la cantidad comprada sea válida (mayor a 0)
            if (detalleDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad comprada para el producto " + producto.getNombre() + " debe ser mayor que cero.");
            }

            // Crear DetalleCompraModel
            DetalleCompraModel detalleCompraModel = new DetalleCompraModel();
            detalleCompraModel.setProducto(producto);
            detalleCompraModel.setCantidad(detalleDTO.getCantidad());
            detalleCompraModel.setPrecioCompra(producto.getPrecioCompra()); // Precio del producto al momento de la compra
            detalleCompraModel.setCompra(compraModel); // Establecer la referencia a la compra principal

            detallesCompra.add(detalleCompraModel);
            totalCompra += (detalleDTO.getCantidad() * producto.getPrecioCompra());

            // Actualizar el stock del producto: SUMAR al stock existente
            producto.setStock(producto.getStock() + detalleDTO.getCantidad());
            productoRepository.save(producto); // Guarda el producto con el stock actualizado
        }

        compraModel.setDetalles(detallesCompra);
        compraModel.setTotal(totalCompra);

        // 3. Guardar la Compra (los detalles se guardan en cascada gracias a la configuración en CompraModel)
        CompraModel compraGuardada = compraRepository.save(compraModel);

        // 4. Mapear a CompraResponseDTO y devolver
        return mapCompraModelToResponseDTO(compraGuardada);
    }
}