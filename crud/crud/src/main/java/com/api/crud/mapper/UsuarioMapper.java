package com.api.crud.mapper;

import com.api.crud.dto.RolDTO;
import com.api.crud.dto.UsuarioDTO;
import com.api.crud.models.UsuarioModel;

public class UsuarioMapper {
    public static UsuarioDTO toDTO(UsuarioModel user) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(user.getIdUsuario());
        dto.setNombre(user.getNombre());
        dto.setApellido(user.getApellido());
        dto.setDni(user.getDni());
        dto.setContacto(user.getContacto());

        // Convertir el rol en un objeto
        RolDTO rolDTO = new RolDTO();
        rolDTO.setId(user.getRol().getIdRoles());
        rolDTO.setNombre(user.getRol().getNombre());
        rolDTO.setDescripcion(user.getRol().getDescripcion()); // ✅ Se agrega la descripción

        dto.setRol(rolDTO); // ✅ Ahora devuelve el rol como objeto

        return dto;
    }
}
