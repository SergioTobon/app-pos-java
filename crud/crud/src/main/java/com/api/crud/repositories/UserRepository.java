package com.api.crud.repositories;

import com.ejemplo.inventario.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByDni(String dni); // Buscar usuario por DNI
}
