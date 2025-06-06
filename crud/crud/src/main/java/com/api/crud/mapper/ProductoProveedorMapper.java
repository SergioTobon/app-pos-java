// C:\Programacion\ProyectosPOLI\app-pos-java\crud\crud\src\main\java\com\api\crud\mapper\ProductoProveedorMapper.java

package com.api.crud.mapper;

import com.api.crud.models.ProductoProveedorModel;
import com.api.crud.dto.ProductoProveedorDTO;
// Asegúrate de importar ProductoModel y ProveedorModel si los necesitas para obtener nombres
import com.api.crud.models.ProductoModel;
import com.api.crud.models.ProveedorModel;

import org.springframework.stereotype.Component; // O la anotación de tu mapper (ej. @Mapper de MapStruct)

// Si usas MapStruct, la interfaz sería así:
// @Mapper(componentModel = "spring")
// public interface ProductoProveedorMapper {
//     ProductoProveedorDTO toDTO(ProductoProveedorModel model);
//     ProductoProveedorModel toModel(ProductoProveedorDTO dto);
// }
// Y tendrías que definir mappings personalizados para los IDs de la clave compuesta.

// Si es un mapper manual (como los que hemos estado usando en los Services):
@Component // Si quieres que Spring lo gestione como un bean
public class ProductoProveedorMapper {

    public ProductoProveedorDTO toDTO(ProductoProveedorModel model) {
        if (model == null) {
            return null;
        }

        ProductoProveedorDTO dto = new ProductoProveedorDTO();

        // ELIMINAR ESTA LÍNEA QUE CAUSA EL ERROR:
        // dto.setId(model.getId()); // <-- ESTO ES EL ERROR. ProductoProveedorDTO no tiene setId(ProductoProveedorId)

        // CORRECTO: Mapear los componentes individuales de la clave compuesta y otros atributos
        dto.setIdProducto(model.getId().getIdProducto()); // Mapea el ID del Producto desde la clave compuesta
        dto.setIdProveedor(model.getId().getIdProveedor()); // Mapea el ID del Proveedor desde la clave compuesta
        dto.setPrecioCompraEspecifico(model.getPrecio());

        // Opcional: Si quieres los nombres en el DTO, necesitarás que el modelo tenga las referencias cargadas
        // O cargar los nombres en el servicio antes de mapear.
        if (model.getProducto() != null) {
            dto.setNombreProducto(model.getProducto().getNombre());
        }
        if (model.getProveedor() != null) {
            dto.setNombreProveedor(model.getProveedor().getNombre());
        }

        return dto;
    }

    public ProductoProveedorModel toModel(ProductoProveedorDTO dto, ProductoModel producto, ProveedorModel proveedor) {
        // Para convertir de DTO a Model, normalmente necesitas las entidades Producto y Proveedor ya cargadas
        // porque ProductoProveedorModel necesita las referencias a los objetos ProductoModel y ProveedorModel
        // para su constructor y la clave compuesta.
        // Este método se usaría internamente en un servicio que ya tiene los objetos Producto y Proveedor.

        if (dto == null || producto == null || proveedor == null) {
            return null;
        }

        // Aquí creas el ProductoProveedorModel usando las referencias completas,
        // no solo los IDs del DTO, porque la entidad necesita las referencias.
        return new ProductoProveedorModel(producto, proveedor, dto.getPrecioCompraEspecifico());
    }

    // Un método auxiliar si solo necesitas un mapeo simple sin las entidades completas (menos común)
    // ProductoProveedorModel fromDto(ProductoProveedorDTO dto) { ... }
}