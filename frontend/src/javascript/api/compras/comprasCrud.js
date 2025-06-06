// FRONTEND/src/javascript/api/compras/comprasCrud.js

function initComprasCRUD() {
    // --- Configuración de la API ---
    const API_BASE_URL = 'http://localhost:8080/api';
    const API_COMPRAS_URL = `${API_BASE_URL}/compras`;
    const API_PRODUCTOS_URL = `${API_BASE_URL}/productos`; // Aún necesario para detalles de compra
    const API_PROVEEDORES_URL = `${API_BASE_URL}/proveedores`; // Aún necesario para detalles de compra

    // --- Referencias a elementos HTML (con verificaciones iniciales) ---
    const comprasTableBody = document.getElementById('comprasTableBody');
    const noComprasMessage = document.getElementById('noComprasMessage');
    const compraForm = document.getElementById('compraForm'); // Necesario para el modal de detalles
    const compraIdInput = document.getElementById('compraId');
    const idProveedorCompraSelect = document.getElementById('idProveedorCompraSelect');
    const productosCompraContainer = document.getElementById('productosCompraContainer');
    const totalCompraDisplay = document.getElementById('totalCompraDisplay');
    const modalTitle = document.getElementById('compraModalLabel');

    // Aquí eliminamos 'btnAbrirModalAgregarCompra', 'btnAgregarProductoACart', 'btnGuardarCompra'
    // de las comprobaciones críticas, ya que no los usaremos para "agregar".
    if (!comprasTableBody || !compraForm || !idProveedorCompraSelect || !productosCompraContainer ||
        !totalCompraDisplay || !modalTitle) {
        console.error("Uno o más elementos HTML esenciales para la visualización de compras no fueron encontrados. Asegúrate de que los IDs en tu HTML coincidan.");
        return; // Detiene la ejecución si los elementos críticos no están.
    }

    let allProducts = []; // Para almacenar todos los productos disponibles (útil para detalles)

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
            alert(message); // Fallback para mostrar el mensaje
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

    // --- Carga de datos para Selects (aún necesaria para el modal de detalles) ---

    async function cargarProveedoresEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_PROVEEDORES_URL);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al cargar proveedores: ${response.status} - ${errorText}`);
            }
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
            showToast(`Error al cargar la lista de proveedores: ${error.message}`, 'danger');
        }
    }

    async function cargarProductosDisponibles() {
        try {
            const response = await fetch(API_PRODUCTOS_URL);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al cargar productos: ${response.status} - ${errorText}`);
            }
            allProducts = await response.json(); // Guardar todos los productos
        } catch (error) {
            console.error('Error al cargar productos disponibles:', error);
            showToast(`Error al cargar la lista de productos disponibles: ${error.message}`, 'danger');
            allProducts = []; // Asegura que allProducts esté vacío en caso de error
        }
    }

    // Adaptamos esta función para solo mostrar, no para permitir selección/cambio.
    function addProductoCompraItem(productoData, cantidad, precioUnitario) {
        const newItemDiv = document.createElement('div');
        newItemDiv.classList.add('row', 'g-2', 'mb-2', 'producto-compra-item');
        // No necesitamos un itemCounter si no vamos a agregar/eliminar dinámicamente

        newItemDiv.innerHTML = `
            <div class="col-md-4">
                <label class="form-label visually-hidden">Producto</label>
                <input type="text" class="form-control" value="${productoData.nombre || 'N/A'}" disabled>
            </div>
            <div class="col-md-3">
                <label class="form-label visually-hidden">Cantidad</label>
                <input type="text" class="form-control" value="${cantidad || 0}" disabled>
            </div>
            <div class="col-md-3">
                <label class="form-label visually-hidden">Precio Unitario</label>
                <input type="text" class="form-control" value="${currencyFormatter.format(precioUnitario || 0)}" disabled>
            </div>
            <div class="col-md-2 d-flex align-items-center">
                </div>
        `;
        productosCompraContainer.appendChild(newItemDiv);
    }

    // La función updateTotalCompra se mantiene para calcular el total de los detalles mostrados
    function updateTotalCompra() {
        let total = 0;
        document.querySelectorAll('.producto-compra-item').forEach(itemElement => {
            // Ya que los inputs están deshabilitados, leemos directamente el valor.
            // Para asegurar la precisión, podrías almacenar el valor numérico en un data-attribute
            // cuando se genera el item, o parsearlo del texto formateado.
            // Para simplificar, asumimos que el texto formateado se puede parsear limpiándolo.
            const cantidad = parseFloat(itemElement.querySelector('div:nth-child(2) input').value.replace(/[^\d.]/g, ''));
            const precioUnitarioText = itemElement.querySelector('div:nth-child(3) input').value;
            const cleanText = precioUnitarioText.replace(/[^\d,\.]+/g, '').replace(',', '.');
            const precioUnitario = parseFloat(cleanText);

            if (!isNaN(cantidad) && cantidad > 0 && !isNaN(precioUnitario) && precioUnitario >= 0) {
                total += precioUnitario * cantidad;
            }
        });
        totalCompraDisplay.textContent = currencyFormatter.format(total);
    }


    // La función limpiarFormularioCompra se simplifica, ya no se prepara para "agregar".
    function limpiarFormularioCompra() {
        compraIdInput.value = '';
        idProveedorCompraSelect.value = '';
        productosCompraContainer.innerHTML = '';
        totalCompraDisplay.textContent = currencyFormatter.format(0);
        modalTitle.textContent = 'Detalles de Compra'; // Siempre mostrar "Detalles"
        
        // Deshabilitar todos los inputs relevantes en el modal de detalles
        idProveedorCompraSelect.setAttribute('disabled', 'true');
        // Si hay otros elementos del formulario, asegúrate de deshabilitarlos también.
    }

    async function cargarCompras() {
        try {
            const response = await fetch(API_COMPRAS_URL);
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al cargar compras: ${response.status} - ${errorText}`);
            }
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
    // ELIMINADO: Event listener para btnAbrirModalAgregarCompra.

    // ELIMINADO: Event listener para btnAgregarProductoACart.

    // ELIMINADO: Event listener para el submit de compraForm (ya no se usa para guardar).
    // Ahora el formulario solo se usará para mostrar detalles.
    if (compraForm) {
        compraForm.addEventListener('submit', (event) => {
            event.preventDefault(); // Evitar el envío del formulario si accidentalmente se presiona Enter
            console.log('El formulario de compra está en modo de solo lectura. No se guarda nada.');
        });
    }

    // Evento para Ver Detalles de una Compra existente (se mantiene).
    if (comprasTableBody) {
        comprasTableBody.addEventListener('click', async (event) => {
            if (event.target.classList.contains('btn-ver-compra') || event.target.closest('.btn-ver-compra')) {
                const id = event.target.dataset.id || event.target.closest('.btn-ver-compra').dataset.id;
                try {
                    const response = await fetch(`${API_COMPRAS_URL}/${id}`);
                    if (!response.ok) {
                        const errorText = await response.text();
                        throw new Error(`Error al obtener detalles de compra: ${response.status} - ${errorText}`);
                    }
                    const compra = await response.json();

                    limpiarFormularioCompra(); // Limpia y prepara el modal para solo visualización
                    modalTitle.textContent = `Detalles de Compra #${compra.idCompra}`;
                    compraIdInput.value = compra.idCompra;

                    // Cargar proveedores y seleccionar el proveedor de la compra
                    await cargarProveedoresEnSelect(compra.proveedor ? compra.proveedor.id : null);
                    idProveedorCompraSelect.setAttribute('disabled', 'true'); // Asegura que esté deshabilitado

                    // Cargar productos disponibles (necesario si quieres mostrar nombres completos, etc.)
                    await cargarProductosDisponibles(); 

                    productosCompraContainer.innerHTML = ''; // Limpia el contenedor de productos

                    compra.detalles.forEach(detalle => {
                        // Encuentra el producto para obtener su nombre completo
                        const producto = allProducts.find(p => p.id === detalle.producto.id) || detalle.producto;
                        addProductoCompraItem(producto, detalle.cantidad, detalle.precioCompra); 
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
                    showToast(`Error al cargar detalles de la compra: ${error.message}`, 'danger');
                }
            }
        });
    } else {
        console.warn("Elemento 'comprasTableBody' no encontrado. La funcionalidad de ver detalles de compras no estará disponible.");
    }

    // --- Carga Inicial ---
    cargarCompras();

} // Fin de initComprasCRUD.

// Asegúrate de que initComprasCRUD se ejecute una vez que el DOM esté completamente cargado.
document.addEventListener('DOMContentLoaded', initComprasCRUD);