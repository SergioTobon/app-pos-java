package com.api.crud.mapper;

import com.api.crud.dto.RolDTO;
import com.api.crud.models.RolModel;

public class RolMapper {

    public static RolDTO toDTO(RolModel rol) {
        if (rol == null) {
            return null;
        }

        RolDTO dto = new RolDTO();
        dto.setId(rol.getIdRoles()); // <-- CORRECCIÓN
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        return dto;
    }

    public static RolModel toEntity(RolDTO dto) {
        if (dto == null) {
            return null;
        }

        RolModel rol = new RolModel();
        // Cuando mapeas de DTO a Entity, el ID de la Entity es su clave primaria
        // Y el DTO tiene un método getId()
        if (dto.getId() != null) { // Solo si el DTO ya tiene un ID (para actualizaciones)
            rol.setIdRoles(dto.getId()); // <-- CORRECCIÓN
        }
        rol.setNombre(dto.getNombre());
        rol.setDescripcion(dto.getDescripcion());
        return rol;
    }
}