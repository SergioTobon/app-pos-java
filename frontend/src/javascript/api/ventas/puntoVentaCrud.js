// FRONTEND/src/javascript/api/ventas/puntoVenta.js

function initPuntoVenta() {
    const API_PRODUCTOS_URL = 'http://localhost:8080/api/productos';
    const API_CLIENTES_URL = 'http://localhost:8080/api/clientes';
    const API_USUARIOS_URL = 'http://localhost:8080/api/usuarios';
    const API_VENTAS_URL = 'http://localhost:8080/api/ventas';

    // Elementos del DOM para la lista de productos
    const productosVentaTableBody = document.getElementById('productosVentaTableBody');
    const noProductosVentaMessage = document.getElementById('noProductosVentaMessage');
    const searchProductoVentaInput = document.getElementById('searchProductoVenta');

    // Elementos del DOM para el carrito de compras
    const idClienteVentaSelect = document.getElementById('idClienteVentaSelect');
    const idUsuarioVentaSelect = document.getElementById('idUsuarioVentaSelect');
    const productosCarritoContainer = document.getElementById('productosCarritoContainer');
    const carritoVacioMessage = document.getElementById('carritoVacioMessage');
    const totalCarritoDisplay = document.getElementById('totalCarritoDisplay');
    const btnConfirmarVenta = document.getElementById('btnConfirmarVenta');
    const btnVaciarCarrito = document.getElementById('btnVaciarCarrito');
    const carritoVentaForm = document.getElementById('carritoVentaForm');

    let allProducts = []; // Para almacenar todos los productos disponibles (con su precio de compra)
    let carritoItems = []; // Para almacenar los productos añadidos al carrito

    // Formateador de números (para moneda COP y el formato "100.000,00")
    const currencyFormatter = new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    // Función de ayuda para mostrar mensajes (MODIFICADA)
    function showToast(message, type = 'success') {
        const toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) {
            console.error('No se encontró el contenedor de toasts.');
            alert(message); // Retornar a alert si el contenedor falta
            return;
        }

        const toastElement = document.createElement('div');
        // 'show' para que sea visible inmediatamente, 'fade' para la animación
        toastElement.classList.add('toast', 'align-items-center', 'border-0', 'fade', 'show');
        toastElement.setAttribute('role', 'alert');
        toastElement.setAttribute('aria-live', 'assertive');
        toastElement.setAttribute('aria-atomic', 'true');

        let bgColorClass = '';
        let textColorClass = 'text-white'; // Color de texto predeterminado para fondos oscuros
        switch (type) {
            case 'success':
                bgColorClass = 'bg-success';
                break;
            case 'danger':
                bgColorClass = 'bg-danger';
                break;
            case 'warning':
                bgColorClass = 'bg-warning';
                textColorClass = 'text-dark'; // Las advertencias suelen verse mejor con texto oscuro
                break;
            case 'info':
                bgColorClass = 'bg-info';
                break;
            default:
                bgColorClass = 'bg-secondary';
        }
        toastElement.classList.add(bgColorClass, textColorClass);

        toastElement.innerHTML = `
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Cerrar"></button>
            </div>
        `;

        toastContainer.appendChild(toastElement);

        const bsToast = new bootstrap.Toast(toastElement, {
            delay: 3000 // El toast desaparece después de 3 segundos
        });
        bsToast.show();

        // Eliminar el elemento toast del DOM después de que se oculte para mantenerlo limpio
        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    // --- Carga de datos para Selects (Clientes y Usuarios) ---

    async function cargarClientesEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_CLIENTES_URL);
            if (!response.ok) throw new Error(`Error al cargar clientes: ${response.statusText}`);
            const clientes = await response.json();

            idClienteVentaSelect.innerHTML = '<option value="">Seleccione un cliente</option>';
            clientes.forEach(cliente => {
                const option = document.createElement('option');
                option.value = cliente.id;
                option.textContent = cliente.nombre;
                idClienteVentaSelect.appendChild(option);
            });
            if (selectedId) {
                idClienteVentaSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar clientes en select:', error);
            showToast('Error al cargar la lista de clientes.', 'danger');
        }
    }

    async function cargarUsuariosEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_USUARIOS_URL);
            if (!response.ok) throw new Error(`Error al cargar usuarios: ${response.statusText}`);
            const usuarios = await response.json();

            idUsuarioVentaSelect.innerHTML = '<option value="">Seleccione un usuario</option>';
            usuarios.forEach(usuario => {
                const option = document.createElement('option');
                option.value = usuario.id;
                option.textContent = usuario.nombre;
                idUsuarioVentaSelect.appendChild(option);
            });
            if (selectedId) {
                idUsuarioVentaSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar usuarios en select:', error);
            showToast('Error al cargar la lista de usuarios.', 'danger');
        }
    }

    // --- Funciones para la Lista de Productos Disponibles ---

    async function cargarProductosDisponibles(searchTerm = '') {
        try {
            const response = await fetch(API_PRODUCTOS_URL);
            if (!response.ok) throw new Error(`Error al cargar productos: ${response.statusText}`);
            allProducts = await response.json(); // Guarda todos los productos

            renderProductosDisponibles(allProducts, searchTerm);
        } catch (error) {
            console.error('Error al cargar productos disponibles:', error);
            showToast('Error al cargar la lista de productos disponibles.', 'danger');
        }
    }

    function renderProductosDisponibles(productsToRender, searchTerm = '') {
        productosVentaTableBody.innerHTML = '';
        const filteredProducts = productsToRender.filter(p =>
            p.nombre.toLowerCase().includes(searchTerm.toLowerCase())
        );

        if (filteredProducts.length === 0) {
            noProductosVentaMessage.classList.remove('d-none');
        } else {
            noProductosVentaMessage.classList.add('d-none');
            filteredProducts.forEach(producto => {
                const row = productosVentaTableBody.insertRow();
                row.innerHTML = `
                    <td>${producto.id}</td>
                    <td>${producto.nombre}</td>
                    <td>${producto.stock}</td>
                    <td>${currencyFormatter.format(producto.precioVenta)}</td>
                    <td>
                        <button class="btn btn-sm btn-primary btn-add-to-cart" data-id="${producto.id}"
                            ${producto.stock === 0 ? 'disabled' : ''}>
                            <i class="bi bi-cart-plus"></i> Añadir
                        </button>
                    </td>
                `;
            });
        }
    }

    // --- Funciones para el Carrito de Compras ---

    function addProductoToCart(productId) {
        const productToAdd = allProducts.find(p => p.id === productId);

        if (!productToAdd) {
            showToast('Producto no encontrado.', 'danger');
            return;
        }
        if (productToAdd.stock <= 0) {
            showToast(`No hay stock disponible para ${productToAdd.nombre}.`, 'danger');
            return;
        }

        const existingItemIndex = carritoItems.findIndex(item => item.idProducto === productId);

        if (existingItemIndex > -1) {
            // Si el producto ya está en el carrito, incrementa la cantidad
            if (carritoItems[existingItemIndex].cantidad < productToAdd.stock) {
                carritoItems[existingItemIndex].cantidad++;
                showToast(`Cantidad de ${productToAdd.nombre} actualizada en el carrito.`, 'info'); // Tipo 'info' para actualizaciones
            } else {
                showToast(`No hay más stock disponible para ${productToAdd.nombre}.`, 'warning');
            }
        } else {
            // Si es un producto nuevo, añádelo con cantidad 1
            carritoItems.push({
                idProducto: productToAdd.id,
                nombre: productToAdd.nombre,
                precioUnitario: productToAdd.precioVenta,
                precioCompra: productToAdd.precioCompra, // Necesario para calcular la ganancia
                cantidad: 1,
                porcentajeGanancia: ( (productToAdd.precioVenta / productToAdd.precioCompra) - 1) * 100 // Calcula la ganancia al añadir
            });
            showToast(`${productToAdd.nombre} añadido al carrito.`, 'success'); // Tipo 'success' para nuevas adiciones
        }
        renderCarrito();
    }

    function removeProductoFromCart(productId) {
        carritoItems = carritoItems.filter(item => item.idProducto !== productId);
        showToast('Producto eliminado del carrito.', 'warning');
        renderCarrito();
    }

    function updateCantidadInCart(productId, newCantidad) {
        const itemIndex = carritoItems.findIndex(item => item.idProducto === productId);
        if (itemIndex > -1) {
            const productAvailable = allProducts.find(p => p.id === productId);
            if (newCantidad > 0 && newCantidad <= productAvailable.stock) {
                carritoItems[itemIndex].cantidad = newCantidad;
                showToast(`Cantidad de ${carritoItems[itemIndex].nombre} actualizada a ${newCantidad}.`, 'info');
            } else if (newCantidad > productAvailable.stock) {
                carritoItems[itemIndex].cantidad = productAvailable.stock; // Ajusta a la cantidad máxima disponible
                showToast(`No puedes añadir más de ${productAvailable.stock} unidades de ${productAvailable.nombre}. Cantidad ajustada.`, 'warning');
            } else {
                showToast('La cantidad debe ser mayor a 0.', 'danger');
            }
        }
        renderCarrito();
    }

    function renderCarrito() {
        productosCarritoContainer.innerHTML = '';
        let totalCarrito = 0;

        if (carritoItems.length === 0) {
            carritoVacioMessage.classList.remove('d-none');
        } else {
            carritoVacioMessage.classList.add('d-none');
            // Añadir los títulos de las columnas una sola vez
            productosCarritoContainer.innerHTML = `
                <div class="row g-2 mb-2 fw-bold text-muted border-bottom pb-1">
                    <div class="col-6">Producto</div>
                    <div class="col-2 text-center">Cant.</div>
                    <div class="col-2 text-end">Subtotal</div>
                    <div class="col-2"></div>
                </div>
            `;
            carritoItems.forEach(item => {
                const subtotal = item.cantidad * item.precioUnitario;
                totalCarrito += subtotal;

                const itemDiv = document.createElement('div');
                itemDiv.classList.add('row', 'g-2', 'mb-2', 'align-items-center', 'carrito-item');
                itemDiv.innerHTML = `
                    <div class="col-6">
                        ${item.nombre}
                        <div class="text-muted small">${currencyFormatter.format(item.precioUnitario)} c/u</div>
                    </div>
                    <div class="col-2">
                        <input type="number" class="form-control form-control-sm cantidad-carrito-input"
                               value="${item.cantidad}" min="1" data-id="${item.idProducto}">
                    </div>
                    <div class="col-2 text-end">
                        ${currencyFormatter.format(subtotal)}
                    </div>
                    <div class="col-2 text-end">
                        <button type="button" class="btn btn-danger btn-sm btn-remove-from-cart" data-id="${item.idProducto}">
                            <i class="bi bi-x"></i>
                        </button>
                    </div>
                `;
                productosCarritoContainer.appendChild(itemDiv);
            });

            // Añadir listeners a los nuevos inputs de cantidad y botones de eliminar
            productosCarritoContainer.querySelectorAll('.cantidad-carrito-input').forEach(input => {
                input.addEventListener('change', (e) => {
                    const productId = parseInt(e.target.dataset.id);
                    const newCantidad = parseInt(e.target.value);
                    updateCantidadInCart(productId, newCantidad);
                });
            });

            productosCarritoContainer.querySelectorAll('.btn-remove-from-cart').forEach(button => {
                button.addEventListener('click', (e) => {
                    const productId = parseInt(e.currentTarget.dataset.id); // Usar currentTarget para el botón
                    removeProductoFromCart(productId);
                });
            });
        }
        totalCarritoDisplay.textContent = currencyFormatter.format(totalCarrito);
    }

    function vaciarCarrito() {
        carritoItems = [];
        renderCarrito();
        showToast('El carrito ha sido vaciado.', 'info');
    }

    // --- Event Listeners ---

    // Listener para buscar productos
    searchProductoVentaInput.addEventListener('input', (e) => {
        const searchTerm = e.target.value;
        renderProductosDisponibles(allProducts, searchTerm); // Filtra los productos ya cargados
    });

    // Listener para añadir al carrito desde la tabla de productos
    productosVentaTableBody.addEventListener('click', (event) => {
        const target = event.target.closest('.btn-add-to-cart');
        if (target) {
            const productId = parseInt(target.dataset.id);
            addProductoToCart(productId);
        }
    });

    // Listener para el botón "Vaciar Carrito"
    btnVaciarCarrito.addEventListener('click', vaciarCarrito);

    // Listener para confirmar la venta
    carritoVentaForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const idUsuario = parseInt(idUsuarioVentaSelect.value);
        const idCliente = parseInt(idClienteVentaSelect.value);

        if (isNaN(idUsuario) || idUsuario <= 0) {
            showToast('Por favor, seleccione un vendedor válido.', 'danger');
            return;
        }
        if (isNaN(idCliente) || idCliente <= 0) {
            showToast('Por favor, seleccione un cliente válido.', 'danger');
            return;
        }
        if (carritoItems.length === 0) {
            showToast('El carrito está vacío. Añada productos para realizar una venta.', 'danger');
            return;
        }

        const productosVentaDTO = carritoItems.map(item => {
            // Recalcular el porcentaje de ganancia en caso de que el precio de compra haya cambiado
            // Esto es solo una precaución, idealmente el precio de compra no cambiaría durante la venta
            const originalProduct = allProducts.find(p => p.id === item.idProducto);
            let ganancia = 0;
            if (originalProduct && originalProduct.precioCompra > 0) {
                ganancia = ((item.precioUnitario / originalProduct.precioCompra) - 1) * 100;
            }

            return {
                idProducto: item.idProducto,
                cantidad: item.cantidad,
                porcentajeGanancia: ganancia // Usar el porcentaje calculado basado en el precio de venta actual del ítem
            };
        });

        const ventaData = {
            idUsuario: idUsuario,
            idCliente: idCliente,
            productos: productosVentaDTO
        };

        try {
            const response = await fetch(API_VENTAS_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ventaData)
            });

            if (response.status === 400) {
                const errorBody = await response.json();
                showToast(`Error al procesar la venta: ${errorBody.message || 'Algunos datos son inválidos.'}`, 'danger');
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al confirmar la venta: ${response.status} - ${errorText}`);
            }

            showToast('¡Venta realizada exitosamente!', 'success');
            vaciarCarrito(); // Limpiar el carrito después de la venta
            cargarProductosDisponibles(); // Recargar productos disponibles para reflejar cambios de stock
        } catch (error) {
            console.error('Error al confirmar la venta:', error);
            showToast(`Error al confirmar la venta: ${error.message}`, 'danger');
        }
    });

    // --- Carga Inicial ---
    async function init() {
        await cargarClientesEnSelect();
        await cargarUsuariosEnSelect();
        await cargarProductosDisponibles(); // Cargar productos al inicio
        renderCarrito(); // Renderizar el carrito inicialmente vacío
    }

    init(); // Ejecutar la carga inicial
}

// Asegurarse de que la función se ejecuta cuando el DOM está cargado
document.addEventListener('DOMContentLoaded', initPuntoVenta);