package com.api.crud.repositories;

import com.api.crud.models.RolModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RolModel, Integer> {
    // Método para buscar un Rol por su nombre (útil para usuarios)
    // Spring Data JPA creará la consulta SQL automáticamente
    Optional<RolModel> findByNombre(String nombre);
}