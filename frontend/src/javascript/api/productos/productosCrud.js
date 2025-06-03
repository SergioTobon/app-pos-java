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

    const modalTitle = document.getElementById('productoModalLabel');
    const btnAbrirModalAgregarProducto = document.getElementById('btnAbrirModalAgregarProducto');
    const noProductosMessage = document.getElementById('noProductosMessage');

    console.log('--- Debugging productos.js ---');
    console.log('productosTableBody obtenido:', productosTableBody);
    console.log('noProductosMessage obtenido:', document.getElementById('noProductosMessage'));

    // ¡NUEVO! Creador de formato de números para precios
    const priceFormatter = new Intl.NumberFormat('en-US', { // Usamos 'en-US' para el formato 100,000.00
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });


    // Helper function to get the Bootstrap modal instance for products
    function getProductoModalInstance() {
        const modalElement = document.getElementById('productoModal');
        if (modalElement) {
            return bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        }
        console.error("El elemento del modal #productoModal no fue encontrado.");
        return null;
    }

    function showToast(message, type = 'success') {
        console.log(`Mensaje (${type}): ${message}`);
        // Aquí podrías integrar un sistema de Toast de Bootstrap o una alerta simple
        // const toastElement = document.getElementById('myToast'); // si tienes un toast
        // const toastBody = toastElement.querySelector('.toast-body');
        // toastBody.textContent = message;
        // const toast = new bootstrap.Toast(toastElement);
        // toast.show();
    }

    async function cargarProveedoresEnSelect() {
        try {
            const response = await fetch(API_PROVEEDORES_URL);
            if (!response.ok) {
                throw new Error(`Error al cargar proveedores: ${response.statusText}`);
            }
            const proveedores = await response.json();

            idProveedorProductoSelect.innerHTML = '<option value="">Seleccione un proveedor</option>';

            proveedores.forEach(proveedor => {
                const option = document.createElement('option');
                option.value = proveedor.id;
                option.textContent = proveedor.nombre;
                idProveedorProductoSelect.appendChild(option);
            });
        } catch (error) {
            console.error('Error al cargar proveedores para el select:', error);
            showToast('Error al cargar la lista de proveedores.', 'danger');
        }
    }


    function limpiarFormulario() {
        productoIdInput.value = '';
        nombreProductoInput.value = '';
        stockProductoInput.value = '';
        descripcionProductoInput.value = '';
        precioCompraProductoInput.value = '';
        porcentajeGananciaInput.value = '20';

        idProveedorProductoSelect.value = '';

        modalTitle.textContent = 'Agregar Nuevo Producto';
    }

    async function cargarProductos() {
        console.log('>>> Iniciando carga de productos...');
        try {
            const response = await fetch(API_BASE_URL);
            console.log('>>> fetch completado. Response Status:', response.status);
            
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`HTTP error! status: ${response.status} - ${errorText}`);
            }

            const productos = await response.json();
            console.log('>>> Datos de productos recibidos y parseados (en cargarProductos):', productos);

            if (productosTableBody) {
                productosTableBody.innerHTML = '';
            } else {
                console.error('ERROR: #productosTableBody no fue encontrado en el DOM para renderizar productos.');
                return;
            }
            
            if (productos.length === 0) {
                console.log('>>> No hay productos. Mostrando mensaje de no-productos.');
                noProductosMessage.classList.remove('d-none');
            } else {
                console.log('>>> Productos encontrados. Ocultando mensaje de no-productos y renderizando tabla.');
                noProductosMessage.classList.add('d-none');
                productos.forEach(producto => {
                    console.log(">>> Procesando producto para añadir a la tabla:", producto);
                    const row = productosTableBody.insertRow();
                    row.innerHTML = `
                        <td>${producto.id}</td>
                        <td>${producto.nombre}</td>
                        <td>${producto.stock}</td>
                        <td>${producto.descripcion || ''}</td>
                        <td>${producto.precioCompra ? priceFormatter.format(producto.precioCompra) : ''}</td>
                        <td>${producto.precioVenta ? priceFormatter.format(producto.precioVenta) : ''}</td>
                        <td>${producto.nombreProveedor || producto.idProveedor || ''}</td>
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
                console.log('>>> Renderezado de tabla completado para productos.');
            }
        } catch (error) {
            console.error('>>> Error CRÍTICO al cargar productos:', error);
            showToast(`Error al cargar productos: ${error.message}`, 'danger');
        }
    }

    btnAbrirModalAgregarProducto.addEventListener('click', () => {
        limpiarFormulario();
        cargarProveedoresEnSelect();
        const productoModal = getProductoModalInstance();
        if (productoModal) {
            productoModal.show();
        }
    });

    productoForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const id = productoIdInput.value;
        const precioCompra = parseFloat(precioCompraProductoInput.value);
        const porcentajeGanancia = parseFloat(porcentajeGananciaInput.value);

        if (isNaN(precioCompra) || precioCompra < 0) {
            showToast('Por favor, ingrese un Precio de Compra válido.', 'danger');
            return;
        }
        if (isNaN(porcentajeGanancia) || porcentajeGanancia < 0) {
            showToast('Por favor, ingrese un Porcentaje de Ganancia válido.', 'danger');
            return;
        }

        if (!idProveedorProductoSelect.value) {
            showToast('Por favor, seleccione un proveedor.', 'danger');
            return;
        }

        const precioVentaCalculado = precioCompra * (1 + (porcentajeGanancia / 100));

        const productoData = {
            nombre: nombreProductoInput.value,
            stock: parseInt(stockProductoInput.value),
            descripcion: descripcionProductoInput.value,
            precioCompra: precioCompra,
            precioVenta: precioVentaCalculado,
            idProveedor: parseInt(idProveedorProductoSelect.value) 
        };

        try {
            let response;
            if (id) {
                response = await fetch(`${API_BASE_URL}/${id}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productoData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al actualizar producto: ${response.status} - ${errorText}`);
                }
                showToast('Producto actualizado exitosamente.');
            } else {
                response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(productoData)
                });
                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al agregar producto: ${response.status} - ${errorText}`);
                }
                showToast('Producto agregado exitosamente.');
            }

            const productoModal = getProductoModalInstance();
            if (productoModal) {
                productoModal.hide();
            }
            limpiarFormulario();
            cargarProductos();
        } catch (error) {
            console.error('Error al guardar producto:', error);
            showToast(`Error al guardar producto: ${error.message}`, 'danger');
        }
    });

    productosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-editar-producto') || event.target.closest('.btn-editar-producto')) {
            const id = event.target.dataset.id || event.target.closest('.btn-editar-producto').dataset.id;
            try {
                const response = await fetch(`${API_BASE_URL}/${id}`);
                if (!response.ok) {
                    throw new Error(`Error al obtener producto: ${response.statusText}`);
                }
                const producto = await response.json();
                
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

                await cargarProveedoresEnSelect();
                idProveedorProductoSelect.value = producto.idProveedor;
                
                modalTitle.textContent = 'Editar Producto';
                const productoModal = getProductoModalInstance();
                if (productoModal) {
                    productoModal.show();
                }
            } catch (error) {
                console.error('Error al cargar datos del producto para edición:', error);
                showToast('Error al cargar datos del producto para edición.', 'danger');
            }
        }
    });

    productosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-eliminar-producto') || event.target.closest('.btn-eliminar-producto')) {
            const id = event.target.dataset.id || event.target.closest('.btn-eliminar-producto').dataset.id;
            if (confirm(`¿Estás seguro de que quieres eliminar el producto con ID ${id}?`)) {
                try {
                    const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
                    if (!response.ok) {
                        const errorText = await response.text();
                        throw new Error(`Error al eliminar producto: ${response.status} - ${errorText}`);
                    }
                    showToast('Producto eliminado exitosamente.');
                    cargarProductos();
                } catch (error) {
                    console.error('Error al eliminar producto:', error);
                    showToast('Error al eliminar producto.', 'danger');
                }
            }
        }
    });

    console.log('>>> Llamando a cargarProductos() desde initProductosCRUD (final).');
    cargarProductos();

}