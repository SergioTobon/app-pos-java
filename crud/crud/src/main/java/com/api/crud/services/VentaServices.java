package com.api.crud.services;

import com.api.crud.dto.*;
import com.api.crud.models.*;
import com.api.crud.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VentaServices {

    @Autowired
    private VentaRepository ventaRepository;
    @Autowired
    private DetalleVentaRepository detalleVentaRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    @Transactional
    public VentaResponseDTO guardarVenta(VentaRequestDTO ventaRequestDTO) {
        // 1. Validar Usuario y Cliente (SIN CAMBIOS)
        if (ventaRequestDTO.getIdUsuario() == null) {
            throw new RuntimeException("El ID de Usuario es requerido para la venta.");
        }
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findById(ventaRequestDTO.getIdUsuario());
        if (usuarioOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + ventaRequestDTO.getIdUsuario());
        }
        UsuarioModel usuario = usuarioOptional.get();

        if (ventaRequestDTO.getIdCliente() == null) {
            throw new RuntimeException("El ID de Cliente es requerido para la venta.");
        }
        Optional<ClienteModel> clienteOptional = clienteRepository.findById(ventaRequestDTO.getIdCliente());
        if (clienteOptional.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado con ID: " + ventaRequestDTO.getIdCliente());
        }
        ClienteModel cliente = clienteOptional.get();

        // 2. Crear VentaModel (cabecera de la venta)
        VentaModel ventaModel = new VentaModel();
        ventaModel.setFecha(LocalDateTime.now());
        ventaModel.setUsuario(usuario);
        ventaModel.setCliente(cliente);
        ventaModel.setTotal(0.0);

        List<DetalleVentaModel> detallesVenta = new ArrayList<>();
        Double totalGeneralVenta = 0.0;

        // 3. Procesar los detalles de la venta (productos)
        if (ventaRequestDTO.getProductos() == null || ventaRequestDTO.getProductos().isEmpty()) {
            throw new RuntimeException("La lista de productos en la venta no puede estar vacía.");
        }

        for (DetalleVentaRequestDTO detalleDTO : ventaRequestDTO.getProductos()) {
            if (detalleDTO.getIdProducto() == null || detalleDTO.getCantidad() == null || detalleDTO.getCantidad() <= 0) {
                throw new RuntimeException("Los detalles del producto en la venta son inválidos (ID de producto, cantidad o cantidad <= 0).");
            }

            Optional<ProductoModel> productoOptional = productoRepository.findById(detalleDTO.getIdProducto());
            if (productoOptional.isEmpty()) {
                throw new RuntimeException("Producto no encontrado con ID: " + detalleDTO.getIdProducto());
            }
            ProductoModel producto = productoOptional.get();

            // Validar stock antes de vender
            if (producto.getStock() < detalleDTO.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre() + ". Stock disponible: " + producto.getStock() + ", cantidad solicitada: " + detalleDTO.getCantidad());
            }

            // --- Lógica de cálculo del precioUnitario ---
            Double precioUnitario;
            if (detalleDTO.getPorcentajeGanancia() != null) {
                if (producto.getPrecioCompra() == null) {
                    throw new RuntimeException("El producto " + producto.getNombre() + " no tiene un precio de compra definido para calcular la ganancia.");
                }
                precioUnitario = producto.getPrecioCompra() * (1 + (detalleDTO.getPorcentajeGanancia() / 100.0));
            } else {
                if (producto.getPrecioVenta() == null) {
                    throw new RuntimeException("El producto " + producto.getNombre() + " no tiene un precio de venta definido ni se proporcionó un porcentaje de ganancia.");
                }
                precioUnitario = producto.getPrecioVenta();
            }
            // --- FIN Lógica de cálculo del precioUnitario ---

            Double subtotalDetalle = detalleDTO.getCantidad() * precioUnitario;

            // Crear DetalleVentaModel
            DetalleVentaModel detalleVentaModel = new DetalleVentaModel();
            detalleVentaModel.setProducto(producto);
            detalleVentaModel.setCantidad(detalleDTO.getCantidad());
            detalleVentaModel.setPrecioUnitario(precioUnitario);
            detalleVentaModel.setSubtotal(subtotalDetalle);
            detalleVentaModel.setVenta(ventaModel); // Importante: Asociar el detalle a la venta

            detallesVenta.add(detalleVentaModel); // Añadir a la lista temporal
            totalGeneralVenta += subtotalDetalle;

            // Disminuir el stock del producto
            producto.setStock(producto.getStock() - detalleDTO.getCantidad());
            productoRepository.save(producto); // Guardar el producto con el stock actualizado
        }

        // --- ¡LA LÍNEA QUE FALTABA! ---
        // 4. Asignar la lista de detalles a la VentaModel
        ventaModel.setDetalles(detallesVenta); // <-- ¡AÑADE ESTA LÍNEA AQUÍ!
        // --- FIN DE LA LÍNEA AÑADIDA ---

        // 5. Asignar el total final a la venta
        ventaModel.setTotal(totalGeneralVenta);

        // 6. Guardar la Venta (Hibernate ahora guardará los detalles en cascada gracias a cascade = CascadeType.ALL)
        VentaModel ventaGuardada = ventaRepository.save(ventaModel);

        // 7. Mapear la VentaModel guardada a DTO de respuesta
        return mapVentaModelToResponseDTO(ventaGuardada);
    }

    // ... (El resto de tus métodos obtenerTodasLasVentas, obtenerVentaPorId, y los métodos de mapeo mapVentaModelToResponseDTO y mapDetalleVentaModelToResponseDTO están correctos para lo que necesitas)
    public List<VentaResponseDTO> obtenerTodasLasVentas() {
        List<VentaModel> ventas = ventaRepository.findAll();
        return ventas.stream()
                .map(this::mapVentaModelToResponseDTO)
                .collect(Collectors.toList());
    }

    public Optional<VentaResponseDTO> obtenerVentaPorId(Integer id) {
        Optional<VentaModel> ventaOptional = ventaRepository.findById(id);
        return ventaOptional.map(this::mapVentaModelToResponseDTO);
    }

    private VentaResponseDTO mapVentaModelToResponseDTO(VentaModel ventaModel) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(ventaModel.getIdVenta());
        dto.setFecha(ventaModel.getFecha());
        dto.setTotal(ventaModel.getTotal());

        if (ventaModel.getUsuario() != null) {
            UsuarioDTO usuarioDTO = new UsuarioDTO();
            usuarioDTO.setId(ventaModel.getUsuario().getIdUsuario());
            usuarioDTO.setDni(ventaModel.getUsuario().getDni());
            usuarioDTO.setNombre(ventaModel.getUsuario().getNombre());
            usuarioDTO.setApellido(ventaModel.getUsuario().getApellido());
            usuarioDTO.setContacto(ventaModel.getUsuario().getContacto());
            if (ventaModel.getUsuario().getRol() != null) {
                RolDTO rolDTO = new RolDTO();
                rolDTO.setId(ventaModel.getUsuario().getRol().getIdRoles());
                rolDTO.setNombre(ventaModel.getUsuario().getRol().getNombre());
                rolDTO.setDescripcion(ventaModel.getUsuario().getRol().getDescripcion());
                usuarioDTO.setRol(rolDTO);
            }
            dto.setUsuario(usuarioDTO);
        }

        if (ventaModel.getCliente() != null) {
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(ventaModel.getCliente().getIdCliente());
            clienteDTO.setDni(ventaModel.getCliente().getDni());
            clienteDTO.setNombre(ventaModel.getCliente().getNombre());
            clienteDTO.setContacto(ventaModel.getCliente().getContacto());
            dto.setCliente(clienteDTO);
        }

        if (ventaModel.getDetalles() != null && !ventaModel.getDetalles().isEmpty()) {
            List<DetalleVentaResponseDTO> detallesDTO = ventaModel.getDetalles().stream()
                    .map(this::mapDetalleVentaModelToResponseDTO)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }

        return dto;
    }

    private DetalleVentaResponseDTO mapDetalleVentaModelToResponseDTO(DetalleVentaModel detalleVentaModel) {
        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
        dto.setIdDetalleVenta(detalleVentaModel.getIdDetalleVenta());
        dto.setCantidad(detalleVentaModel.getCantidad());
        dto.setPrecioUnitario(detalleVentaModel.getPrecioUnitario());
        dto.setSubtotal(detalleVentaModel.getSubtotal());

        if (detalleVentaModel.getProducto() != null) {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(detalleVentaModel.getProducto().getId());
            productoDTO.setNombre(detalleVentaModel.getProducto().getNombre());
            productoDTO.setStock(detalleVentaModel.getProducto().getStock());
            productoDTO.setDescripcion(detalleVentaModel.getProducto().getDescripcion());
            productoDTO.setPrecioCompra(detalleVentaModel.getProducto().getPrecioCompra());
            productoDTO.setPrecioVenta(detalleVentaModel.getProducto().getPrecioVenta());

            if (detalleVentaModel.getProducto().getProveedor() != null) {
                productoDTO.setIdProveedor(detalleVentaModel.getProducto().getProveedor().getId());
                productoDTO.setNombreProveedor(detalleVentaModel.getProducto().getProveedor().getNombre());
            }
            dto.setProducto(productoDTO);
        }
        return dto;
    }
}