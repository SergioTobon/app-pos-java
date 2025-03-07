package com.api.crud.services;

import com.api.crud.models.UserModel;
import java.util.Optional;

public interface UserServices {

    Optional<UserModel> autenticar(String dni, String password);

}
