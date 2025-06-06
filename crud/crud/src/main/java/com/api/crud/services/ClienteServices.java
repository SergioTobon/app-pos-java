package com.api.crud.services;

import com.api.crud.dto.ClienteDTO;
import com.api.crud.models.ClienteModel;
import com.api.crud.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteServices {

    @Autowired
    private ClienteRepository clienteRepository;

    // Obtener todos los clientes
    public List<ClienteDTO> obtenerTodosLosClientes() {
        List<ClienteModel> clientes = clienteRepository.findAll();
        List<ClienteDTO> clientesDTO = new ArrayList<>();
        for (ClienteModel cliente : clientes) {
            clientesDTO.add(mapClienteModelToDTO(cliente));
        }
        return clientesDTO;
    }

    // Obtener un cliente por ID
    public Optional<ClienteDTO> obtenerClientePorId(Integer id) {
        Optional<ClienteModel> clienteOptional = clienteRepository.findById(id);
        return clienteOptional.map(this::mapClienteModelToDTO);
    }

    // Guardar un nuevo cliente (o actualizar si el ID existe)
    public ClienteDTO guardarCliente(ClienteDTO clienteDTO) {
        // Puedes añadir validaciones aquí, por ejemplo, si el DNI ya existe
        Optional<ClienteModel> existingCliente = clienteRepository.findByDni(clienteDTO.getDni());
        if (existingCliente.isPresent() && (clienteDTO.getId() == null || !existingCliente.get().getIdCliente().equals(clienteDTO.getId()))) {
            throw new RuntimeException("Ya existe un cliente con el DNI: " + clienteDTO.getDni());
        }

        ClienteModel clienteModel = mapClienteDTOToModel(clienteDTO);
        ClienteModel clienteGuardado = clienteRepository.save(clienteModel);
        return mapClienteModelToDTO(clienteGuardado);
    }

    // Eliminar un cliente por ID
    public boolean eliminarCliente(Integer id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // Mapeo de ClienteModel a ClienteDTO
    private ClienteDTO mapClienteModelToDTO(ClienteModel clienteModel) {
        ClienteDTO dto = new ClienteDTO();
        dto.setId(clienteModel.getIdCliente());
        dto.setDni(clienteModel.getDni());
        dto.setNombre(clienteModel.getNombre());
        dto.setContacto(clienteModel.getContacto());
        return dto;
    }

    // Mapeo de ClienteDTO a ClienteModel
    private ClienteModel mapClienteDTOToModel(ClienteDTO clienteDTO) {
        ClienteModel model = new ClienteModel();
        // Si el DTO tiene un ID, se lo asignamos para que save() sepa que es una actualización
        if (clienteDTO.getId() != null) {
            model.setIdCliente(clienteDTO.getId());
        }
        model.setDni(clienteDTO.getDni());
        model.setNombre(clienteDTO.getNombre());
        model.setContacto(clienteDTO.getContacto());
        return model;
    }
}