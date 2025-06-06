package com.api.crud.services;

import com.api.crud.models.CompraModel;
import com.api.crud.models.DetalleCompraModel;
import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProveedorModel;
import com.api.crud.models.ProductoProveedorModel; // Se puede quitar si no se usa directamente
import com.api.crud.repositories.CompraRepository;
import com.api.crud.repositories.ProductoRepository;
import com.api.crud.repositories.ProveedorRepository;
import com.api.crud.repositories.ProductoProveedorRepository; // Se puede quitar si no se usa directamente

import com.api.crud.dto.CompraRequestDTO;
import com.api.crud.dto.CompraResponseDTO;
import com.api.crud.dto.DetalleCompraRequestDTO;
import com.api.crud.dto.DetalleCompraResponseDTO;
import com.api.crud.dto.ProductoDTO;
import com.api.crud.dto.ProveedorDTO;
import com.api.crud.dto.ProductoProveedorDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompraServices {

    private final CompraRepository compraRepository;
    private final ProductoRepository productoRepository; // Necesario para obtener el producto fresco para DetalleCompraModel
    private final ProveedorRepository proveedorRepository;
    // private final ProductoProveedorRepository productoProveedorRepository; // <-- Puedes quitar esta línea si no la usas directamente
    private final ProductoServices productoServices; // <-- ¡Inyección de ProductoServices!

    @Autowired
    public CompraServices(CompraRepository compraRepository,
                          ProductoRepository productoRepository,
                          ProveedorRepository proveedorRepository,
                          // ProductoProveedorRepository productoProveedorRepository, // <-- Si quitas la línea de arriba, quita este parámetro
                          ProductoServices productoServices) { // <-- ¡Añade este parámetro!
        this.compraRepository = compraRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        // this.productoProveedorRepository = productoProveedorRepository; // <-- Si quitas la línea de arriba, quita esta asignación
        this.productoServices = productoServices; // <-- ¡Asignación!
    }

    // --- Métodos de Mapeo (Helper Methods) para Response DTOs ---

    private DetalleCompraResponseDTO mapDetalleCompraModelToResponseDTO(DetalleCompraModel detalleModel) {
        DetalleCompraResponseDTO dto = new DetalleCompraResponseDTO();
        dto.setIdDetalleCompra(detalleModel.getIdDetalleCompra());
        dto.setCantidad(detalleModel.getCantidad());
        dto.setPrecioCompra(detalleModel.getPrecioCompra());

        if (detalleModel.getProducto() != null) {
            // --- CAMBIO CLAVE AQUÍ: OBTENER EL PRODUCTODTO COMPLETO Y ACTUALIZADO ---
            // Usamos productoServices.mapProductoModelToDTO para asegurar que el ProductoDTO
            // contenga el stock actualizado, precio de compra general y la colección de proveedores asociados.
            // Primero, aseguramos que obtenemos la versión más reciente del ProductoModel.
            ProductoModel productoActualizadoModel = productoRepository.findById(detalleModel.getProducto().getId())
                    .orElseThrow(() -> new RuntimeException("Producto asociado a detalle de compra no encontrado: " + detalleModel.getProducto().getId()));

            dto.setProducto(productoServices.mapProductoModelToDTO(productoActualizadoModel));
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
        dto.setIdCompra(compraModel.getIdCompra()); // Asumiendo que el ID en CompraModel es 'id'
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
        return compras.stream()
                .map(this::mapCompraModelToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<CompraResponseDTO> obtenerCompraPorId(Integer id) {
        Optional<CompraModel> compraOptional = compraRepository.findById(id);
        return compraOptional.map(this::mapCompraModelToResponseDTO);
    }

    @Transactional
    public CompraResponseDTO guardarCompra(CompraRequestDTO compraRequestDTO) {
        // 1. Validar y obtener el Proveedor
        if (compraRequestDTO.getIdProveedor() == null) {
            throw new IllegalArgumentException("El ID del proveedor es obligatorio para registrar la compra.");
        }
        ProveedorModel proveedor = proveedorRepository.findById(compraRequestDTO.getIdProveedor())
                .orElseThrow(() -> new RuntimeException("No se pudo encontrar el proveedor con ID: " + compraRequestDTO.getIdProveedor() + "."));

        // 2. Validar productos en la solicitud
        if (compraRequestDTO.getProductos() == null || compraRequestDTO.getProductos().isEmpty()) {
            throw new IllegalArgumentException("La compra debe contener al menos un producto.");
        }

        // 3. Crear la entidad CompraModel
        CompraModel compraModel = new CompraModel();
        compraModel.setFecha(LocalDateTime.now());
        compraModel.setProveedor(proveedor);

        double totalCompra = 0.0;
        List<DetalleCompraModel> detallesCompra = new ArrayList<>();

        // 4. Procesar cada detalle de producto
        for (DetalleCompraRequestDTO detalleRequestDTO : compraRequestDTO.getProductos()) {
            // Validaciones de cantidad y precio para el detalle
            if (detalleRequestDTO.getCantidad() == null || detalleRequestDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad comprada para el producto con ID '" + detalleRequestDTO.getIdProducto() + "' debe ser un valor positivo.");
            }
            if (detalleRequestDTO.getPrecioCompra() == null || detalleRequestDTO.getPrecioCompra() < 0) {
                throw new IllegalArgumentException("El precio unitario de compra para el producto con ID '" + detalleRequestDTO.getIdProducto() + "' es inválido o negativo.");
            }

            // --- CAMBIO CLAVE AQUÍ: DELEGAR ACTUALIZACIÓN DEL PRODUCTO ---
            // Llama a ProductoServices para actualizar el stock, el precio de compra general del producto
            // y la relación Producto-Proveedor. Este método ya persiste los cambios al ProductoModel.
            productoServices.registrarCompraProducto(
                    detalleRequestDTO.getIdProducto(),
                    compraRequestDTO.getIdProveedor(), // El proveedor de la orden global
                    detalleRequestDTO.getCantidad(),
                    detalleRequestDTO.getPrecioCompra()
            );

            // Obtener el ProductoModel actualizado desde el repositorio para asociarlo al DetalleCompraModel
            // Esto es crucial para que el DetalleCompraModel tenga la referencia correcta y actualizada del Producto.
            ProductoModel productoActualizadoParaDetalle = productoRepository.findById(detalleRequestDTO.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado después de actualización en CompraServices para detalle: " + detalleRequestDTO.getIdProducto()));


            // Crear y configurar DetalleCompraModel
            DetalleCompraModel detalleCompraModel = new DetalleCompraModel();
            detalleCompraModel.setProducto(productoActualizadoParaDetalle); // Asocia con el producto actualizado
            detalleCompraModel.setCantidad(detalleRequestDTO.getCantidad());
            detalleCompraModel.setPrecioCompra(detalleRequestDTO.getPrecioCompra()); // Precio de compra de ESTE detalle
            detalleCompraModel.setCompra(compraModel); // Asocia con la compra principal (esto es bidireccional si CompraModel tiene el cascade)

            detallesCompra.add(detalleCompraModel);

            // Sumar al total de la compra
            totalCompra += (detalleRequestDTO.getCantidad() * detalleRequestDTO.getPrecioCompra());
        }

        compraModel.setDetalles(detallesCompra); // Establece los detalles en la compra principal
        compraModel.setTotal(totalCompra);

        // 5. Guardar la Compra (los detalles se guardarán en cascada si CompraModel está configurado así)
        CompraModel compraGuardada = compraRepository.save(compraModel);

        // 6. Mapear la CompraModel guardada a CompraResponseDTO y devolver
        return mapCompraModelToResponseDTO(compraGuardada);
    }
}