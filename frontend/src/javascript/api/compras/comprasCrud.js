// FRONTEND/src/javascript/api/compras/comprasCrud.js

function initComprasCRUD() {
    const API_COMPRAS_URL = 'http://localhost:8080/api/compras';
    const API_PRODUCTOS_URL = 'http://localhost:8080/api/productos';
    const API_PROVEEDORES_URL = 'http://localhost:8080/api/proveedores';

    const comprasTableBody = document.getElementById('comprasTableBody');
    const noComprasMessage = document.getElementById('noComprasMessage');

    const compraForm = document.getElementById('compraForm');
    const compraIdInput = document.getElementById('compraId'); // Hidden input for purchase ID
    const idProveedorCompraSelect = document.getElementById('idProveedorCompraSelect');
    const productosCompraContainer = document.getElementById('productosCompraContainer');
    const btnAgregarProductoACart = document.getElementById('btnAgregarProductoACart');
    const totalCompraDisplay = document.getElementById('totalCompraDisplay');
    const btnGuardarCompra = document.getElementById('btnGuardarCompra');

    const modalTitle = document.getElementById('compraModalLabel');
    const btnAbrirModalAgregarCompra = document.getElementById('btnAbrirModalAgregarCompra');

    let allProducts = []; // Para almacenar todos los productos disponibles

    // Formateador de números (para moneda)
    const currencyFormatter = new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    // --- Helper Functions ---
    function getCompraModalInstance() {
        const modalElement = document.getElementById('compraModal');
        if (modalElement) {
            return bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        }
        console.error("El elemento del modal #compraModal no fue encontrado.");
        return null;
    }

    // Asegúrate de que esta función showToast esté bien implementada en tu global.js o en un script cargado antes
    function showToast(message, type = 'success') {
        const toastContainer = document.querySelector('.toast-container');
        if (!toastContainer) {
            console.error('No se encontró el contenedor de toasts.');
            alert(message);
            return;
        }

        const toastElement = document.createElement('div');
        toastElement.classList.add('toast', 'align-items-center', 'border-0', 'fade', 'show');
        toastElement.setAttribute('role', 'alert');
        toastElement.setAttribute('aria-live', 'assertive');
        toastElement.setAttribute('aria-atomic', 'true');

        let bgColorClass = '';
        let textColorClass = 'text-white';
        switch (type) {
            case 'success':
                bgColorClass = 'bg-success';
                break;
            case 'danger':
                bgColorClass = 'bg-danger';
                break;
            case 'warning':
                bgColorClass = 'bg-warning';
                textColorClass = 'text-dark';
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
            delay: 3000
        });
        bsToast.show();

        toastElement.addEventListener('hidden.bs.toast', () => {
            toastElement.remove();
        });
    }

    // --- Carga de datos para Selects ---

    async function cargarProveedoresEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_PROVEEDORES_URL);
            if (!response.ok) throw new Error(`Error al cargar proveedores: ${response.statusText}`);
            const proveedores = await response.json();

            idProveedorCompraSelect.innerHTML = '<option value="">Seleccione un proveedor</option>';
            proveedores.forEach(proveedor => {
                const option = document.createElement('option');
                option.value = proveedor.id;
                option.textContent = proveedor.nombre;
                idProveedorCompraSelect.appendChild(option);
            });

            if (selectedId) {
                idProveedorCompraSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar proveedores:', error);
            showToast('Error al cargar la lista de proveedores.', 'danger');
        }
    }

    async function cargarProductosDisponibles() {
        try {
            const response = await fetch(API_PRODUCTOS_URL);
            if (!response.ok) throw new Error(`Error al cargar productos: ${response.statusText}`);
            allProducts = await response.json(); // Guardar todos los productos
            // showToast('Productos disponibles cargados.', 'info'); // Opcional: para depuración
        } catch (error) {
            console.error('Error al cargar productos disponibles:', error);
            showToast('Error al cargar la lista de productos disponibles.', 'danger');
            allProducts = []; // Asegura que allProducts esté vacío en caso de error
        }
    }

    // Esta función se llama al añadir un nuevo item de producto al modal
    function populateProductSelect(selectElement, selectedProductId = null) {
        selectElement.innerHTML = '<option value="">Seleccione un producto</option>';
        // Asegúrate de que allProducts no esté vacío antes de intentar poblar
        if (allProducts && allProducts.length > 0) {
            allProducts.forEach(producto => {
                const option = document.createElement('option');
                option.value = producto.id;
                option.textContent = `${producto.nombre} (ID: ${producto.id}, Precio: ${currencyFormatter.format(producto.precioCompra)})`;
                selectElement.appendChild(option);
            });
        } else {
             // Opcional: añadir un mensaje si no hay productos
             const option = document.createElement('option');
             option.value = "";
             option.textContent = "No hay productos disponibles";
             option.disabled = true; // Deshabilitar la opción
             selectElement.appendChild(option);
        }

        if (selectedProductId) {
            selectElement.value = selectedProductId;
        }
    }

    // --- Gestión de Items de Compra en el Formulario ---

    let itemCounter = 0; // Para dar IDs únicos a los elementos dinámicos

    function addProductoCompraItem(productoId = null, cantidad = null, precioUnitario = null) {
        const newItemDiv = document.createElement('div');
        newItemDiv.classList.add('row', 'g-2', 'mb-2', 'producto-compra-item');
        newItemDiv.dataset.itemId = itemCounter; // Para identificar la fila

        newItemDiv.innerHTML = `
            <div class="col-md-5">
                <label for="productoSelect_${itemCounter}" class="form-label visually-hidden">Producto</label>
                <select class="form-select producto-select" id="productoSelect_${itemCounter}" required>
                    </select>
            </div>
            <div class="col-md-2">
                <label for="cantidadProducto_${itemCounter}" class="form-label visually-hidden">Cantidad</label>
                <input type="number" class="form-control cantidad-producto" id="cantidadProducto_${itemCounter}" placeholder="Cantidad" min="1" value="${cantidad || 1}" required>
            </div>
            <div class="col-md-3">
                <label for="precioUnitario_${itemCounter}" class="form-label visually-hidden">Precio Unitario</label>
                <input type="text" class="form-control precio-unitario" id="precioUnitario_${itemCounter}" placeholder="Precio Unit." readonly value="${precioUnitario !== null ? currencyFormatter.format(precioUnitario) : ''}">
            </div>
            <div class="col-md-2 d-flex align-items-center">
                <button type="button" class="btn btn-danger btn-sm btn-eliminar-producto-compra">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;
        productosCompraContainer.appendChild(newItemDiv);

        const currentProductSelect = newItemDiv.querySelector(`#productoSelect_${itemCounter}`);
        // *** CAMBIO CLAVE AQUÍ: Asegúrate de poblar el select del producto ***
        populateProductSelect(currentProductSelect, productoId);

        // Si se está editando y el precioUnitario ya viene dado, asegúrate de que el select se vea bien
        if (precioUnitario !== null && productoId !== null) {
            const selectedProduct = allProducts.find(p => p.id == productoId);
            if (selectedProduct) {
                // Aquí, el precioUnitario ya está en el input, no es necesario recalcular con precioCompra
                // solo nos aseguramos que el select tenga el valor correcto
                currentProductSelect.value = selectedProduct.id;
            }
        }


        // Add event listeners for the new item
        currentProductSelect.addEventListener('change', (e) => onProductChange(e, newItemDiv));
        newItemDiv.querySelector(`#cantidadProducto_${itemCounter}`).addEventListener('input', () => onQuantityChange(newItemDiv));
        newItemDiv.querySelector('.btn-eliminar-producto-compra').addEventListener('click', () => removeProductoCompraItem(newItemDiv));

        itemCounter++;
        updateTotalCompra(); // Actualizar el total cuando se añade un item
    }

    function removeProductoCompraItem(itemElement) {
        itemElement.remove();
        updateTotalCompra(); // Actualizar el total cuando se elimina un item
    }

    function onProductChange(event, itemElement) {
        const selectedProductId = event.target.value;
        const precioUnitarioInput = itemElement.querySelector('.precio-unitario');
        const producto = allProducts.find(p => p.id == selectedProductId);

        if (producto) {
            precioUnitarioInput.value = currencyFormatter.format(producto.precioCompra);
        } else {
            precioUnitarioInput.value = '';
        }
        updateTotalCompra();
    }

    function onQuantityChange(itemElement) {
        updateTotalCompra();
    }

    function updateTotalCompra() {
        let total = 0;
        document.querySelectorAll('.producto-compra-item').forEach(itemElement => {
            const selectedProductId = itemElement.querySelector('.producto-select').value;
            const cantidad = parseInt(itemElement.querySelector('.cantidad-producto').value);
            const producto = allProducts.find(p => p.id == selectedProductId);

            if (producto && !isNaN(cantidad) && cantidad > 0) {
                total += producto.precioCompra * cantidad;
            }
        });
        totalCompraDisplay.textContent = currencyFormatter.format(total);
    }

    // --- Limpieza y Carga Inicial ---

    function limpiarFormularioCompra() {
        compraIdInput.value = '';
        idProveedorCompraSelect.value = ''; // Resetear el select del proveedor
        productosCompraContainer.innerHTML = ''; // Limpiar productos dinámicos
        itemCounter = 0; // Resetear contador de items
        //addProductoCompraItem(); // NO LLAMES AQUI, SE LLAMARA EN EL LISTENER DESPUES DE CARGAR DATOS
        updateTotalCompra(); // Resetear el total a 0
        modalTitle.textContent = 'Agregar Nueva Compra';
        btnGuardarCompra.classList.remove('d-none'); // Mostrar botón de guardar
        idProveedorCompraSelect.removeAttribute('disabled'); // Habilitar proveedor
        
        btnAgregarProductoACart.classList.remove('d-none');
        btnAgregarProductoACart.removeAttribute('disabled');
    }

    async function cargarCompras() {
        try {
            const response = await fetch(API_COMPRAS_URL);
            if (!response.ok) throw new Error(`Error al cargar compras: ${response.statusText}`);
            const compras = await response.json();

            comprasTableBody.innerHTML = '';
            if (compras.length === 0) {
                noComprasMessage.classList.remove('d-none');
            } else {
                noComprasMessage.classList.add('d-none');
                compras.forEach(compra => {
                    const row = comprasTableBody.insertRow();
                    row.innerHTML = `
                        <td>${compra.idCompra}</td>
                        <td>${compra.fecha}</td>
                        <td>${compra.proveedor ? compra.proveedor.nombre : 'N/A'}</td>
                        <td>${currencyFormatter.format(compra.total)}</td>
                        <td>
                            <button class="btn btn-sm btn-info btn-ver-compra me-2" data-id="${compra.idCompra}">
                                <i class="bi bi-eye"></i> Ver Detalles
                            </button>
                        </td>
                    `;
                });
            }
        } catch (error) {
            console.error('Error al cargar compras:', error);
            showToast(`Error al cargar compras: ${error.message}`, 'danger');
        }
    }

    // --- Event Listeners ---

    btnAbrirModalAgregarCompra.addEventListener('click', async () => {
        limpiarFormularioCompra();
        await cargarProveedoresEnSelect(); // Cargar proveedores al abrir el modal
        await cargarProductosDisponibles(); // Cargar productos disponibles

        // *** CAMBIO CLAVE AQUÍ: Añadir el primer ítem de producto DESPUÉS de que allProducts esté cargado ***
        addProductoCompraItem(); // Añadir un item de producto vacío por defecto

        const compraModal = getCompraModalInstance();
        if (compraModal) {
            // Reintroducimos setTimeout para manejar la condición de carrera aria-hidden
            setTimeout(() => {
                compraModal.show();
            }, 0);
        }
    });

    btnAgregarProductoACart.addEventListener('click', () => {
        addProductoCompraItem();
    });

    compraForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const idProveedor = parseInt(idProveedorCompraSelect.value);
        if (isNaN(idProveedor) || idProveedor <= 0) {
            showToast('Por favor, seleccione un proveedor válido.', 'danger');
            return;
        }

        const productosDetalle = [];
        let allProductsValid = true;
        document.querySelectorAll('.producto-compra-item').forEach(itemElement => {
            const idProducto = parseInt(itemElement.querySelector('.producto-select').value);
            const cantidad = parseInt(itemElement.querySelector('.cantidad-producto').value);

            if (isNaN(idProducto) || idProducto <= 0 || isNaN(cantidad) || cantidad <= 0) {
                allProductsValid = false;
                showToast('Asegúrese de seleccionar un producto y una cantidad válida (mayor a 0) para cada ítem.', 'danger');
                return;
            }
            productosDetalle.push({ idProducto, cantidad });
        });

        if (!allProductsValid || productosDetalle.length === 0) {
            if (productosDetalle.length === 0) {
                 showToast('Debe agregar al menos un producto válido a la compra.', 'danger');
            }
            return;
        }

        const compraRequestDTO = {
            idProveedor: idProveedor,
            productos: productosDetalle
        };

        try {
            const response = await fetch(API_COMPRAS_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(compraRequestDTO)
            });

            if (response.status === 400) {
                const errorBody = await response.json();
                showToast(`Error de datos: ${errorBody.message || 'Algunos datos son inválidos.'}`, 'danger');
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al guardar la compra: ${response.status} - ${errorText}`);
            }

            showToast('Compra registrada exitosamente.', 'success');
            const compraModal = getCompraModalInstance();
            if (compraModal) {
                compraModal.hide();
            }
            cargarCompras();
        } catch (error) {
            console.error('Error al guardar la compra:', error);
            showToast(`Error al guardar la compra: ${error.message}`, 'danger');
        }
    });

    // Evento para Ver Detalles de una Compra existente
    comprasTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-ver-compra') || event.target.closest('.btn-ver-compra')) {
            const id = event.target.dataset.id || event.target.closest('.btn-ver-compra').dataset.id;
            try {
                const response = await fetch(`${API_COMPRAS_URL}/${id}`);
                if (!response.ok) throw new Error(`Error al obtener detalles de compra: ${response.statusText}`);
                const compra = await response.json();

                modalTitle.textContent = `Detalles de Compra #${compra.idCompra}`;
                compraIdInput.value = compra.idCompra;
                btnGuardarCompra.classList.add('d-none'); // Ocultar botón de guardar en modo solo ver

                idProveedorCompraSelect.setAttribute('disabled', 'true');
                await cargarProveedoresEnSelect(compra.proveedor ? compra.proveedor.id : null);

                btnAgregarProductoACart.classList.add('d-none');
                btnAgregarProductoACart.setAttribute('disabled', 'true');

                productosCompraContainer.innerHTML = '';
                itemCounter = 0;

                await cargarProductosDisponibles(); // Cargar productos disponibles ANTES de poblar los detalles

                compra.detalles.forEach(detalle => {
                    addProductoCompraItem(detalle.producto.id, detalle.cantidad, detalle.precioCompra);

                    const lastItemAdded = productosCompraContainer.lastElementChild;
                    if (lastItemAdded) {
                        lastItemAdded.querySelector('.producto-select').setAttribute('disabled', 'true');
                        lastItemAdded.querySelector('.cantidad-producto').setAttribute('disabled', 'true');
                        lastItemAdded.querySelector('.btn-eliminar-producto-compra').classList.add('d-none');
                    }
                });
                updateTotalCompra();

                const compraModal = getCompraModalInstance();
                if (compraModal) {
                    setTimeout(() => {
                        compraModal.show();
                    }, 0);
                }
            } catch (error) {
                console.error('Error al cargar detalles de compra:', error);
                showToast('Error al cargar detalles de la compra.', 'danger');
            }
        }
    });

    // --- Initial Load ---
    cargarCompras();

} // End of initComprasCRUD.

document.addEventListener('DOMContentLoaded', initComprasCRUD);