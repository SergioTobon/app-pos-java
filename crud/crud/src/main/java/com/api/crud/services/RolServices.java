package com.api.crud.services;

import com.api.crud.dto.RolDTO;
import com.api.crud.models.RolModel;
import com.api.crud.repositories.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RolServices {

    @Autowired
    private RolRepository rolRepository;

    // Obtener todos los roles
    public List<RolDTO> obtenerTodosLosRoles() {
        List<RolModel> roles = rolRepository.findAll();
        List<RolDTO> rolesDTO = new ArrayList<>();
        for (RolModel rol : roles) {
            rolesDTO.add(mapRolModelToDTO(rol));
        }
        return rolesDTO;
    }

    // Obtener un rol por ID
    public Optional<RolDTO> obtenerRolPorId(Integer id) {
        Optional<RolModel> rolOptional = rolRepository.findById(id);
        return rolOptional.map(this::mapRolModelToDTO);
    }

    // Guardar un nuevo rol (o actualizar si el ID existe)
    public RolDTO guardarRol(RolDTO rolDTO) {
        // Puedes añadir validaciones, por ejemplo, si el nombre ya existe
        Optional<RolModel> existingRol = rolRepository.findByNombre(rolDTO.getNombre());
        if (existingRol.isPresent() && (rolDTO.getId() == null || !existingRol.get().getIdRoles().equals(rolDTO.getId()))) {
            throw new RuntimeException("Ya existe un rol con el nombre: " + rolDTO.getNombre());
        }

        RolModel rolModel = mapRolDTOToModel(rolDTO);
        RolModel rolGuardado = rolRepository.save(rolModel);
        return mapRolModelToDTO(rolGuardado);
    }

    // Eliminar un rol por ID
    public boolean eliminarRol(Integer id) {
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Mapeo de RolModel a RolDTO
    private RolDTO mapRolModelToDTO(RolModel rolModel) {
        RolDTO dto = new RolDTO();
        dto.setId(rolModel.getIdRoles());
        dto.setNombre(rolModel.getNombre());
        dto.setDescripcion(rolModel.getDescripcion());
        return dto;
    }

    // Mapeo de RolDTO a RolModel
    private RolModel mapRolDTOToModel(RolDTO rolDTO) {
        RolModel model = new RolModel();
        if (rolDTO.getId() != null) {
            model.setIdRoles(rolDTO.getId());
        }
        model.setNombre(rolDTO.getNombre());
        model.setDescripcion(rolDTO.getDescripcion());
        return model;
    }
}