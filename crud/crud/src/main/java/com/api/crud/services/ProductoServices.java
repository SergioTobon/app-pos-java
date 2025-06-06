package com.api.crud.services;

import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.models.ProductoProveedorId;
import com.api.crud.models.ProveedorModel;
import com.api.crud.repositories.ProductoRepository;
import com.api.crud.repositories.ProveedorRepository;
import com.api.crud.repositories.ProductoProveedorRepository;
import com.api.crud.dto.ProductoDTO;
import com.api.crud.dto.ProductoProveedorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductoServices {

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoProveedorRepository productoProveedorRepository;

    @Autowired
    public ProductoServices(ProductoRepository productoRepository, ProveedorRepository proveedorRepository,
                            ProductoProveedorRepository productoProveedorRepository) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoProveedorRepository = productoProveedorRepository;
    }

    // --- MÉTODO DE MAPEO DESDE MODELO A DTO (Ahora público para que otros servicios puedan usarlo) ---
    public ProductoDTO mapProductoModelToDTO(ProductoModel productoModel) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(productoModel.getId());
        dto.setNombre(productoModel.getNombre());
        dto.setStock(productoModel.getStock());
        dto.setDescripcion(productoModel.getDescripcion());
        dto.setPrecioVenta(productoModel.getPrecioVenta());
        dto.setPrecioCompra(productoModel.getPrecioCompra());
        // AÑADIR: Mapear el porcentaje de ganancia
        dto.setPorcentajeGanancia(productoModel.getPorcentajeGanancia()); // Asegúrate de que ProductoDTO tenga este campo

        // Mapea la lista de proveedores asociados
        if (productoModel.getProductoProveedores() != null && !productoModel.getProductoProveedores().isEmpty()) {
            List<ProductoProveedorDTO> proveedoresAsociados = productoModel.getProductoProveedores().stream()
                    .map(ppModel -> {
                        ProductoProveedorDTO ppDto = new ProductoProveedorDTO();
                        if (ppModel.getProveedor() != null) {
                            ppDto.setIdProveedor(ppModel.getProveedor().getId());
                            ppDto.setNombreProveedor(ppModel.getProveedor().getNombre());
                        }
                        if (ppModel.getProducto() != null) {
                            ppDto.setIdProducto(ppModel.getProducto().getId());
                            ppDto.setNombreProducto(ppModel.getProducto().getNombre());
                        }
                        ppDto.setPrecioCompraEspecifico(ppModel.getPrecio());
                        return ppDto;
                    })
                    .collect(Collectors.toList());
            dto.setProveedoresAsociados(proveedoresAsociados);
        } else {
            dto.setProveedoresAsociados(new ArrayList<>());
        }
        return dto;
    }

    private ProductoModel mapProductoDTOToModel(ProductoDTO productoDTO) {
        ProductoModel model = new ProductoModel();
        if (productoDTO.getId() != null) {
            model.setId(productoDTO.getId());
        }
        model.setNombre(productoDTO.getNombre());
        model.setStock(productoDTO.getStock());
        model.setDescripcion(productoDTO.getDescripcion());
        model.setPrecioVenta(productoDTO.getPrecioVenta());
        model.setPrecioCompra(productoDTO.getPrecioCompra());
        // AÑADIR: Mapear el porcentaje de ganancia
        model.setPorcentajeGanancia(productoDTO.getPorcentajeGanancia()); // Asegúrate de que ProductoModel tenga este campo
        return model;
    }

    // --- Métodos de CRUD existentes ---
    public List<ProductoDTO> obtenerTodosLosProductos() {
        List<ProductoModel> productos = productoRepository.findAll();
        return productos.stream()
                .map(this::mapProductoModelToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ProductoDTO> obtenerProductoPorId(Integer id) {
        Optional<ProductoModel> productoOptional = productoRepository.findById(id);
        return productoOptional.map(this::mapProductoModelToDTO);
    }

    @Transactional
    public ProductoDTO guardarProducto(ProductoDTO productoDTO) {
        ProductoModel productoModel;
        boolean isNewProduct = productoDTO.getId() == null;

        if (isNewProduct) {
            productoModel = new ProductoModel();
            productoModel.setStock(0);
            productoModel.setPrecioCompra(null);
            // Si es un producto nuevo, establece el porcentaje de ganancia por defecto si viene en el DTO
            if (productoDTO.getPorcentajeGanancia() != null) {
                productoModel.setPorcentajeGanancia(productoDTO.getPorcentajeGanancia());
            } else {
                productoModel.setPorcentajeGanancia(20.0); // O tu valor por defecto
            }
        } else {
            productoModel = productoRepository.findById(productoDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + productoDTO.getId() + " no encontrado para actualizar."));
            if (productoDTO.getStock() != null) {
                productoModel.setStock(productoDTO.getStock());
            }
            if (productoDTO.getPrecioCompra() != null) {
                productoModel.setPrecioCompra(productoDTO.getPrecioCompra());
            }
            // Actualiza el porcentaje de ganancia si viene en el DTO
            if (productoDTO.getPorcentajeGanancia() != null) {
                productoModel.setPorcentajeGanancia(productoDTO.getPorcentajeGanancia());
            }
        }

        productoModel.setNombre(productoDTO.getNombre());
        productoModel.setDescripcion(productoDTO.getDescripcion());
        // Si el precio de venta viene en el DTO, úsalo, de lo contrario, se calculará al registrar compra
        if (productoDTO.getPrecioVenta() != null) {
            productoModel.setPrecioVenta(productoDTO.getPrecioVenta());
        } else {
            // Si no viene precioVenta, pero hay precioCompra y porcentajeGanancia, calcula
            if (productoModel.getPrecioCompra() != null && productoModel.getPorcentajeGanancia() != null && productoModel.getPorcentajeGanancia() > 0) {
                double precioVentaCalculado = productoModel.getPrecioCompra() * (1 + (productoModel.getPorcentajeGanancia() / 100.0));
                productoModel.setPrecioVenta(precioVentaCalculado);
            }
        }


        if (!isNewProduct && productoDTO.getProveedoresAsociados() != null) {
            if (productoModel.getProductoProveedores() == null) {
                productoModel.setProductoProveedores(new HashSet<>());
            }

            Set<Integer> incomingProveedorIds = productoDTO.getProveedoresAsociados().stream()
                    .map(ProductoProveedorDTO::getIdProveedor)
                    .collect(Collectors.toSet());

            productoModel.getProductoProveedores().removeIf(existingPp ->
                    !incomingProveedorIds.contains(existingPp.getProveedor().getId())
            );

            for (ProductoProveedorDTO incomingPpDto : productoDTO.getProveedoresAsociados()) {
                ProveedorModel proveedor = proveedorRepository.findById(incomingPpDto.getIdProveedor())
                        .orElseThrow(() -> new RuntimeException("Proveedor con ID " + incomingPpDto.getIdProveedor() + " no encontrado para el producto " + productoDTO.getNombre()));

                Optional<ProductoProveedorModel> existingRelOptional = productoModel.getProductoProveedores().stream()
                        .filter(pp -> pp.getProveedor().getId().equals(incomingPpDto.getIdProveedor()))
                        .findFirst();

                if (existingRelOptional.isPresent()) {
                    existingRelOptional.get().setPrecio(incomingPpDto.getPrecioCompraEspecifico());
                } else {
                    ProductoProveedorModel newPp = new ProductoProveedorModel();
                    newPp.setProducto(productoModel);
                    newPp.setProveedor(proveedor);
                    newPp.setPrecio(incomingPpDto.getPrecioCompraEspecifico());
                    productoModel.getProductoProveedores().add(newPp);
                }
            }
        } else if (isNewProduct && productoDTO.getProveedoresAsociados() != null && !productoDTO.getProveedoresAsociados().isEmpty()){
            System.out.println("Advertencia: Se intentaron asociar proveedores al crear un nuevo producto. Esto no se guardará en esta operación.");
        }

        ProductoModel productoGuardado = productoRepository.save(productoModel);
        return mapProductoModelToDTO(productoGuardado);
    }

    // En ProductoServices.java

// ... (tus imports y otros métodos) ...

    @Transactional
// CAMBIO CLAVE: Ahora devuelve ProductoDTO y recibe ProductoDTO
    public ProductoDTO actualizarProducto(Integer id, ProductoDTO productoDTO) {
        ProductoModel existingProduct = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto con ID " + id + " no encontrado para actualizar."));

        // Actualiza solo los campos que vienen en el DTO
        if (productoDTO.getNombre() != null) {
            existingProduct.setNombre(productoDTO.getNombre());
        }
        if (productoDTO.getDescripcion() != null) {
            existingProduct.setDescripcion(productoDTO.getDescripcion());
        }
        // Puedes decidir si permites actualizar el stock desde PUT o solo vía compras/ventas
        if (productoDTO.getStock() != null) {
            existingProduct.setStock(productoDTO.getStock());
        }
        if (productoDTO.getPrecioCompra() != null) {
            existingProduct.setPrecioCompra(productoDTO.getPrecioCompra());
        }
        if (productoDTO.getPorcentajeGanancia() != null) {
            existingProduct.setPorcentajeGanancia(productoDTO.getPorcentajeGanancia());
        }
        // No permitas que el DTO sobrescriba directamente el precioVenta si se calcula
        // Si quisieras que el DTO pudiera establecer el precioVenta directamente, tendrías que añadir otra lógica

        // Lógica para recalcular el PrecioVenta si hay cambios en PrecioCompra o PorcentajeGanancia
        // Esto usará los valores *actuales* de existingProduct (que ya fueron actualizados si vinieron en el DTO)
        if (existingProduct.getPrecioCompra() != null && existingProduct.getPorcentajeGanancia() != null && existingProduct.getPorcentajeGanancia() > 0) {
            double precioVentaCalculado = existingProduct.getPrecioCompra() * (1 + (existingProduct.getPorcentajeGanancia() / 100.0));
            existingProduct.setPrecioVenta(precioVentaCalculado);
        } else {
            // Si no se pudo calcular (por ejemplo, falta precioCompra o porcentajeGanancia)
            // Y si el DTO de entrada traía un precioVenta, puedes usarlo.
            if (productoDTO.getPrecioVenta() != null) {
                existingProduct.setPrecioVenta(productoDTO.getPrecioVenta());
            } else {
                // Opcional: Si no se puede calcular y no viene en el DTO, podrías establecerlo a null o 0.0
                existingProduct.setPrecioVenta(null);
            }
        }

        // Manejo de proveedores asociados (si ProductoDTO lo permite y viene en la solicitud PUT)
        // Es el mismo bloque de lógica que ya tenías y es robusto.
        if (productoDTO.getProveedoresAsociados() != null) {
            if (existingProduct.getProductoProveedores() == null) {
                existingProduct.setProductoProveedores(new HashSet<>());
            }

            Set<Integer> incomingProveedorIds = productoDTO.getProveedoresAsociados().stream()
                    .map(ProductoProveedorDTO::getIdProveedor)
                    .collect(Collectors.toSet());

            existingProduct.getProductoProveedores().removeIf(existingPp ->
                    !incomingProveedorIds.contains(existingPp.getProveedor().getId())
            );

            for (ProductoProveedorDTO incomingPpDto : productoDTO.getProveedoresAsociados()) {
                ProveedorModel proveedor = proveedorRepository.findById(incomingPpDto.getIdProveedor())
                        .orElseThrow(() -> new RuntimeException("Proveedor con ID " + incomingPpDto.getIdProveedor() + " no encontrado para el producto " + existingProduct.getNombre()));

                Optional<ProductoProveedorModel> existingRelOptional = existingProduct.getProductoProveedores().stream()
                        .filter(pp -> pp.getProveedor().getId().equals(incomingPpDto.getIdProveedor()))
                        .findFirst();

                if (existingRelOptional.isPresent()) {
                    existingRelOptional.get().setPrecio(incomingPpDto.getPrecioCompraEspecifico());
                } else {
                    ProductoProveedorModel newPp = new ProductoProveedorModel();
                    newPp.setProducto(existingProduct);
                    newPp.setProveedor(proveedor);
                    newPp.setPrecio(incomingPpDto.getPrecioCompraEspecifico());
                    existingProduct.getProductoProveedores().add(newPp);
                }
            }
        }


        ProductoModel productoGuardado = productoRepository.save(existingProduct);
        return mapProductoModelToDTO(productoGuardado); // Mapea el modelo guardado de nuevo a DTO
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
        return productos.stream()
                .map(this::mapProductoModelToDTO)
                .collect(Collectors.toList());
    }

    public Optional<Double> obtenerPrecioCompraEspecifico(Integer idProducto, Integer idProveedor) {
        ProductoProveedorId id = new ProductoProveedorId(idProducto, idProveedor);
        return productoProveedorRepository.findById(id)
                .map(ProductoProveedorModel::getPrecio);
    }

    public List<ProductoProveedorDTO> obtenerProveedoresDeProducto(Integer idProducto) {
        return productoProveedorRepository.findById_IdProducto(idProducto).stream()
                .map(ppModel -> {
                    ProductoProveedorDTO ppDto = new ProductoProveedorDTO();
                    if(ppModel.getProveedor() != null) {
                        ppDto.setIdProveedor(ppModel.getProveedor().getId());
                        ppDto.setNombreProveedor(ppModel.getProveedor().getNombre());
                    }
                    if(ppModel.getProducto() != null){
                        ppDto.setIdProducto(ppModel.getProducto().getId());
                        ppDto.setNombreProducto(ppModel.getProducto().getNombre());
                    }
                    ppDto.setPrecioCompraEspecifico(ppModel.getPrecio());
                    return ppDto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Método para registrar una compra de UN producto, actualizando su stock, precio de compra general
     * y la relación Producto-Proveedor con su precio específico.
     * Es llamado por CompraServices para cada detalle de compra.
     *
     * @param idProducto El ID del producto al que se le registrará la compra.
     * @param idProveedor El ID del proveedor al que se le compró el producto.
     * @param cantidadComprada La cantidad de unidades compradas.
     * @param precioCompraUnitario El precio unitario de compra de esta transacción.
     * @return El ProductoDTO actualizado.
     * @throws RuntimeException si el producto o proveedor no se encuentran.
     */
    @Transactional
    public ProductoDTO registrarCompraProducto(Integer idProducto, Integer idProveedor, Integer cantidadComprada, Double precioCompraUnitario) {
        ProductoModel producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new RuntimeException("Producto con ID " + idProducto + " no encontrado."));

        ProveedorModel proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new RuntimeException("Proveedor con ID " + idProveedor + " no encontrado."));

        // Actualizar el stock del producto
        if (producto.getStock() == null) {
            producto.setStock(0); // Inicializar si es nulo
        }
        producto.setStock(producto.getStock() + cantidadComprada);

        // Actualizar el precio de compra general del producto
        producto.setPrecioCompra(precioCompraUnitario);

        // *** CAMBIO CLAVE AÑADIDO AQUÍ: CALCULAR Y ESTABLECER EL PRECIO DE VENTA ***
        // Asegúrate de que ProductoModel tenga el campo 'porcentajeGanancia'.
        // Si porcentajeGanancia es null o no es válido, PrecioVenta podría ser null.
        if (producto.getPorcentajeGanancia() != null && producto.getPorcentajeGanancia() > 0) {
            double precioVentaCalculado = precioCompraUnitario * (1 + (producto.getPorcentajeGanancia() / 100.0));
            producto.setPrecioVenta(precioVentaCalculado);
        } else {
            // Opcional: Define un comportamiento si no hay porcentaje de ganancia (ej. mantener null, 0, o un valor por defecto)
            producto.setPrecioVenta(null); // O deja el valor existente si no quieres sobreescribir.
        }


        // Buscar o crear la relación Producto-Proveedor
        ProductoProveedorId ppId = new ProductoProveedorId(idProducto, idProveedor);
        Optional<ProductoProveedorModel> productoProveedorOptional = productoProveedorRepository.findById(ppId);

        ProductoProveedorModel productoProveedor;
        if (productoProveedorOptional.isPresent()) {
            productoProveedor = productoProveedorOptional.get();
            // Actualizar el precio específico del proveedor para este producto
            productoProveedor.setPrecio(precioCompraUnitario);
        } else {
            // Crear nueva relación si no existe
            productoProveedor = new ProductoProveedorModel();
            productoProveedor.setProducto(producto);
            productoProveedor.setProveedor(proveedor);
            productoProveedor.setPrecio(precioCompraUnitario);
            // Asegúrate de añadir la nueva relación a la colección del producto para que se guarde con CascadeType.ALL
            // Esto es importante si ProductoModel maneja la relación @OneToMany o @ManyToMany
            if (producto.getProductoProveedores() == null) {
                producto.setProductoProveedores(new HashSet<>());
            }
            producto.getProductoProveedores().add(productoProveedor);
        }

        // Guardar el producto (esto también guardará/actualizará la relación ProductoProveedor gracias a CascadeType.ALL)
        ProductoModel productoActualizado = productoRepository.save(producto);
        return mapProductoModelToDTO(productoActualizado);
    }
}