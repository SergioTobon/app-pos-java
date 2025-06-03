// frontend/src/admin/adminCrud.js

const baseUrlCompras = 'http://localhost:8080/api/compras';

// Función para cargar los datos del dashboard (compras)
// DEBE ser una función global (adjunta a window) para que layout.js la pueda llamar
window.fetchDashboardData = async function() {
    const totalPurchasesCountElement = document.getElementById('totalPurchasesCount');
    const latestPurchasesTableBody = document.getElementById('latestPurchasesTableBody');
    const pendingPurchasesCountElement = document.getElementById('pendingPurchasesCount');

    // Comprobar que los elementos HTML existen antes de intentar manipularlos
    if (!totalPurchasesCountElement || !latestPurchasesTableBody || !pendingPurchasesCountElement) {
        console.warn("Elementos del dashboard no encontrados. Asegúrate de que dashboard.html se cargó correctamente.");
        return;
    }

    try {
        const response = await fetch(baseUrlCompras);
        if (!response.ok) {
            if (response.status === 204) { // No Content
                console.warn('No hay compras para mostrar.');
                latestPurchasesTableBody.innerHTML = '<tr><td colspan="5" class="text-center">No hay compras registradas.</td></tr>';
                totalPurchasesCountElement.textContent = '0';
                pendingPurchasesCountElement.textContent = '0';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const purchases = await response.json();
        
        latestPurchasesTableBody.innerHTML = ''; 
        if (purchases.length === 0) {
            latestPurchasesTableBody.innerHTML = '<tr><td colspan="5" class="text-center">No hay compras recientes.</td></tr>';
        } else {
            purchases.forEach(purchase => {
                const row = latestPurchasesTableBody.insertRow();
                row.innerHTML = `
                    <td>${purchase.id}</td>
                    <td>${purchase.nombreProveedor || 'N/A'}</td>
                    <td>${purchase.nombreProducto || 'N/A'}</td>
                    <td>${purchase.cantidad || 'N/A'}</td>
                    <td>${new Date(purchase.fechaCompra).toLocaleDateString() || 'N/A'}</td>
                `;
            });
        }

        totalPurchasesCountElement.textContent = purchases.length;
        // Lógica para compras pendientes (ejemplo simple)
        const pending = purchases.filter(p => p.estado === 'PENDIENTE').length; 
        pendingPurchasesCountElement.textContent = pending;

    } catch (error) {
        console.error('Error al obtener datos del dashboard (compras):', error);
        latestPurchasesTableBody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error al cargar compras.</td></tr>';
        totalPurchasesCountElement.textContent = 'N/A';
        pendingPurchasesCountElement.textContent = 'N/A';
    }
};

// No necesitas document.addEventListener('DOMContentLoaded') ni llamar a loadAdminLayout() aquí.
// layout.js se encarga de llamar a window.fetchDashboardData() cuando dashboard.html esté cargado.