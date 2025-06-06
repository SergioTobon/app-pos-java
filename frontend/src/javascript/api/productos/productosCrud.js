// FRONTEND/src/javascript/api/productos/productosCrud.js

function initProductosCRUD() {

    const API_BASE_URL = 'http://localhost:8080/api/productos';
    const API_PROVEEDORES_URL = 'http://localhost:8080/api/proveedores';

    const productosTableBody = document.getElementById('productosTableBody');

    const productoForm = document.getElementById('productoForm');
    const productoIdInput = document.getElementById('productoId');
    const nombreProductoInput = document.getElementById('nombreProducto');
    const stockProductoInput = document.getElementById('stockProducto');
    const descripcionProductoInput = document.getElementById('descripcionProducto');
    const precioCompraProductoInput = document.getElementById('precioCompraProducto');
    const porcentajeGananciaInput = document.getElementById('porcentajeGanancia');
    const idProveedorProductoSelect = document.getElementById('idProveedorProductoSelect');

    // Referencias a los contenedores de los campos para ocultar/mostrar
    // Asegúrate de que estos selectores (.closest('.mb-3')) coincidan con la estructura HTML de tu modal
    const stockGroup = stockProductoInput.closest('.mb-3'); 
    const precioCompraGroup = precioCompraProductoInput.closest('.mb-3');
    const porcentajeGananciaGroup = porcentajeGananciaInput.closest('.mb-3');
    const proveedorGroup = idProveedorProductoSelect.closest('.mb-3');


    const modalTitle = document.getElementById('productoModalLabel');
    const btnAbrirModalAgregarProducto = document.getElementById('btnAbrirModalAgregarProducto');
    const noProductosMessage = document.getElementById('noProductosMessage');

    // --- NUEVOS ELEMENTOS PARA EL MODAL DE DETALLE ---
    const productoDetalleModalElement = document.getElementById('productoDetalleModal');
    const productoDetalleModalLabel = document.getElementById('productoDetalleModalLabel'); // Cambiado de productoDetalleModalTitle
    const detalleNombre = document.getElementById('detalleNombre');
    const detalleStock = document.getElementById('detalleStock');
    const detalleDescripcion = document.getElementById('detalleDescripcion');
    const detallePrecioCompra = document.getElementById('detallePrecioCompra');
    const detallePrecioVenta = document.getElementById('detallePrecioVenta');
    const detalleProveedor = document.getElementById('detalleProveedor');
    // REMOVIDO: const detalleEstado = document.getElementById('detalleEstado'); // Este ya no es necesario
    // --- FIN NUEVOS ELEMENTOS ---

    // --- NUEVOS ELEMENTOS PARA FILTRO Y BÚSQUEDA ---
    const searchProductoInput = document.getElementById('searchProductoInput');
    const filtroStockSelect = document.getElementById('filtroStockSelect');
    let allProductsData = []; // Variable para almacenar todos los productos obtenidos del backend
    // --- FIN NUEVOS ELEMENTOS ---


    console.log('--- Debugging productos.js ---');
    console.log('productosTableBody obtenido:', productosTableBody);
    console.log('noProductosMessage obtenido:', document.getElementById('noProductosMessage'));

    // Creador de formato de números para precios
    const priceFormatter = new Intl.NumberFormat('es-CO', { // Usamos 'es-CO' para el formato colombiano (miles con punto, decimales con coma)
        style: 'currency',
        currency: 'COP', // O la moneda que uses
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    // Simulando un token de autorización (¡En un caso real, esto vendría de tu proceso de login!)
    const AUTH_TOKEN = 'tu_super_token_secreto_aqui'; // Reemplaza esto con un token real

    // Helper function to get the Bootstrap modal instance for product FORM
    function getProductoModalInstance() {
        const modalElement = document.getElementById('productoModal');
        if (modalElement) {
            return bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        }
        console.error("El elemento del modal #productoModal no fue encontrado.");
        return null;
    }

    // Helper function to get the Bootstrap modal instance for product DETAIL
    function getProductoDetalleModalInstance() {
        if (productoDetalleModalElement) {
            return bootstrap.Modal.getInstance(productoDetalleModalElement) || new bootstrap.Modal(productoDetalleModalElement);
        }
        console.error("El elemento del modal #productoDetalleModal no fue encontrado.");
        return null;
    }

    function showToast(message, type = 'success') {
        console.log(`Mensaje (${type}): ${message}`);
        const toastContainer = document.getElementById('toastContainer');
        if (!toastContainer) {
            console.error("No se encontró el contenedor de toasts. No se puede mostrar el mensaje.");
            return;
        }

        const toastId = `toast-${Date.now()}`;
        const toastHtml = `
            <div class="toast align-items-center text-white bg-${type} border-0" role="alert" aria-live="assertive" aria-atomic="true" id="${toastId}">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `;
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        const newToast = document.getElementById(toastId);
        const bsToast = new bootstrap.Toast(newToast, { delay: 3000 }); // Duración de 3 segundos
        bsToast.show();

        newToast.addEventListener('hidden.bs.toast', function () {
            newToast.remove();
        });
    }

    // Carga los proveedores en el select para asociarlos a un producto
    async function cargarProveedoresEnSelect() {
        try {
            const response = await fetch(API_PROVEEDORES_URL, {
                headers: { 'Authorization': `Bearer ${AUTH_TOKEN}` }
            });
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al cargar proveedores: ${response.status} - ${errorText}`);
            }
            const proveedores = await response.json();

            idProveedorProductoSelect.innerHTML = '<option value="">Seleccione un proveedor principal</option>';

            proveedores.forEach(proveedor => {
                const option = document.createElement('option');
                option.value = proveedor.id;
                option.textContent = proveedor.nombre;
                idProveedorProductoSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error al cargar proveedores para el select:', error);
            showToast(`Error al cargar la lista de proveedores: ${error.message}`, 'danger');
        }
    }

    // Limpia el formulario de productos
    function limpiarFormulario() {
        productoIdInput.value = '';
        nombreProductoInput.value = '';
        stockProductoInput.value = '';
        descripcionProductoInput.value = '';
        precioCompraProductoInput.value = '';
        porcentajeGananciaInput.value = '20'; // Valor predeterminado
        idProveedorProductoSelect.value = '';
        modalTitle.textContent = 'Agregar Nuevo Producto al Catálogo';

        // Ocultar campos específicos para "Agregar" si existen en el DOM
        if (stockGroup) stockGroup.classList.add('d-none');
        if (precioCompraGroup) precioCompraGroup.classList.add('d-none');
        if (porcentajeGananciaGroup) porcentajeGananciaGroup.classList.add('d-none');
        if (proveedorGroup) proveedorGroup.classList.add('d-none');
    }

    // Muestra todos los campos del formulario (para "Editar")
    function mostrarTodosLosCamposFormulario() {
        if (stockGroup) stockGroup.classList.remove('d-none');
        if (precioCompraGroup) precioCompraGroup.classList.remove('d-none');
        if (porcentajeGananciaGroup) porcentajeGananciaGroup.classList.remove('d-none');
        if (proveedorGroup) proveedorGroup.classList.remove('d-none');
    }


    // Renderiza la tabla de productos basada en los datos filtrados y buscados
    function renderizarTablaProductos(productos) {
        if (productosTableBody) {
            productosTableBody.innerHTML = '';
        } else {
            console.error('ERROR: #productosTableBody no fue encontrado en el DOM para renderizar productos.');
            return;
        }

        if (productos.length === 0) {
            console.log('>>> No hay productos para mostrar después de aplicar filtros/búsqueda. Mostrando mensaje.');
            noProductosMessage.classList.remove('d-none');
        } else {
            console.log('>>> Productos encontrados. Ocultando mensaje y renderizando tabla.');
            noProductosMessage.classList.add('d-none');
            productos.forEach(producto => {
                const row = productosTableBody.insertRow();
                row.classList.add('clickable-row');
                row.dataset.id = producto.id;

                row.innerHTML = `
                    <td>${producto.id}</td>
                    <td>${producto.nombre}</td>
                    <td>${producto.stock}</td>
                    <td>${producto.descripcion || ''}</td>
                    <td>${producto.precioCompra ? priceFormatter.format(producto.precioCompra) : ''}</td>
                    <td>${producto.precioVenta ? priceFormatter.format(producto.precioVenta) : ''}</td>
                    <td>
                        <button class="btn btn-sm btn-info btn-editar-producto me-2" data-id="${producto.id}">
                            <i class="bi bi-pencil-square"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-danger btn-eliminar-producto" data-id="${producto.id}">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
                    </td>
                `;
            });
            console.log('>>> Renderezado de tabla completado para productos del catálogo.');
        }
    }

    // Aplica el filtro y la búsqueda a los datos de productos y renderiza la tabla
    function aplicarFiltrosYBusqueda() {
        let productosFiltrados = [...allProductsData]; // Trabaja con una copia de todos los productos

        const searchTerm = searchProductoInput.value.toLowerCase().trim();
        const stockFilter = filtroStockSelect.value;

        // Aplicar búsqueda
        if (searchTerm) {
            productosFiltrados = productosFiltrados.filter(producto =>
                producto.nombre.toLowerCase().includes(searchTerm) ||
                (producto.descripcion && producto.descripcion.toLowerCase().includes(searchTerm))
            );
        }

        // Aplicar filtro de stock
        if (stockFilter === 'conStock') {
            productosFiltrados = productosFiltrados.filter(producto => producto.stock > 0);
        } else if (stockFilter === 'sinStock') {
            productosFiltrados = productosFiltrados.filter(producto => producto.stock === 0);
        }

        renderizarTablaProductos(productosFiltrados);
    }

    // Carga y muestra los productos en la tabla
    async function cargarProductos() {
        console.log('>>> Iniciando carga de productos del catálogo...');
        try {
            const response = await fetch(API_BASE_URL, {
                headers: { 'Authorization': `Bearer ${AUTH_TOKEN}` }
            });
            console.log('>>> fetch completado. Response Status:', response.status);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status} - ${errorText}`);
            }

            const productos = await response.json();
            console.log('>>> Datos de productos recibidos y parseados (en cargarProductos):', productos);

            // Almacenar todos los productos obtenidos
            allProductsData = productos;

            // Aplicar filtros y búsqueda iniciales
            aplicarFiltrosYBusqueda();

        } catch (error) {
            console.error('>>> Error CRÍTICO al cargar productos del catálogo:', error);
            showToast(`Error al cargar productos del catálogo: ${error.message}`, 'danger');
            if (error.message.includes('401')) {
                // window.location.href = '/login.html'; // Ejemplo de redirección
            }
        }
    }

    // Event listener para abrir el modal de agregar producto
    btnAbrirModalAgregarProducto.addEventListener('click', () => {
        limpiarFormulario(); // Esto ahora oculta los campos no deseados al crear
        // No es necesario cargar proveedores si el campo no se va a mostrar
        const productoModal = getProductoModalInstance();
        if (productoModal) {
            productoModal.show();
        }
    });

    // Event listener para enviar el formulario de producto (agregar/editar)
    productoForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const id = productoIdInput.value;
        const nombre = nombreProductoInput.value;
        const descripcion = descripcionProductoInput.value;

        // Validaciones básicas para nombre
        if (!nombre.trim()) {
            showToast('Por favor, ingrese un nombre para el producto.', 'danger');
            return;
        }

        let productoData = {
            nombre: nombre.trim(),
            descripcion: descripcion.trim() || null // Envía null si está vacío
        };

        try {
            let response;
            if (id) {
                // Es una ACTUALIZACIÓN (PUT): se envían todos los campos y se validan
                const precioCompra = parseFloat(precioCompraProductoInput.value);
                const porcentajeGanancia = parseFloat(porcentajeGananciaInput.value);
                const stockInicial = parseInt(stockProductoInput.value);
                const idProveedor = parseInt(idProveedorProductoSelect.value);

                if (isNaN(precioCompra) || precioCompra < 0) {
                    showToast('Para actualizar, ingrese un Precio de Compra de referencia válido (no negativo).', 'danger');
                    return;
                }
                if (isNaN(porcentajeGanancia) || porcentajeGanancia < 0) {
                    showToast('Para actualizar, ingrese un Porcentaje de Ganancia válido (no negativo).', 'danger');
                    return;
                }
                if (isNaN(stockInicial) || stockInicial < 0) {
                    showToast('Para actualizar, ingrese un Stock válido (no negativo).', 'danger');
                    return;
                }
                if (!idProveedorProductoSelect.value) {
                    showToast('Para actualizar, seleccione un proveedor principal para el producto.', 'danger');
                    return;
                }

                const precioVentaCalculado = precioCompra * (1 + (porcentajeGanancia / 100));

                productoData = {
                    ...productoData, // Mantener nombre y descripción
                    stock: stockInicial,
                    precioCompra: precioCompra,
                    precioVenta: precioVentaCalculado,
                    idProveedor: idProveedor
                };

                response = await fetch(`${API_BASE_URL}/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${AUTH_TOKEN}`
                    },
                    body: JSON.stringify(productoData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al actualizar producto: ${response.status} - ${errorText}`);
                }
                showToast('Producto del catálogo actualizado exitosamente.');
            } else {
                // Es una CREACIÓN (POST): Solo se envían nombre y descripción
                response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${AUTH_TOKEN}`
                    },
                    body: JSON.stringify(productoData) // Solo nombre y descripción
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al agregar producto: ${response.status} - ${errorText}`);
                }
                showToast('Producto agregado al catálogo exitosamente.');
            }

            const productoModal = getProductoModalInstance();
            if (productoModal) {
                productoModal.hide(); // Cierra el modal
            }
            limpiarFormulario(); // Limpia el formulario (y lo prepara para la próxima creación)
            cargarProductos(); // Vuelve a cargar la tabla para reflejar los cambios
        } catch (error) {
            console.error('Error al guardar/actualizar producto:', error);
            showToast(`Error al guardar/actualizar producto: ${error.message}`, 'danger');
        }
    });

    // Event listener para editar un producto (abre el modal de formulario)
    productosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-editar-producto') || event.target.closest('.btn-editar-producto')) {
            const id = event.target.dataset.id || event.target.closest('.btn-editar-producto').dataset.id;
            try {
                const response = await fetch(`${API_BASE_URL}/${id}`, {
                    headers: { 'Authorization': `Bearer ${AUTH_TOKEN}` }
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al obtener producto para edición: ${response.status} - ${errorText}`);
                }
                const producto = await response.json();

                // Mostrar todos los campos para edición
                mostrarTodosLosCamposFormulario();
                // Cargar proveedores porque el campo ahora está visible para edición
                await cargarProveedoresEnSelect();
                
                // Rellena el formulario con los datos del producto
                productoIdInput.value = producto.id;
                nombreProductoInput.value = producto.nombre;
                stockProductoInput.value = producto.stock;
                descripcionProductoInput.value = producto.descripcion || '';
                precioCompraProductoInput.value = producto.precioCompra;

                if (producto.precioCompra > 0 && producto.precioVenta !== null) {
                    const calculatedPorcentaje = ((producto.precioVenta / producto.precioCompra) - 1) * 100;
                    porcentajeGananciaInput.value = calculatedPorcentaje.toFixed(2);
                } else {
                    porcentajeGananciaInput.value = '0';
                }
                
                idProveedorProductoSelect.value = producto.proveedorPrincipal ? producto.proveedorPrincipal.id : '';

                modalTitle.textContent = 'Editar Producto del Catálogo';
                const productoModal = getProductoModalInstance();
                if (productoModal) {
                    productoModal.show();
                }
            } catch (error) {
                console.error('Error al cargar datos del producto para edición:', error);
                showToast(`Error al cargar datos del producto para edición: ${error.message}`, 'danger');
            }
        }
    });

    // Event listener para mostrar detalles de un producto (al hacer clic en la fila)
    productosTableBody.addEventListener('click', async (event) => {
        // Asegúrate de que no se haga clic en los botones de editar/eliminar dentro de la fila
        if (event.target.closest('.btn-editar-producto') || event.target.closest('.btn-eliminar-producto')) {
            return; // Si se hizo clic en un botón, no activar el detalle de la fila
        }

        const row = event.target.closest('.clickable-row');
        if (row) {
            const id = row.dataset.id;
            try {
                const response = await fetch(`${API_BASE_URL}/${id}`, {
                    headers: { 'Authorization': `Bearer ${AUTH_TOKEN}` }
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al obtener detalles del producto: ${response.status} - ${errorText}`);
                }
                const producto = await response.json();

                // Rellenar el modal de detalle
                productoDetalleModalLabel.textContent = `Detalles de ${producto.nombre}`; // Cambiado de productoDetalleModalTitle
                detalleNombre.textContent = producto.nombre;
                detalleStock.textContent = producto.stock;
                detalleDescripcion.textContent = producto.descripcion || 'N/A';
                detallePrecioCompra.textContent = producto.precioCompra ? priceFormatter.format(producto.precioCompra) : 'N/A';
                detallePrecioVenta.textContent = producto.precioVenta ? priceFormatter.format(producto.precioVenta) : 'N/A';
                detalleProveedor.textContent = producto.proveedorPrincipal ? producto.proveedorPrincipal.nombre : 'N/A';
                // REMOVIDO: detalleEstado.textContent = producto.activo ? 'Activo' : 'Inactivo';

                const productoDetalleModal = getProductoDetalleModalInstance();
                if (productoDetalleModal) {
                    productoDetalleModal.show();
                }
            } catch (error) {
                console.error('Error al cargar detalles del producto:', error);
                showToast(`Error al cargar detalles del producto: ${error.message}`, 'danger');
            }
        }
    });

    // Event listener para ELIMINAR FÍSICAMENTE un producto
    productosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-eliminar-producto') || event.target.closest('.btn-eliminar-producto')) {
            const id = event.target.dataset.id || event.target.closest('.btn-eliminar-producto').dataset.id;
            // Confirmación para eliminación PERMANENTE
            if (confirm(`¿Estás seguro de que quieres ELIMINAR PERMANENTEMENTE el producto con ID ${id}? Esta acción no se puede deshacer y eliminará también los registros relacionados (ej. en detalles de pedidos).`)) {
                try {
                    // Se realiza una solicitud DELETE
                    const response = await fetch(`${API_BASE_URL}/${id}`, {
                        method: 'DELETE',
                        headers: {
                            'Authorization': `Bearer ${AUTH_TOKEN}`
                        }
                    });

                    if (!response.ok) {
                        const errorText = await response.text();
                        throw new Error(`Error al eliminar producto: ${response.status} - ${errorText}`);
                    }
                    showToast('Producto eliminado permanentemente.', 'success');
                    cargarProductos(); // Recarga la tabla para reflejar la eliminación
                } catch (error) {
                    console.error('Error al eliminar producto:', error);
                    let errorMessage = 'Error al eliminar producto.';
                    if (error.message.includes('401')) {
                        errorMessage += ' No autorizado. Por favor, inicie sesión.';
                    } else if (error.message.includes('foreign key constraint fails') || error.message.includes('violates foreign key constraint')) {
                        // Mensaje más específico si el backend devuelve este tipo de error
                        errorMessage += ' No se pudo eliminar el producto debido a referencias existentes en otros registros (ej. ventas). Considere primero eliminar esos registros o desvincular el producto.';
                    }
                    showToast(errorMessage, 'danger');
                }
            }
        }
    });

    // --- NUEVOS EVENT LISTENERS PARA FILTRO Y BÚSQUEDA ---
    searchProductoInput.addEventListener('input', aplicarFiltrosYBusqueda);
    filtroStockSelect.addEventListener('change', aplicarFiltrosYBusqueda);
    // --- FIN NUEVOS EVENT LISTENERS ---

    console.log('>>> Llamando a cargarProductos() desde initProductosCRUD (final).');
    cargarProductos(); // Carga inicial de productos al cargar la página
}