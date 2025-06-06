package com.api.crud.services;

import com.api.crud.models.ProveedorModel;
import com.api.crud.models.ProductoModel; // Importa ProductoModel para poder acceder a sus propiedades
import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.repositories.ProveedorRepository;
import com.api.crud.repositories.ProductoProveedorRepository;
import com.api.crud.dto.ProveedorDTO;
import com.api.crud.dto.ProductoProveedorDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProveedorServices {

    private final ProveedorRepository proveedorRepository;
    private final ProductoProveedorRepository productoProveedorRepository;

    @Autowired
    public ProveedorServices(ProveedorRepository proveedorRepository,
                             ProductoProveedorRepository productoProveedorRepository) {
        this.proveedorRepository = proveedorRepository;
        this.productoProveedorRepository = productoProveedorRepository;
    }



    /**
     * Mapea un ProveedorModel a un ProveedorDTO para ser enviado al cliente.
     * No incluye las relaciones de productos para evitar bucles y mantener la simplicidad.
     * @param proveedorModel El modelo de proveedor a mapear.
     * @return El DTO de proveedor.
     */
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

    /**
     * Mapea un ProveedorDTO a un ProveedorModel para ser guardado en la base de datos.
     * @param proveedorDTO El DTO de proveedor a mapear.
     * @return El modelo de proveedor.
     */
    private ProveedorModel mapProveedorDTOToModel(ProveedorDTO proveedorDTO) {
        ProveedorModel model = new ProveedorModel();
        if (proveedorDTO.getId() != null) {
            model.setId(proveedorDTO.getId());
        }
        model.setNombre(proveedorDTO.getNombre());
        model.setNit(proveedorDTO.getNit());
        model.setContacto(proveedorDTO.getContacto());
        model.setDireccion(proveedorDTO.getDireccion());
        model.setEmail(proveedorDTO.getEmail());
        model.setTelefono(proveedorDTO.getTelefono());
        return model;
    }



    /**
     * Obtiene una lista de todos los proveedores disponibles, mapeados a DTOs.
     * @return Una lista de ProveedorDTO.
     */
    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        List<ProveedorModel> proveedores = proveedorRepository.findAll();
        return proveedores.stream()
                .map(this::mapProveedorModelToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un proveedor por su ID, mapeado a un DTO.
     * @param id El ID del proveedor.
     * @return Un Optional que contiene el ProveedorDTO si se encuentra, o vacío si no.
     */
    public Optional<ProveedorDTO> obtenerProveedorPorId(Integer id) {
        Optional<ProveedorModel> proveedorOptional = proveedorRepository.findById(id);
        return proveedorOptional.map(this::mapProveedorModelToDTO);
    }

    /**
     * Guarda un nuevo proveedor en la base de datos.
     * @param proveedorDTO El DTO del proveedor a guardar.
     * @return El ProveedorDTO del proveedor guardado.
     */
    public ProveedorDTO guardarProveedor(ProveedorDTO proveedorDTO) {
        ProveedorModel proveedorModel = mapProveedorDTOToModel(proveedorDTO);
        ProveedorModel proveedorGuardado = proveedorRepository.save(proveedorModel);
        return mapProveedorModelToDTO(proveedorGuardado);
    }

    /**
     * Actualiza un proveedor existente en la base de datos.
     * @param id El ID del proveedor a actualizar.
     * @param proveedorDTO El DTO del proveedor con la información actualizada.
     * @return Un Optional que contiene el ProveedorDTO actualizado si se encuentra, o vacío si no.
     */
    public Optional<ProveedorDTO> actualizarProveedor(Integer id, ProveedorDTO proveedorDTO) {
        Optional<ProveedorModel> proveedorExistenteOptional = proveedorRepository.findById(id);

        if (proveedorExistenteOptional.isPresent()) {
            ProveedorModel proveedorExistente = proveedorExistenteOptional.get();
            proveedorExistente.setNombre(proveedorDTO.getNombre());
            proveedorExistente.setNit(proveedorDTO.getNit());
            // CORRECCIÓN: 'proveente' cambiado a 'proveedorExistente'
            proveedorExistente.setContacto(proveedorDTO.getContacto());
            proveedorExistente.setDireccion(proveedorDTO.getDireccion());
            proveedorExistente.setEmail(proveedorDTO.getEmail());
            proveedorExistente.setTelefono(proveedorDTO.getTelefono());

            ProveedorModel proveedorActualizado = proveedorRepository.save(proveedorExistente);
            return Optional.of(mapProveedorModelToDTO(proveedorActualizado));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Elimina un proveedor por su ID.
     * Las relaciones ProductoProveedorModel asociadas serán eliminadas en cascada gracias a
     * la configuración de la relación en ProveedorModel (CascadeType.ALL, orphanRemoval=true).
     * @param id El ID del proveedor a eliminar.
     * @return true si el proveedor fue eliminado, false si no se encontró.
     */
    @Transactional
    public boolean eliminarProveedor(Integer id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return true;
        }
        return false;
    }



    /**
     * Obtiene una lista de ProductoProveedorDTO que representan los productos que un proveedor suministra,
     * incluyendo el precio de compra específico.
     * @param idProveedor ID del proveedor.
     * @return Una lista de ProductoProveedorDTO. Retorna una lista vacía si el proveedor no suministra productos
     * o si el proveedor no existe.
     */
    public List<ProductoProveedorDTO> obtenerProductosSuministradosPorProveedor(Integer idProveedor) {
        // Aseguramos que el proveedor exista primero
        Optional<ProveedorModel> proveedorOptional = proveedorRepository.findById(idProveedor);
        if (proveedorOptional.isEmpty()) {
            // Si el proveedor no existe, no hay productos suministrados por él.
            return List.of(); // Devuelve una lista inmutable vacía
        }

        // Se corrigió el nombre del método en el repositorio a `findById_IdProveedor` (o el nombre real de tu método)
        // O MEJOR AÚN: Si tienes la relación mapeada en ProveedorModel, puedes accederla directamente:
        // List<ProductoProveedorModel> relaciones = proveedorOptional.get().getProductoProveedores();
        // Asegúrate de que ProveedorModel tenga:
        // @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
        // private Set<ProductoProveedorModel> productoProveedores = new HashSet<>();

        // Si prefieres usar el repositorio ProductoProveedorRepository (es más explícito para la tabla de unión)
        // asegúrate de que el nombre del método sea el correcto según cómo lo hayas definido en tu JpaRepository.
        // Asumiendo que `findById_IdProveedor` busca por el 'idProveedor' dentro de la clave compuesta 'id'.
        List<ProductoProveedorModel> relaciones = productoProveedorRepository.findById_IdProveedor(idProveedor);


        return relaciones.stream()
                .map(ppModel -> {
                    ProductoProveedorDTO dto = new ProductoProveedorDTO();
                    // Para evitar NullPointerException si Producto o Proveedor no se cargan bien (aunque no debería pasar)
                    if (ppModel.getProducto() != null) {
                        dto.setIdProducto(ppModel.getProducto().getId());
                        dto.setNombreProducto(ppModel.getProducto().getNombre());
                    }
                    if (ppModel.getProveedor() != null) {
                        dto.setIdProveedor(ppModel.getProveedor().getId());
                        dto.setNombreProveedor(ppModel.getProveedor().getNombre());
                    }
                    dto.setPrecioCompraEspecifico(ppModel.getPrecio());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}