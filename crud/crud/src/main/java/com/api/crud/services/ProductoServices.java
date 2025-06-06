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
    // Este método es crucial para que los ProductoDTO devueltos (incluso dentro de CompraResponseDTO)
    // contengan la información completa y actualizada, incluyendo los proveedores asociados.
    public ProductoDTO mapProductoModelToDTO(ProductoModel productoModel) { // <-- CAMBIO: De private a public
        ProductoDTO dto = new ProductoDTO();
        dto.setId(productoModel.getId());
        dto.setNombre(productoModel.getNombre());
        dto.setStock(productoModel.getStock());
        dto.setDescripcion(productoModel.getDescripcion());
        dto.setPrecioVenta(productoModel.getPrecioVenta());
        dto.setPrecioCompra(productoModel.getPrecioCompra()); // Mapea el precioCompra general del ProductoModel al DTO

        // Mapea la lista de proveedores asociados
        if (productoModel.getProductoProveedores() != null && !productoModel.getProductoProveedores().isEmpty()) {
            List<ProductoProveedorDTO> proveedoresAsociados = productoModel.getProductoProveedores().stream()
                    .map(ppModel -> {
                        ProductoProveedorDTO ppDto = new ProductoProveedorDTO();
                        if (ppModel.getProveedor() != null) { // Asegura que el proveedor no es nulo
                            ppDto.setIdProveedor(ppModel.getProveedor().getId());
                            ppDto.setNombreProveedor(ppModel.getProveedor().getNombre());
                        }
                        if (ppModel.getProducto() != null) { // Asegura que el producto no es nulo
                            ppDto.setIdProducto(ppModel.getProducto().getId());
                            ppDto.setNombreProducto(ppModel.getProducto().getNombre());
                        }
                        ppDto.setPrecioCompraEspecifico(ppModel.getPrecio()); // El precio específico de la relación
                        return ppDto;
                    })
                    .collect(Collectors.toList());
            dto.setProveedoresAsociados(proveedoresAsociados);
        } else {
            dto.setProveedoresAsociados(new ArrayList<>()); // Asegura que no sea null
        }
        return dto;
    }

    // El mapProductoDTOToModel es menos crítico ya que se usa principalmente en guardarProducto/actualizarProducto
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
        } else {
            productoModel = productoRepository.findById(productoDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Producto con ID " + productoDTO.getId() + " no encontrado para actualizar."));
            if (productoDTO.getStock() != null) {
                productoModel.setStock(productoDTO.getStock());
            }
            if (productoDTO.getPrecioCompra() != null) {
                productoModel.setPrecioCompra(productoDTO.getPrecioCompra());
            }
        }

        productoModel.setNombre(productoDTO.getNombre());
        productoModel.setDescripcion(productoDTO.getDescripcion());
        productoModel.setPrecioVenta(productoDTO.getPrecioVenta());

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

    @Transactional
    public Optional<ProductoModel> actualizarProducto(Integer id, ProductoModel productoActualizado) {
        Optional<ProductoModel> existingProductOptional = productoRepository.findById(id);

        if (existingProductOptional.isPresent()) {
            ProductoModel existingProduct = existingProductOptional.get();

            existingProduct.setNombre(productoActualizado.getNombre());
            if (productoActualizado.getStock() != null) {
                existingProduct.setStock(productoActualizado.getStock());
            }
            if (productoActualizado.getPrecioCompra() != null) {
                existingProduct.setPrecioCompra(productoActualizado.getPrecioCompra());
            }

            existingProduct.setDescripcion(productoActualizado.getDescripcion());
            existingProduct.setPrecioVenta(productoActualizado.getPrecioVenta());

            Set<ProductoProveedorModel> incomingProductoProveedores = productoActualizado.getProductoProveedores();

            if (incomingProductoProveedores == null) {
                incomingProductoProveedores = new HashSet<>();
            }

            Set<ProductoProveedorId> incomingPpIds = incomingProductoProveedores.stream()
                    .map(ProductoProveedorModel::getId)
                    .collect(Collectors.toSet());

            existingProduct.getProductoProveedores().removeIf(
                    existingPp -> !incomingPpIds.contains(existingPp.getId())
            );

            for (ProductoProveedorModel incomingPp : incomingProductoProveedores) {
                ProductoProveedorId currentId = new ProductoProveedorId(existingProduct.getId(), incomingPp.getProveedor().getId());

                Optional<ProductoProveedorModel> existingPpOptional = existingProduct.getProductoProveedores().stream()
                        .filter(pp -> pp.getId().equals(currentId))
                        .findFirst();

                if (existingPpOptional.isPresent()) {
                    existingPpOptional.get().setPrecio(incomingPp.getPrecio());
                } else {
                    ProveedorModel proveedor = proveedorRepository.findById(incomingPp.getProveedor().getId())
                            .orElseThrow(() -> new RuntimeException("Proveedor con ID " + incomingPp.getProveedor().getId() + " no encontrado."));

                    ProductoProveedorModel newPp = new ProductoProveedorModel();
                    newPp.setProducto(existingProduct);
                    newPp.setProveedor(proveedor);
                    newPp.setPrecio(incomingPp.getPrecio());
                    existingProduct.getProductoProveedores().add(newPp);
                }
            }
            return Optional.of(productoRepository.save(existingProduct));

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

    public List<ProductoDTO> obtenerProductosPorProveedor(Integer idProveedor) {
        List<ProductoProveedorModel> relaciones = productoProveedorRepository.findById_IdProveedor(idProveedor);

        return relaciones.stream()
                .map(ppModel -> mapProductoModelToDTO(ppModel.getProducto()))
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
            producto.getProductoProveedores().add(productoProveedor);
        }

        // Guardar el producto (esto también guardará/actualizará la relación ProductoProveedor gracias a CascadeType.ALL)
        ProductoModel productoActualizado = productoRepository.save(producto);
        return mapProductoModelToDTO(productoActualizado);
    }
}