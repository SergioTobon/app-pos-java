package com.api.crud.services;

import com.api.crud.dto.UserDTO;
import com.api.crud.models.UserModel;

import java.util.List;
import java.util.Optional;

public interface UserServices {

    Optional<UserModel> autenticar(String dni, String password);

    UserModel createUser(UserDTO userDTO);

    void deleteUser(Long id_usuario);

    UserModel updateUser(Long idUsuario, UserDTO userDTO);

    List<UserModel> getUsersByIdRol(int idRol);

    List<UserModel> getAllUsers();

    Optional<UserModel> obtenerPorId(Long idUsuario);


}
