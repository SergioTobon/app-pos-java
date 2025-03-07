package com.api.crud.services;

import com.api.crud.dao.UserDAO;
import com.api.crud.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServicesImpl implements UserServices {

    @Autowired
    private UserDAO userDAO;

    @Override
    public Optional<UserModel> autenticar(String dni, String password) {
        return userDAO.findByDniAndPassword(dni, password);
    }
}
