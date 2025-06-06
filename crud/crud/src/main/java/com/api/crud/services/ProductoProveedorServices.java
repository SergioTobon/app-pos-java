package com.api.crud.services;

import com.api.crud.dto.ProductoProveedorDTO;
import com.api.crud.repositories.ProductoProveedorRepository;
import com.api.crud.repositories.ProductoRepository; // Necesario para cargar ProductoModel
import com.api.crud.repositories.ProveedorRepository; // Necesario para cargar ProveedorModel
import com.api.crud.mapper.ProductoProveedorMapper;
import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.models.ProductoProveedorId; // Importar la clave compuesta
import com.api.crud.models.ProductoModel; // Importar ProductoModel
import com.api.crud.models.ProveedorModel; // Importar ProveedorModel

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional; // Para operaciones transaccionales

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoProveedorServices {

    private final ProductoProveedorRepository productoProveedorRepository;
    private final ProductoRepository productoRepository; // Inyectar ProductoRepository
    private final ProveedorRepository proveedorRepository; // Inyectar ProveedorRepository
    private final ProductoProveedorMapper productoProveedorMapper; // Inyectar el Mapper

    @Autowired
    public ProductoProveedorServices(ProductoProveedorRepository productoProveedorRepository,
                                     ProductoRepository productoRepository,
                                     ProveedorRepository proveedorRepository,
                                     ProductoProveedorMapper productoProveedorMapper) {
        this.productoProveedorRepository = productoProveedorRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoProveedorMapper = productoProveedorMapper;
    }

    public List<ProductoProveedorDTO> listarProductoProveedores() {
        return productoProveedorRepository.findAll().stream()
                .map(productoProveedorMapper::toDTO) // Usar la instancia del mapper
                .collect(Collectors.toList());
    }

    // Para obtener, necesitamos los dos IDs que componen la clave.
    public Optional<ProductoProveedorDTO> obtenerProductoProveedorPorIds(Integer idProducto, Integer idProveedor) {
        ProductoProveedorId id = new ProductoProveedorId(idProducto, idProveedor);
        return productoProveedorRepository.findById(id)
                .map(productoProveedorMapper::toDTO); // Usar la instancia del mapper
    }

    @Transactional // Es buena práctica para operaciones de guardado/actualización
    public ProductoProveedorDTO guardarProductoProveedor(ProductoProveedorDTO productoProveedorDTO) {
        // Necesitamos cargar las entidades Producto y Proveedor completas para construir el modelo.
        ProductoModel producto = productoRepository.findById(productoProveedorDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoProveedorDTO.getIdProducto()));
        ProveedorModel proveedor = proveedorRepository.findById(productoProveedorDTO.getIdProveedor())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + productoProveedorDTO.getIdProveedor()));

        // Mapear de DTO a Model, pasando las entidades cargadas
        ProductoProveedorModel productoProveedor = productoProveedorMapper.toModel(productoProveedorDTO, producto, proveedor);

        productoProveedor = productoProveedorRepository.save(productoProveedor);
        return productoProveedorMapper.toDTO(productoProveedor); // Usar la instancia del mapper
    }

    // Para actualizar, también necesitamos los dos IDs para identificar la relación existente
    @Transactional
    public ProductoProveedorDTO actualizarProductoProveedor(Integer idProducto, Integer idProveedor, ProductoProveedorDTO productoProveedorDTO) {
        ProductoProveedorId id = new ProductoProveedorId(idProducto, idProveedor);

        // Verificar si la relación existe
        Optional<ProductoProveedorModel> productoProveedorExistente = productoProveedorRepository.findById(id);
        if (productoProveedorExistente.isEmpty()) {
            return null; // O lanzar una excepción, dependiendo de tu manejo de errores
        }

        // Cargar las entidades Producto y Proveedor, aunque para una actualización sobre una relación existente
        // es probable que las referencias en el 'productoProveedorExistente' ya sean suficientes,
        // a menos que el DTO permita cambiar el producto o proveedor de la relación (lo cual es raro).
        // Para simplemente actualizar el precio, no necesitarías recargar producto/proveedor.
        ProductoModel producto = productoRepository.findById(productoProveedorDTO.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoProveedorDTO.getIdProducto()));
        ProveedorModel proveedor = proveedorRepository.findById(productoProveedorDTO.getIdProveedor())
                .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con ID: " + productoProveedorDTO.getIdProveedor()));


        // Actualizar el modelo existente con los nuevos datos del DTO
        ProductoProveedorModel modelToUpdate = productoProveedorExistente.get();
        modelToUpdate.setPrecio(productoProveedorDTO.getPrecioCompraEspecifico()); // Asumiendo que el precio es lo único actualizable

        // Si el DTO permite cambiar producto/proveedor, sería más complejo:
        // modelToUpdate.setProducto(producto);
        // modelToUpdate.setProveedor(proveedor);
        // y la clave compuesta debería actualizarse:
        // modelToUpdate.setId(new ProductoProveedorId(producto.getId(), proveedor.getId()));

        productoProveedorRepository.save(modelToUpdate); // Guardar el modelo actualizado
        return productoProveedorMapper.toDTO(modelToUpdate); // Mapear de vuelta a DTO
    }

    // Para eliminar, también necesitamos los dos IDs que componen la clave.
    @Transactional
    public void eliminarProductoProveedor(Integer idProducto, Integer idProveedor) {
        ProductoProveedorId id = new ProductoProveedorId(idProducto, idProveedor);
        productoProveedorRepository.deleteById(id);
    }
}