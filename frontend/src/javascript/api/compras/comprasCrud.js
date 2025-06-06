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
        } catch (error) {
            console.error('Error al cargar productos disponibles:', error);
            showToast('Error al cargar la lista de productos disponibles.', 'danger');
            allProducts = []; // Asegura que allProducts esté vacío en caso de error
        }
    }

    function populateProductSelect(selectElement, selectedProductId = null) {
        selectElement.innerHTML = '<option value="">Seleccione un producto</option>';
        if (allProducts && allProducts.length > 0) {
            allProducts.forEach(producto => {
                const option = document.createElement('option');
                option.value = producto.id;
                option.textContent = `${producto.nombre} (ID: ${producto.id}, Precio por defecto: ${currencyFormatter.format(producto.precioCompra)})`;
                selectElement.appendChild(option);
            });
        } else {
            const option = document.createElement('option');
            option.value = "";
            option.textContent = "No hay productos disponibles";
            option.disabled = true;
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
        newItemDiv.dataset.itemId = itemCounter;

        newItemDiv.innerHTML = `
            <div class="col-md-4">
                <label for="productoSelect_${itemCounter}" class="form-label visually-hidden">Producto</label>
                <select class="form-select producto-select" id="productoSelect_${itemCounter}" required>
                </select>
            </div>
            <div class="col-md-3">
                <label for="cantidadProducto_${itemCounter}" class="form-label visually-hidden">Cantidad</label>
                <input type="number" class="form-control cantidad-producto" id="cantidadProducto_${itemCounter}" placeholder="Cantidad" min="1" value="${cantidad || 1}" required>
            </div>
            <div class="col-md-3">
                <label for="precioUnitario_${itemCounter}" class="form-label visually-hidden">Precio Unitario</label>
                <input type="text" class="form-control precio-unitario" id="precioUnitario_${itemCounter}" placeholder="Precio Unit." value="${precioUnitario !== null ? currencyFormatter.format(precioUnitario) : (productoId !== null ? currencyFormatter.format(allProducts.find(p => p.id == productoId)?.precioCompra || 0) : '0')}" required>
            <div class="col-md-2 d-flex align-items-center">
                <button type="button" class="btn btn-danger btn-sm btn-eliminar-producto-compra">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;
        productosCompraContainer.appendChild(newItemDiv);

        const currentProductSelect = newItemDiv.querySelector(`#productoSelect_${itemCounter}`);
        const currentCantidadInput = newItemDiv.querySelector(`#cantidadProducto_${itemCounter}`);
        const currentPrecioUnitarioInput = newItemDiv.querySelector(`#precioUnitario_${itemCounter}`);

        populateProductSelect(currentProductSelect, productoId);

        // Si se está editando y se ha seleccionado un producto, pre-rellenar el precio
        if (productoId !== null) {
            const selectedProduct = allProducts.find(p => p.id == productoId);
            if (selectedProduct && precioUnitario === null) { // Solo pre-rellenar si el precio no ha sido proporcionado (ej. al editar una compra existente)
                currentPrecioUnitarioInput.value = currencyFormatter.format(selectedProduct.precioCompra);
            }
        }
        
        // Añadir detectores de eventos para el nuevo elemento
        currentProductSelect.addEventListener('change', (e) => onProductChange(e, newItemDiv));
        currentCantidadInput.addEventListener('input', () => updateTotalCompra()); // El cambio de cantidad afecta el total
        currentPrecioUnitarioInput.addEventListener('input', () => updateTotalCompra()); // El cambio de precio unitario afecta el total
        newItemDiv.querySelector('.btn-eliminar-producto-compra').addEventListener('click', () => removeProductoCompraItem(newItemDiv));

        itemCounter++;
        updateTotalCompra();
    }

    function removeProductoCompraItem(itemElement) {
        itemElement.remove();
        updateTotalCompra();
    }

   // Dentro de onProductChange
function onProductChange(event, itemElement) {
    const selectedProductId = event.target.value;
    const precioUnitarioInput = itemElement.querySelector('.precio-unitario');
    const producto = allProducts.find(p => p.id == selectedProductId);

    if (producto) {
        // Rellenar con el precio por defecto del producto, o 0 si no existe
        precioUnitarioInput.value = currencyFormatter.format(producto.precioCompra || 0);
    } else {
        // Limpiar o establecer a '0' si no se selecciona ningún producto
        precioUnitarioInput.value = '0'; 
    }
    updateTotalCompra();
}

    function updateTotalCompra() {
    let total = 0;
    document.querySelectorAll('.producto-compra-item').forEach(itemElement => {
        const selectedProductId = itemElement.querySelector('.producto-select').value;
        const cantidad = parseInt(itemElement.querySelector('.cantidad-producto').value);
        
        const precioUnitarioText = itemElement.querySelector('.precio-unitario').value.trim(); // 1. Elimina espacios al inicio/final
        let precioUnitario;

        // 2. Manejo de string vacío o no numérico
        if (precioUnitarioText === '') {
            precioUnitario = 0; // O un valor por defecto si un input vacío significa 0
        } else {
            // Esta expresión regular es más robusta:
            // - /[^\d,]+/g: Elimina cualquier carácter que NO sea un dígito (\d) o una coma (,)
            // - .replace(',', '.'): Luego, reemplaza las comas por puntos para que parseFloat funcione
            const cleanText = precioUnitarioText.replace(/[^\d,]+/g, '').replace(',', '.');
            precioUnitario = parseFloat(cleanText);
        }
        
        // 3. Verificación final de NaN para asegurar que siempre sea un número
        if (isNaN(precioUnitario)) {
            console.warn(`Advertencia: El precio unitario "${precioUnitarioText}" para el producto ID ${selectedProductId} no pudo ser analizado. Estableciendo a 0.`);
            precioUnitario = 0; // Fallback seguro para evitar NaN en cálculos
        }

        console.log(`Parsed precioUnitario for product ID ${selectedProductId}:`, precioUnitario);

        if (selectedProductId && !isNaN(cantidad) && cantidad > 0 && precioUnitario >= 0) { // precioUnitario ya se ha validado como número
            total += precioUnitario * cantidad;
        }
    });
    totalCompraDisplay.textContent = currencyFormatter.format(total);
}

    function limpiarFormularioCompra() {
        compraIdInput.value = '';
        idProveedorCompraSelect.value = '';
        productosCompraContainer.innerHTML = '';
        itemCounter = 0;
        updateTotalCompra();
        modalTitle.textContent = 'Agregar Nueva Compra';
        btnGuardarCompra.classList.remove('d-none');
        idProveedorCompraSelect.removeAttribute('disabled');
        btnAgregarProductoACart.classList.remove('d-none');
        btnAgregarProductoACart.removeAttribute('disabled');
        
        // Habilitar inputs para una nueva compra
        document.querySelectorAll('.producto-compra-item').forEach(itemElement => {
            itemElement.querySelector('.producto-select').removeAttribute('disabled');
            itemElement.querySelector('.cantidad-producto').removeAttribute('disabled');
            itemElement.querySelector('.precio-unitario').removeAttribute('disabled'); // Asegurarse de que este también esté habilitado
            itemElement.querySelector('.btn-eliminar-producto-compra').classList.remove('d-none');
        });
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
        await cargarProveedoresEnSelect();
        await cargarProductosDisponibles();
        addProductoCompraItem(); // Añadir un elemento vacío para una nueva compra

        const compraModal = getCompraModalInstance();
        if (compraModal) {
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
            const precioUnitarioText = itemElement.querySelector('.precio-unitario').value;
            const precioUnitario = parseFloat(precioUnitarioText.replace(/[COP$.]/g, '').replace(',', '.')); // Analizar el precio introducido por el usuario

            if (isNaN(idProducto) || idProducto <= 0 || isNaN(cantidad) || cantidad <= 0 || isNaN(precioUnitario) || precioUnitario < 0) {
                allProductsValid = false;
                showToast('Asegúrese de seleccionar un producto, una cantidad válida (mayor a 0) y un precio unitario válido (mayor o igual a 0) para cada ítem.', 'danger');
                return;
            }
            productosDetalle.push({ idProducto, cantidad, precioCompra: precioUnitario }); // Incluir precioCompra
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
                btnGuardarCompra.classList.add('d-none'); // Ocultar el botón de guardar en modo de solo vista

                idProveedorCompraSelect.setAttribute('disabled', 'true');
                await cargarProveedoresEnSelect(compra.proveedor ? compra.proveedor.id : null);

                btnAgregarProductoACart.classList.add('d-none');
                btnAgregarProductoACart.setAttribute('disabled', 'true');

                productosCompraContainer.innerHTML = '';
                itemCounter = 0;

                await cargarProductosDisponibles(); // Cargar productos disponibles ANTES de rellenar los detalles

                compra.detalles.forEach(detalle => {
                    addProductoCompraItem(detalle.producto.id, detalle.cantidad, detalle.precioCompra);

                    const lastItemAdded = productosCompraContainer.lastElementChild;
                    if (lastItemAdded) {
                        lastItemAdded.querySelector('.producto-select').setAttribute('disabled', 'true');
                        lastItemAdded.querySelector('.cantidad-producto').setAttribute('disabled', 'true');
                        lastItemAdded.querySelector('.precio-unitario').setAttribute('disabled', 'true'); // Deshabilitar también el campo de precio
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

    // --- Carga Inicial ---
    cargarCompras();

} // Fin de initComprasCRUD.

document.addEventListener('DOMContentLoaded', initComprasCRUD);