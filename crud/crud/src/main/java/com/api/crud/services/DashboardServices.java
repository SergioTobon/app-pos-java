// src/main/java/com/api/crud/services/DashboardService.java
package com.api.crud.services;

import com.api.crud.dto.ClientFrequencyDTO;
import com.api.crud.dto.DashboardMetricsDTO;
import com.api.crud.dto.ProductSalesDTO;
import com.api.crud.repositories.ClienteRepository;
import com.api.crud.repositories.CompraRepository;
import com.api.crud.repositories.ProductoRepository;
import com.api.crud.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class DashboardServices {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private CompraRepository compraRepository;

    public DashboardMetricsDTO getDashboardMetrics() {
        // Calcular fechas para los últimos 30 días
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minus(30, ChronoUnit.DAYS);

        // Obtener métricas individuales
        Long totalProductos = productoRepository.countTotalProductos();
        Long totalClientes = clienteRepository.countTotalClientes();
        Long numVentas = ventaRepository.countVentasBetweenDates(thirtyDaysAgo, now);
        Double totalCompras = compraRepository.sumTotalComprasBetweenDates(thirtyDaysAgo, now);

        // Asegurarse de que el total de compras no sea null si no hay compras
        if (Objects.isNull(totalCompras)) {
            totalCompras = 0.0;
        }

        return new DashboardMetricsDTO(totalProductos, totalClientes, numVentas, totalCompras);
    }

    public List<ProductSalesDTO> getTopSellingProducts(int limit) {
        // Usa PageRequest para limitar los resultados de la consulta
        return productoRepository.findTopSellingProducts(PageRequest.of(0, limit));
    }

    public List<ClientFrequencyDTO> getMostFrequentClients(int limit) {
        // Usa PageRequest para limitar los resultados de la consulta
        return clienteRepository.findMostFrequentClients(PageRequest.of(0, limit));
        // Si usaste findMostValuableClients en ClienteRepository, llama a ese método aquí
        // return clienteRepository.findMostValuableClients(PageRequest.of(0, limit));
    }
}