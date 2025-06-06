package com.api.crud.repositories;

import com.api.crud.models.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioModel, Integer> {
    // Puedes añadir métodos personalizados si los necesitas, por ejemplo, para buscar por DNI o nombre
    Optional<UsuarioModel> findByDni(String dni);
    List<UsuarioModel> findByNombreContainingIgnoreCase(String nombre);
}