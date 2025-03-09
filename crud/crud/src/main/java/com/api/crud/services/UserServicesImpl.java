package com.api.crud.services;

import com.api.crud.dao.UserDAO;
import com.api.crud.dto.UserDTO;
import com.api.crud.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private UserDAO userDAO;

    @Override
    public Optional<UserModel> autenticar(String dni, String password) {
        return userDAO.findByDniAndPassword(dni, password);
    }

    @Override
    public UserModel createUser(UserDTO userDTO) {
        UserModel user = new UserModel();
        user.setDni(userDTO.getDni());
        user.setNombre(userDTO.getNombre());
        user.setApellido(userDTO.getApellido());
        user.setContacto(userDTO.getContacto());
        user.setPassword(userDTO.getPassword());
        user.setIdRol(userDTO.getIdRol()); // Se asigna el rol correctamente

        return userDAO.save(user); // Guarda el usuario en la BD
    }

    @Override
    public void deleteUser(Long idUsuario) {
        if (userDAO.existsById(idUsuario)) {
            userDAO.deleteById(idUsuario);
        } else {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }
    }

    @Override
    public UserModel updateUser(Long idUsuario, UserDTO userDTO) {
        Optional<UserModel> optionalUser = userDAO.findById(idUsuario);

        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            user.setDni(userDTO.getDni());
            user.setNombre(userDTO.getNombre());
            user.setApellido(userDTO.getApellido());
            user.setContacto(userDTO.getContacto());
            user.setPassword(userDTO.getPassword());
            user.setIdRol(userDTO.getIdRol());

            return userDAO.save(user);
        } else {
            throw new RuntimeException("Usuario no encontrado");
        }
    }

    @Override
    public List<UserModel> getUsersByIdRol(int idRol) {
        return userDAO.findByIdRol(idRol);
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userDAO.findAll();
    }

    @Override
    public Optional<UserModel> obtenerPorId(Long id) {
        return userDAO.findById(id);
    }


}
