package com.api.crud.services;

import com.api.crud.models.ProveedorModel; // Importa tu modelo de Proveedor
import com.api.crud.repositories.ProveedorRepository; // Importa tu repositorio de Proveedor
import com.api.crud.dto.ProveedorDTO; // Importa tu DTO de Proveedor
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProveedorServices {

    private final ProveedorRepository proveedorRepository;

    // Inyección de dependencias a través del constructor
    @Autowired
    public ProveedorServices(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    // --- Métodos de Mapeo (Helper Methods) ---

    // Mapea ProveedorModel a ProveedorDTO (para enviar al cliente)
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

    // Mapea ProveedorDTO a ProveedorModel (para guardar en la base de datos)
    private ProveedorModel mapProveedorDTOToModel(ProveedorDTO proveedorDTO) {
        ProveedorModel model = new ProveedorModel();
        // ID se asigna solo si es una actualización, no en la creación
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


    // --- Métodos CRUD ---

    public List<ProveedorDTO> obtenerTodosLosProveedores() {
        List<ProveedorModel> proveedores = proveedorRepository.findAll();
        List<ProveedorDTO> proveedoresDTO = new ArrayList<>();
        for (ProveedorModel proveedor : proveedores) {
            proveedoresDTO.add(mapProveedorModelToDTO(proveedor));
        }
        return proveedoresDTO;
    }

    public Optional<ProveedorDTO> obtenerProveedorPorId(Integer id) {
        Optional<ProveedorModel> proveedorOptional = proveedorRepository.findById(id);
        return proveedorOptional.map(this::mapProveedorModelToDTO);
    }

    public ProveedorDTO guardarProveedor(ProveedorDTO proveedorDTO) {
        ProveedorModel proveedorModel = mapProveedorDTOToModel(proveedorDTO);
        ProveedorModel proveedorGuardado = proveedorRepository.save(proveedorModel);
        return mapProveedorModelToDTO(proveedorGuardado);
    }

    public Optional<ProveedorDTO> actualizarProveedor(Integer id, ProveedorDTO proveedorDTO) {
        Optional<ProveedorModel> proveedorExistenteOptional = proveedorRepository.findById(id);

        if (proveedorExistenteOptional.isPresent()) {
            ProveedorModel proveedorExistente = proveedorExistenteOptional.get();
            // Actualizar solo los campos que vienen en el DTO
            proveedorExistente.setNombre(proveedorDTO.getNombre());
            proveedorExistente.setNit(proveedorDTO.getNit());
            proveedorExistente.setContacto(proveedorDTO.getContacto());
            proveedorExistente.setDireccion(proveedorDTO.getDireccion());
            proveedorExistente.setEmail(proveedorDTO.getEmail());
            proveedorExistente.setTelefono(proveedorDTO.getTelefono());

            ProveedorModel proveedorActualizado = proveedorRepository.save(proveedorExistente);
            return Optional.of(mapProveedorModelToDTO(proveedorActualizado));
        } else {
            return Optional.empty(); // Proveedor no encontrado para actualizar
        }
    }

    public boolean eliminarProveedor(Integer id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return true;
        }
        return false;
    }
}