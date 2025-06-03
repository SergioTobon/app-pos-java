package com.api.crud.mapper;

import com.api.crud.dto.ProveedorDTO;
import com.api.crud.models.ProveedorModel;

public class ProveedorMapper {

    public static ProveedorDTO toDTO(ProveedorModel proveedor) {
        if (proveedor == null) {
            return null;
        }

        ProveedorDTO dto = new ProveedorDTO();
        dto.setId(proveedor.getId());
        dto.setNombre(proveedor.getNombre());
        dto.setContacto(proveedor.getContacto());
        dto.setDireccion(proveedor.getDireccion());
        dto.setEmail(proveedor.getEmail());
        dto.setTelefono(proveedor.getTelefono());
        dto.setNit(proveedor.getNit());
        return dto;
    }

    public static ProveedorModel toEntity(ProveedorDTO dto) {
        if (dto == null) {
            return null;
        }

        ProveedorModel proveedor = new ProveedorModel();
        proveedor.setId(dto.getId());
        proveedor.setNombre(dto.getNombre());
        proveedor.setContacto(dto.getContacto());
        proveedor.setDireccion(dto.getDireccion());
        proveedor.setEmail(dto.getEmail());
        proveedor.setTelefono(dto.getTelefono());
        proveedor.setNit(dto.getNit());
        return proveedor;
    }
}
