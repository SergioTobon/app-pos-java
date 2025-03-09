package com.api.crud.dao;

import com.api.crud.models.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByDniAndPassword(String dni, String password);

    List<UserModel> findByIdRol(int idRol);
}
