// src/main/java/com/api/crud/repositories/ClienteRepository.java
package com.api.crud.repositories;

import com.api.crud.models.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.api.crud.dto.ClientFrequencyDTO; // Importa el nuevo DTO
import org.springframework.data.domain.Pageable; // Importa Pageable


@Repository
public interface ClienteRepository extends JpaRepository<ClienteModel, Integer> {

    Optional<ClienteModel> findByDni(String dni);

    // Query para obtener el total de clientes
    @Query("SELECT COUNT(c) FROM ClienteModel c")
    Long countTotalClientes();

    // Query para clientes más frecuentes (por número de ventas)
    @Query("SELECT new com.api.crud.dto.ClientFrequencyDTO(v.cliente.nombre, COUNT(v.idVenta)) " +
            "FROM VentaModel v " +
            "GROUP BY v.cliente.nombre " +
            "ORDER BY COUNT(v.idVenta) DESC")
    List<ClientFrequencyDTO> findMostFrequentClients(Pageable pageable);

    // Si prefieres por monto gastado, la query sería:
    /*
    @Query("SELECT new com.api.crud.dto.dashboard.ClientFrequencyDTO(v.cliente.nombre, SUM(v.total)) " +
           "FROM VentaModel v "+
           "GROUP BY v.cliente.nombre " +
           "ORDER BY SUM(v.total) DESC")
    List<ClientFrequencyDTO> findMostValuableClients(Pageable pageable);
    */
}