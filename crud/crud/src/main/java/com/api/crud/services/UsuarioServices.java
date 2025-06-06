package com.api.crud.services;

import com.api.crud.dto.RolDTO;
import com.api.crud.dto.UsuarioDTO;
import com.api.crud.models.RolModel;
import com.api.crud.models.UsuarioModel;
import com.api.crud.repositories.RolRepository;
import com.api.crud.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioServices {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Guardar un nuevo usuario (o actualizar si el ID existe)
    public UsuarioDTO guardarUsuario(UsuarioDTO usuarioDTO, String password) {
        // 1. Validación de DNI
        Optional<UsuarioModel> existingUsuario = usuarioRepository.findByDni(usuarioDTO.getDni());
        if (existingUsuario.isPresent() && (usuarioDTO.getId() == null || !existingUsuario.get().getIdUsuario().equals(usuarioDTO.getId()))) {
            throw new RuntimeException("Ya existe un usuario con el DNI: " + usuarioDTO.getDni());
        }

        // 2. Buscar y asignar el Rol
        RolModel rolModel = null;
        if (usuarioDTO.getRol() != null && usuarioDTO.getRol().getId() != null) {
            Optional<RolModel> rolOptional = rolRepository.findById(usuarioDTO.getRol().getId());
            if (rolOptional.isPresent()) {
                rolModel = rolOptional.get();
            } else {
                throw new RuntimeException("Rol no encontrado con ID: " + usuarioDTO.getRol().getId());
            }
        } else {
            throw new RuntimeException("Se requiere un rol para el usuario.");
        }

        // 3. Mapear DTO a Modelo
        UsuarioModel usuarioModel = mapUsuarioDTOToModel(usuarioDTO, rolModel);

        // 4. Lógica de Hashing de Contraseña y Manejo de Actualización
        if (password != null && !password.isEmpty()) {
            usuarioModel.setPassword(passwordEncoder.encode(password));
        } else if (usuarioDTO.getId() == null) {
            throw new RuntimeException("La contraseña es requerida para un nuevo usuario.");
        } else {
            usuarioRepository.findById(usuarioDTO.getId()).ifPresent(existingUserInDb -> {
                usuarioModel.setPassword(existingUserInDb.getPassword());
            });
        }

        // 5. Guardar el usuario
        UsuarioModel usuarioGuardado = usuarioRepository.save(usuarioModel);

        // 6. Mapear Modelo a DTO de respuesta
        return mapUsuarioModelToDTO(usuarioGuardado);
    }

    // Método de mapeo de UsuarioModel a UsuarioDTO (para respuestas GET)
    public List<UsuarioDTO> obtenerTodosLosUsuarios() {
        List<UsuarioModel> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(this::mapUsuarioModelToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> obtenerUsuarioPorId(Integer id) {
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findById(id);
        return usuarioOptional.map(this::mapUsuarioModelToDTO);
    }

    // --- NUEVO MÉTODO AÑADIDO PARA BUSCAR POR DNI ---
    public Optional<UsuarioDTO> obtenerUsuarioPorDni(String dni) {
        Optional<UsuarioModel> usuarioOptional = usuarioRepository.findByDni(dni);
        return usuarioOptional.map(this::mapUsuarioModelToDTO);
    }
    // --- FIN NUEVO MÉTODO ---

    public boolean eliminarUsuario(Integer id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Mapeo de UsuarioDTO a UsuarioModel
    private UsuarioModel mapUsuarioDTOToModel(UsuarioDTO usuarioDTO, RolModel rolModel) {
        UsuarioModel model = new UsuarioModel();
        if (usuarioDTO.getId() != null) {
            model.setIdUsuario(usuarioDTO.getId());
        }
        model.setDni(usuarioDTO.getDni());
        model.setNombre(usuarioDTO.getNombre());
        model.setApellido(usuarioDTO.getApellido());
        model.setContacto(usuarioDTO.getContacto());
        model.setRol(rolModel);
        return model;
    }

    // Mapeo de UsuarioModel a UsuarioDTO (para respuestas de API)
    private UsuarioDTO mapUsuarioModelToDTO(UsuarioModel usuarioModel) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuarioModel.getIdUsuario());
        dto.setDni(usuarioModel.getDni());
        dto.setNombre(usuarioModel.getNombre());
        dto.setApellido(usuarioModel.getApellido());
        dto.setContacto(usuarioModel.getContacto());

        if (usuarioModel.getRol() != null) {
            RolDTO rolDTO = new RolDTO();
            rolDTO.setId(usuarioModel.getRol().getIdRoles());
            rolDTO.setNombre(usuarioModel.getRol().getNombre());
            rolDTO.setDescripcion(usuarioModel.getRol().getDescripcion());
            dto.setRol(rolDTO);
        }
        return dto;
    }
}