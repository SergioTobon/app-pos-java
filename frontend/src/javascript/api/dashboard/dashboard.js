// Asegúrate de que este script se cargue después de Chart.js en tu HTML.



// Puedes poner esto en un archivo nuevo llamado dashboard.js

// o integrarlo en tu archivo principal de JavaScript si es pequeño.



// --- API Configuration ---

const API_BASE_URL = 'http://localhost:8080/api'; // URL base de tu API

const API_DASHBOARD_URL = `${API_BASE_URL}/dashboard`; // URL base para los endpoints del dashboard



// --- Functions to load dashboard metrics ---

async function cargarDashboardMetrics() {

    // Referencias a los elementos HTML

    const dashboardTotalProductos = document.getElementById('dashboardTotalProductos');

    const dashboardTotalClientes = document.getElementById('dashboardTotalClientes');

    const dashboardNumVentas = document.getElementById('dashboardNumVentas');

    const dashboardTotalCompras = document.getElementById('dashboardTotalCompras');



    try {

        // Hacemos UNA SOLA llamada al endpoint que devuelve TODAS las métricas

        const response = await fetch(`${API_DASHBOARD_URL}/metrics`);



        // Verificamos si la respuesta es exitosa

        if (!response.ok) {

            // Si la respuesta no es 200 OK, lanzamos un error

            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);

        }



        const metrics = await response.json(); // Parseamos la respuesta JSON



        // Actualizamos los elementos HTML con los datos recibidos

        // Asegúrate de que los nombres de las propiedades (totalProductos, totalClientes, etc.)

        // coincidan exactamente con las del DashboardMetricsDTO en tu backend.

        dashboardTotalProductos.textContent = metrics.totalProductos || 0;

        dashboardTotalClientes.textContent = metrics.totalClientes || 0;

        dashboardNumVentas.textContent = metrics.numVentasUltimos30Dias || 0;



        // Formato de moneda para totalCompras

        const currencyFormatter = new Intl.NumberFormat('es-CO', {

            style: 'currency',

            currency: 'COP',

            minimumFractionDigits: 2,

            maximumFractionDigits: 2

        });

        dashboardTotalCompras.textContent = currencyFormatter.format(metrics.totalComprasUltimos30Dias || 0);



    } catch (error) {

        console.error('Error al cargar las métricas del dashboard:', error);

        // Si tienes una función para mostrar toasts/mensajes al usuario

        // showToast('Error al cargar las métricas del dashboard.', 'danger');

    }

}



// --- Functions to render charts ---



// Gráfico de Productos Más Vendidos

async function renderTopSellingProductsChart(limit = 5) { // Añade un límite por defecto

    try {

        const response = await fetch(`${API_DASHBOARD_URL}/topSellingProducts?limit=${limit}`);



        if (!response.ok) {

            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);

        }



        const data = await response.json();

        const chartElement = document.getElementById('topSellingProductsChart');

        const alertElement = chartElement.nextElementSibling; // Asume que el alert está inmediatamente después del canvas



        // Si no hay datos, mostrar mensaje en lugar de gráfico

        if (!data || data.length === 0) {

            chartElement.style.display = 'none';

            alertElement.classList.remove('d-none');

            alertElement.textContent = 'No hay datos de productos más vendidos aún.';

            return;

        } else {

            chartElement.style.display = 'block';

            alertElement.classList.add('d-none');

        }



        const labels = data.map(item => item.nombreProducto);

        const quantities = data.map(item => item.cantidadVendida);



        const ctx = chartElement.getContext('2d');



        // Destruir el gráfico existente si lo hay para evitar superposiciones

        if (window.topSellingProductsChartInstance) {

            window.topSellingProductsChartInstance.destroy();

        }



        window.topSellingProductsChartInstance = new Chart(ctx, {

            type: 'bar',

            data: {

                labels: labels,

                datasets: [{

                    label: 'Cantidad Vendida',

                    data: quantities,

                    backgroundColor: 'rgba(75, 192, 192, 0.6)',

                    borderColor: 'rgba(75, 192, 192, 1)',

                    borderWidth: 1

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                scales: {

                    y: {

                        beginAtZero: true,

                        title: {

                            display: true,

                            text: 'Cantidad Vendida'

                        }

                    }

                },

                plugins: {

                    legend: {

                        display: false

                    }

                }

            }

        });

    } catch (error) {

        console.error('Error al renderizar el gráfico de productos más vendidos:', error);

        const chartElement = document.getElementById('topSellingProductsChart');

        const alertElement = chartElement.nextElementSibling;

        alertElement.classList.remove('d-none');

        alertElement.textContent = 'Error al cargar los datos del gráfico.';

    }

}



// Gráfico de Clientes Más Frecuentes

async function renderMostFrequentClientsChart(limit = 5) { // Añade un límite por defecto

    try {

        const response = await fetch(`${API_DASHBOARD_URL}/mostFrequentClients?limit=${limit}`);



        if (!response.ok) {

            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);

        }



        const data = await response.json();

        const chartElement = document.getElementById('mostFrequentClientsChart');

        const alertElement = chartElement.nextElementSibling;



        // Si no hay datos, mostrar mensaje en lugar de gráfico

        if (!data || data.length === 0) {

            chartElement.style.display = 'none';

            alertElement.classList.remove('d-none');

            alertElement.textContent = 'No hay datos de clientes más frecuentes aún.';

            return;

        } else {

            chartElement.style.display = 'block';

            alertElement.classList.add('d-none');

        }



        const labels = data.map(item => item.nombreCliente);

        const values = data.map(item => item.numCompras);



        const ctx = chartElement.getContext('2d');



        // Destruir el gráfico existente si lo hay

        if (window.mostFrequentClientsChartInstance) {

            window.mostFrequentClientsChartInstance.destroy();

        }



        window.mostFrequentClientsChartInstance = new Chart(ctx, {

            type: 'bar',

            data: {

                labels: labels,

                datasets: [{

                    label: 'Número de Compras',

                    data: values,

                    backgroundColor: 'rgba(153, 102, 255, 0.6)',

                    borderColor: 'rgba(153, 102, 255, 1)',

                    borderWidth: 1

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                scales: {

                    y: {

                        beginAtZero: true,

                        title: {

                            display: true,

                            text: 'Número de Compras'

                        }

                    }

                },

                plugins: {

                    legend: {

                        display: false

                    }

                }

            }

        });

    } catch (error) {

        console.error('Error al renderizar el gráfico de clientes más frecuentes:', error);

        const chartElement = document.getElementById('mostFrequentClientsChart');

        const alertElement = chartElement.nextElementSibling;

        alertElement.classList.remove('d-none');

        alertElement.textContent = 'Error al cargar los datos del gráfico.';

    }

}



async function renderSalesByDayChart() {

    try {

        const response = await fetch(`${API_DASHBOARD_URL}/salesByDayLast7Days`);

        if (!response.ok) {

            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);

        }

        const data = await response.json();

        const chartElement = document.getElementById('salesByDayChart');

        const alertElement = chartElement.nextElementSibling;



        if (!data || data.length === 0) {

            chartElement.style.display = 'none';

            alertElement.classList.remove('d-none');

            alertElement.textContent = 'No hay datos de ventas por día para los últimos 7 días.';

            return;

        } else {

            chartElement.style.display = 'block';

            alertElement.classList.add('d-none');

        }



        // Sort data by date to ensure chronological order

        data.sort((a, b) => new Date(a.fecha) - new Date(b.fecha));



        const labels = data.map(item => {

            const date = new Date(item.fecha);

            return date.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric', month: 'short' });

        });

        const salesTotals = data.map(item => item.totalVentas);



        const ctx = chartElement.getContext('2d');

        if (window.salesByDayChartInstance) {

            window.salesByDayChartInstance.destroy();

        }



        window.salesByDayChartInstance = new Chart(ctx, {

            type: 'line', // Line chart is good for time-series data

            data: {

                labels: labels,

                datasets: [{

                    label: 'Total de Ventas',

                    data: salesTotals,

                    backgroundColor: 'rgba(54, 162, 235, 0.6)',

                    borderColor: 'rgba(54, 162, 235, 1)',

                    borderWidth: 2,

                    fill: true,

                    tension: 0.3 // Makes the line a bit smoother

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                scales: {

                    y: {

                        beginAtZero: true,

                        title: {

                            display: true,

                            text: 'Total de Ventas'

                        }

                    },

                    x: {

                        title: {

                            display: true,

                            text: 'Día'

                        }

                    }

                },

                plugins: {

                    legend: {

                        display: true

                    }

                }

            }

        });

    } catch (error) {

        console.error('Error al renderizar el gráfico de ventas por día:', error);

        const chartElement = document.getElementById('salesByDayChart');

        const alertElement = chartElement.nextElementSibling;

        alertElement.classList.remove('d-none');

        alertElement.textContent = 'Error al cargar los datos del gráfico de ventas diarias.';

    }

}



async function renderPurchasesByDayChart() {

    try {

        const response = await fetch(`${API_DASHBOARD_URL}/purchasesByDayLast7Days`);

        if (!response.ok) {

            throw new Error(`Error HTTP: ${response.status} - ${response.statusText}`);

        }

        const data = await response.json();

        const chartElement = document.getElementById('purchasesByDayChart');

        const alertElement = chartElement.nextElementSibling;



        if (!data || data.length === 0) {

            chartElement.style.display = 'none';

            alertElement.classList.remove('d-none');

            alertElement.textContent = 'No hay datos de compras por día para los últimos 7 días.';

            return;

        } else {

            chartElement.style.display = 'block';

            alertElement.classList.add('d-none');

        }



        // Sort data by date to ensure chronological order

        data.sort((a, b) => new Date(a.fecha) - new Date(b.fecha));



        const labels = data.map(item => {

            const date = new Date(item.fecha);

            return date.toLocaleDateString('es-ES', { weekday: 'short', day: 'numeric', month: 'short' });

        });

        const purchaseTotals = data.map(item => item.totalCompras);



        const ctx = chartElement.getContext('2d');

        if (window.purchasesByDayChartInstance) {

            window.purchasesByDayChartInstance.destroy();

        }



        window.purchasesByDayChartInstance = new Chart(ctx, {

            type: 'bar', // Bar chart can also be effective here

            data: {

                labels: labels,

                datasets: [{

                    label: 'Total de Compras',

                    data: purchaseTotals,

                    backgroundColor: 'rgba(255, 159, 64, 0.6)',

                    borderColor: 'rgba(255, 159, 64, 1)',

                    borderWidth: 1

                }]

            },

            options: {

                responsive: true,

                maintainAspectRatio: false,

                scales: {

                    y: {

                        beginAtZero: true,

                        title: {

                            display: true,

                            text: 'Total de Compras'

                        }

                    },

                    x: {

                        title: {

                            display: true,

                            text: 'Día'

                        }

                    }

                },

                plugins: {

                    legend: {

                        display: true

                    }

                }

            }

        });

    } catch (error) {

        console.error('Error al renderizar el gráfico de compras por día:', error);

        const chartElement = document.getElementById('purchasesByDayChart');

        const alertElement = chartElement.nextElementSibling;

        alertElement.classList.remove('d-none');

        alertElement.textContent = 'Error al cargar los datos del gráfico de compras diarias.';

    }

}



// --- Initialization (Llamadas a las funciones al cargar el DOM) ---

document.addEventListener('DOMContentLoaded', function() {

    // Estas funciones se ejecutan al cargar el DOM.

    cargarDashboardMetrics();

    renderTopSellingProductsChart();

    renderMostFrequentClientsChart();

    renderSalesByDayChart();

    renderPurchasesByDayChart();

});