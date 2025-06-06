// src/main/java/com/api/crud/controllers/DashboardController.java
package com.api.crud.controllers;

import com.api.crud.dto.ClientFrequencyDTO;
import com.api.crud.dto.DashboardMetricsDTO;
import com.api.crud.dto.ProductSalesDTO;
import com.api.crud.services.DashboardServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin; // Importa CrossOrigin

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:5500") // O la URL de tu frontend (ej. "http://127.0.0.1:5500")
public class DashboardController {

    @Autowired
    private DashboardServices dashboardService;

    // Endpoint para obtener todas las métricas principales del dashboard
    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDTO> getDashboardMetrics() {
        DashboardMetricsDTO metrics = dashboardService.getDashboardMetrics();
        return ResponseEntity.ok(metrics);
    }

    // Endpoint para obtener los productos más vendidos
    // Permite especificar un límite (ej. top 5, top 10)
    @GetMapping("/topSellingProducts")
    public ResponseEntity<List<ProductSalesDTO>> getTopSellingProducts(@RequestParam(defaultValue = "5") int limit) {
        List<ProductSalesDTO> topProducts = dashboardService.getTopSellingProducts(limit);
        return ResponseEntity.ok(topProducts);
    }

    // Endpoint para obtener los clientes más frecuentes
    // Permite especificar un límite (ej. top 5, top 10)
    @GetMapping("/mostFrequentClients")
    public ResponseEntity<List<ClientFrequencyDTO>> getMostFrequentClients(@RequestParam(defaultValue = "5") int limit) {
        List<ClientFrequencyDTO> frequentClients = dashboardService.getMostFrequentClients(limit);
        return ResponseEntity.ok(frequentClients);
    }
}