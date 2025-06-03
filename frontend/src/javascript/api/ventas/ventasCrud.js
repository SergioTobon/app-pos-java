// FRONTEND/src/javascript/api/ventas/ventasCrud.js

function initVentasCRUD() {
    const API_VENTAS_URL = 'http://localhost:8080/api/ventas';
    const API_PRODUCTOS_URL = 'http://localhost:8080/api/productos';
    const API_CLIENTES_URL = 'http://localhost:8080/api/clientes';
    const API_USUARIOS_URL = 'http://localhost:8080/api/usuarios'; // Asumiendo que tienes un endpoint para usuarios

    const ventasTableBody = document.getElementById('ventasTableBody');
    const noVentasMessage = document.getElementById('noVentasMessage');

    const ventaForm = document.getElementById('ventaForm');
    const ventaIdInput = document.getElementById('ventaId'); // Hidden input for sale ID
    const idUsuarioVentaSelect = document.getElementById('idUsuarioVentaSelect');
    const idClienteVentaSelect = document.getElementById('idClienteVentaSelect');
    const productosVentaContainer = document.getElementById('productosVentaContainer');
    const btnAgregarProductoAVenta = document.getElementById('btnAgregarProductoAVenta');
    const totalVentaDisplay = document.getElementById('totalVentaDisplay');
    const btnGuardarVenta = document.getElementById('btnGuardarVenta');

    const modalTitle = document.getElementById('ventaModalLabel');
    const btnAbrirModalAgregarVenta = document.getElementById('btnAbrirModalAgregarVenta');

    let allProducts = []; // Para almacenar todos los productos disponibles (con su precio de compra)

    // Formateador de números (para moneda)
    const currencyFormatter = new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });

    // --- Helper Functions ---
    function getVentaModalInstance() {
        const modalElement = document.getElementById('ventaModal');
        if (modalElement) {
            return bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        }
        console.error("El elemento del modal #ventaModal no fue encontrado.");
        return null;
    }

    function showToast(message, type = 'success') {
        console.log(`Mensaje (${type}): ${message}`);
        // Implementar un Toast de Bootstrap o un sistema de alertas aquí
        // Por ahora, solo un alert simple para demostración
        alert(message); 
    }

    // --- Carga de datos para Selects ---

    async function cargarUsuariosEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_USUARIOS_URL);
            if (!response.ok) throw new Error(`Error al cargar usuarios: ${response.statusText}`);
            const usuarios = await response.json();

            idUsuarioVentaSelect.innerHTML = '<option value="">Seleccione un usuario</option>';
            usuarios.forEach(usuario => {
                const option = document.createElement('option');
                option.value = usuario.id; // Asumiendo que el ID del usuario es 'id'
                option.textContent = usuario.nombre; // Asumiendo que el nombre del usuario es 'nombre'
                idUsuarioVentaSelect.appendChild(option);
            });

            if (selectedId) {
                idUsuarioVentaSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar usuarios:', error);
            showToast('Error al cargar la lista de usuarios.', 'danger');
        }
    }

    async function cargarClientesEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_CLIENTES_URL);
            if (!response.ok) throw new Error(`Error al cargar clientes: ${response.statusText}`);
            const clientes = await response.json();

            idClienteVentaSelect.innerHTML = '<option value="">Seleccione un cliente</option>';
            clientes.forEach(cliente => {
                const option = document.createElement('option');
                option.value = cliente.id; // Asumiendo que el ID del cliente es 'id'
                option.textContent = cliente.nombre; // Asumiendo que el nombre del cliente es 'nombre'
                idClienteVentaSelect.appendChild(option);
            });

            if (selectedId) {
                idClienteVentaSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar clientes:', error);
            showToast('Error al cargar la lista de clientes.', 'danger');
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
        }
    }

    function populateProductSelect(selectElement, selectedProductId = null) {
        selectElement.innerHTML = '<option value="">Seleccione un producto</option>';
        allProducts.forEach(producto => {
            const option = document.createElement('option');
            option.value = producto.id;
            option.textContent = `${producto.nombre} (Stock: ${producto.stock}, P. Compra: ${currencyFormatter.format(producto.precioCompra)})`;
            selectElement.appendChild(option);
        });
        if (selectedProductId) {
            selectElement.value = selectedProductId;
        }
    }

    // --- Gestión de Items de Venta en el Formulario ---

    // Función para manejar el cambio de selección de producto
    function onProductChange(event, itemElement) {
        // Cuando el producto cambia, actualizamos el precio unitario y el total.
        updateItemAndTotal(itemElement); 
    }

    let itemCounter = 0; // Para dar IDs únicos a los elementos dinámicos

    function addProductoVentaItem(productoId = null, cantidad = null, porcentajeGanancia = null, precioVentaUnitario = null, isViewMode = false) {
        const newItemDiv = document.createElement('div');
        newItemDiv.classList.add('row', 'g-2', 'mb-2', 'producto-venta-item');
        newItemDiv.dataset.itemId = itemCounter; // Para identificar la fila

        // Determine if labels should be included
        const includeLabels = productosVentaContainer.children.length === 0; // Only show labels for the first item

        newItemDiv.innerHTML = `
            <div class="col-md-5"> 
                ${includeLabels ? '<label for="productoVentaSelect_' + itemCounter + '" class="form-label">Producto</label>' : ''}
                <select class="form-select producto-venta-select" id="productoVentaSelect_${itemCounter}" required ${isViewMode ? 'disabled' : ''}>
                    <option value="">Seleccione un producto</option>
                </select>
            </div>
            <div class="col-md-2">
                ${includeLabels ? '<label for="cantidadProductoVenta_' + itemCounter + '" class="form-label">Cantidad</label>' : ''}
                <input type="number" class="form-control cantidad-producto-venta" id="cantidadProductoVenta_${itemCounter}" placeholder="Cantidad" min="1" value="${cantidad || 1}" required ${isViewMode ? 'disabled' : ''}>
            </div>
            <div class="col-md-2">
                ${includeLabels ? '<label for="porcentajeGanancia_' + itemCounter + '" class="form-label">Ganancia (%)</label>' : ''}
                <input type="number" step="0.01" class="form-control porcentaje-ganancia-venta" id="porcentajeGanancia_${itemCounter}" placeholder="Ganancia %" min="0" value="${porcentajeGanancia !== null ? porcentajeGanancia : 20}" required ${isViewMode ? 'disabled' : ''}>
            </div>
            <div class="col-md-2">
                ${includeLabels ? '<label for="precioVentaUnitario_' + itemCounter + '" class="form-label">P. Venta Unit.</label>' : ''}
                <input type="text" class="form-control precio-venta-unitario" id="precioVentaUnitario_${itemCounter}" placeholder="P. Venta Unit." readonly value="${precioVentaUnitario !== null ? currencyFormatter.format(precioVentaUnitario) : ''}">
            </div>
            <div class="col-md-1 d-flex align-items-end"> 
                <button type="button" class="btn btn-danger btn-sm btn-eliminar-producto-venta w-100 ${isViewMode ? 'd-none' : ''}">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;
        productosVentaContainer.appendChild(newItemDiv);

        const currentProductSelect = newItemDiv.querySelector(`#productoVentaSelect_${itemCounter}`);
        populateProductSelect(currentProductSelect, productoId);

        // Add event listeners for the new item
        currentProductSelect.addEventListener('change', (e) => onProductChange(e, newItemDiv));
        newItemDiv.querySelector(`#cantidadProductoVenta_${itemCounter}`).addEventListener('input', () => updateItemAndTotal(newItemDiv));
        newItemDiv.querySelector(`#porcentajeGanancia_${itemCounter}`).addEventListener('input', () => updateItemAndTotal(newItemDiv));
        
        const deleteButton = newItemDiv.querySelector('.btn-eliminar-producto-venta');
        if (deleteButton) { // Ensure button exists before adding listener
            deleteButton.addEventListener('click', () => removeProductoVentaItem(newItemDiv));
        }

        itemCounter++;
        updateItemAndTotal(newItemDiv); // Actualizar el total cuando se añade un item o se modifica
    }

    function removeProductoVentaItem(itemElement) {
        itemElement.remove();
        updateTotalVenta(); // Actualizar el total cuando se elimina un item
    }

    function updateItemAndTotal(itemElement) {
        const selectedProductId = itemElement.querySelector('.producto-venta-select').value;
        const cantidad = parseInt(itemElement.querySelector('.cantidad-producto-venta').value);
        const porcentajeGanancia = parseFloat(itemElement.querySelector('.porcentaje-ganancia-venta').value);
        const precioVentaUnitarioInput = itemElement.querySelector('.precio-venta-unitario');
        
        const productoBase = allProducts.find(p => p.id == selectedProductId);

        if (productoBase && !isNaN(cantidad) && cantidad > 0 && !isNaN(porcentajeGanancia)) {
            const precioCompra = productoBase.precioCompra;
            const precioVentaCalculado = precioCompra * (1 + porcentajeGanancia / 100);
            precioVentaUnitarioInput.value = currencyFormatter.format(precioVentaCalculado);
        } else {
            precioVentaUnitarioInput.value = '';
        }
        updateTotalVenta();
    }

    function updateTotalVenta() {
        let total = 0;
        document.querySelectorAll('.producto-venta-item').forEach(itemElement => {
            const selectedProductId = itemElement.querySelector('.producto-venta-select').value;
            const cantidad = parseInt(itemElement.querySelector('.cantidad-producto-venta').value);
            const porcentajeGanancia = parseFloat(itemElement.querySelector('.porcentaje-ganancia-venta').value);
            
            const productoBase = allProducts.find(p => p.id == selectedProductId);

            if (productoBase && !isNaN(cantidad) && cantidad > 0 && !isNaN(porcentajeGanancia)) {
                const precioVentaCalculado = productoBase.precioCompra * (1 + porcentajeGanancia / 100);
                total += precioVentaCalculado * cantidad;
            }
        });
        totalVentaDisplay.textContent = currencyFormatter.format(total);
    }

    // --- Limpieza y Carga Inicial ---

    function limpiarFormularioVenta() {
        ventaIdInput.value = '';
        idUsuarioVentaSelect.value = '';
        idClienteVentaSelect.value = '';
        productosVentaContainer.innerHTML = ''; // Limpiar productos dinámicos
        itemCounter = 0; // Resetear contador de items
        updateTotalVenta(); // Resetear el total a 0
        modalTitle.textContent = 'Registrar Nueva Venta';
        btnGuardarVenta.classList.remove('d-none'); // Mostrar botón de guardar
        
        idUsuarioVentaSelect.removeAttribute('disabled'); // Habilitar select de usuario
        idClienteVentaSelect.removeAttribute('disabled'); // Habilitar select de cliente
        btnAgregarProductoAVenta.classList.remove('d-none'); // Mostrar botón de añadir producto
        btnAgregarProductoAVenta.removeAttribute('disabled'); // Habilitar botón de añadir producto
    }

    async function cargarVentas() {
        try {
            const response = await fetch(API_VENTAS_URL);
            if (!response.ok) throw new Error(`Error al cargar ventas: ${response.statusText}`);
            const ventas = await response.json();

            ventasTableBody.innerHTML = '';
            if (ventas.length === 0) {
                noVentasMessage.classList.remove('d-none');
            } else {
                noVentasMessage.classList.add('d-none');
                ventas.forEach(venta => {
                    const row = ventasTableBody.insertRow();
                    row.innerHTML = `
                        <td>${venta.idVenta}</td>
                        <td>${venta.fecha}</td>
                        <td>${venta.cliente ? venta.cliente.nombre : 'N/A'}</td>
                        <td>${venta.usuario ? venta.usuario.nombre : 'N/A'}</td>
                        <td>${currencyFormatter.format(venta.total)}</td>
                        <td>
                            <button class="btn btn-sm btn-info btn-ver-venta me-2" data-id="${venta.idVenta}">
                                <i class="bi bi-eye"></i> Ver Detalles
                            </button>
                        </td>
                    `;
                });
            }
        } catch (error) {
            console.error('Error al cargar ventas:', error);
            showToast(`Error al cargar ventas: ${error.message}`, 'danger');
        }
    }

    // --- Event Listeners ---

    btnAbrirModalAgregarVenta.addEventListener('click', async () => {
        limpiarFormularioVenta(); // Limpia el formulario
        await cargarUsuariosEnSelect();
        await cargarClientesEnSelect();
        await cargarProductosDisponibles(); // Aseguramos que allProducts esté lleno.
        
        // Ahora que allProducts tiene datos, podemos añadir el primer item de producto.
        addProductoVentaItem(); 

        const ventaModal = getVentaModalInstance();
        if (ventaModal) {
            setTimeout(() => {
                ventaModal.show();
            }, 0);
        }
    });

    btnAgregarProductoAVenta.addEventListener('click', () => {
        addProductoVentaItem();
    });

    ventaForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const idUsuario = parseInt(idUsuarioVentaSelect.value);
        const idCliente = parseInt(idClienteVentaSelect.value);

        if (isNaN(idUsuario) || idUsuario <= 0) {
            showToast('Por favor, seleccione un usuario válido.', 'danger');
            return;
        }
        if (isNaN(idCliente) || idCliente <= 0) {
            showToast('Por favor, seleccione un cliente válido.', 'danger');
            return;
        }

        const productosDetalle = [];
        let allProductsValid = true;
        document.querySelectorAll('.producto-venta-item').forEach(itemElement => {
            const idProducto = parseInt(itemElement.querySelector('.producto-venta-select').value);
            const cantidad = parseInt(itemElement.querySelector('.cantidad-producto-venta').value);
            const porcentajeGanancia = parseFloat(itemElement.querySelector('.porcentaje-ganancia-venta').value);

            if (isNaN(idProducto) || idProducto <= 0 || isNaN(cantidad) || cantidad <= 0 || isNaN(porcentajeGanancia) || porcentajeGanancia < 0) {
                allProductsValid = false;
                showToast('Asegúrese de seleccionar un producto, una cantidad válida (mayor a 0) y un porcentaje de ganancia válido para cada ítem.', 'danger');
                return; 
            }
            productosDetalle.push({ idProducto, cantidad, porcentajeGanancia });
        });

        if (!allProductsValid || productosDetalle.length === 0) {
            if (productosDetalle.length === 0) {
                showToast('Debe agregar al menos un producto válido a la venta.', 'danger');
            }
            return; 
        }

        const ventaRequestDTO = {
            idUsuario: idUsuario,
            idCliente: idCliente,
            productos: productosDetalle
        };

        try {
            const response = await fetch(API_VENTAS_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ventaRequestDTO)
            });

            if (response.status === 400) {
                const errorBody = await response.json(); 
                showToast(`Error de datos: ${errorBody.message || 'Algunos datos son inválidos.'}`, 'danger');
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al guardar la venta: ${response.status} - ${errorText}`);
            }

            showToast('Venta registrada exitosamente.', 'success');
            const ventaModal = getVentaModalInstance();
            if (ventaModal) {
                ventaModal.hide(); // Oculta el modal
            }
            cargarVentas(); // Recargar la tabla de ventas
        } catch (error) {
            console.error('Error al guardar la venta:', error);
            showToast(`Error al guardar la venta: ${error.message}`, 'danger');
        }
    });

    // Evento para Ver Detalles de una Venta existente
    ventasTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-ver-venta') || event.target.closest('.btn-ver-venta')) {
            const id = event.target.dataset.id || event.target.closest('.btn-ver-venta').dataset.id;
            try {
                const response = await fetch(`${API_VENTAS_URL}/${id}`);
                if (!response.ok) throw new Error(`Error al obtener detalles de venta: ${response.statusText}`);
                const venta = await response.json();

                // Populate Modal for viewing
                modalTitle.textContent = `Detalles de Venta #${venta.idVenta}`;
                ventaIdInput.value = venta.idVenta;
                btnGuardarVenta.classList.add('d-none'); // Ocultar botón de guardar en modo solo ver

                // Deshabilitar selects y botón de añadir producto
                idUsuarioVentaSelect.setAttribute('disabled', 'true');
                idClienteVentaSelect.setAttribute('disabled', 'true');
                btnAgregarProductoAVenta.classList.add('d-none'); // Ocultar botón de añadir producto
                btnAgregarProductoAVenta.setAttribute('disabled', 'true');

                await cargarUsuariosEnSelect(venta.usuario ? venta.usuario.id : null);
                await cargarClientesEnSelect(venta.cliente ? venta.cliente.id : null);
                productosVentaContainer.innerHTML = ''; // Limpiar items existentes
                itemCounter = 0; // Resetear contador

                await cargarProductosDisponibles(); 

                venta.detalles.forEach(detalle => {
                    // For viewing mode, we want labels to be present for the first item, 
                    // then for subsequent items they'll be hidden.
                    // However, for consistency in view mode, it might be better to just display the values without labels
                    // if you prefer a more "table-like" view within the modal.
                    // For now, we'll keep the same logic as adding new items for simplicity.
                    addProductoVentaItem(detalle.producto.id, detalle.cantidad, null, detalle.precioUnitario, true); 
                });
                updateTotalVenta(); // Asegurarse de que el total se muestre correctamente

                const ventaModal = getVentaModalInstance();
                if (ventaModal) {
                    setTimeout(() => {
                        ventaModal.show();
                    }, 0);
                }
            } catch (error) {
                console.error('Error al cargar detalles de venta:', error);
                showToast('Error al cargar detalles de la venta.', 'danger');
            }
        }
    });

    // --- Initial Load ---
    cargarVentas();

} // End of initVentasCRUD.

document.addEventListener('DOMContentLoaded', initVentasCRUD);