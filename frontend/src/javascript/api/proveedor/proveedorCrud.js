// Remove the DOMContentLoaded listener from here
// document.addEventListener('DOMContentLoaded', function () { // REMOVE THIS LINE AND ITS CLOSING BRACKET

const API_BASE_URL = 'http://localhost:8080/api/proveedores';
const proveedoresTableBody = document.getElementById('proveedoresTableBody');
const proveedorModalElement = document.getElementById('proveedorModal'); // Get the element first
const proveedorModal = new bootstrap.Modal(proveedorModalElement); // Then initialize Bootstrap Modal
const proveedorForm = document.getElementById('proveedorForm');
const proveedorIdInput = document.getElementById('proveedorId');
const nombreInput = document.getElementById('nombre');
const nitInput = document.getElementById('nit');
const contactoInput = document.getElementById('contacto');
const direccionInput = document.getElementById('direccion');
const emailInput = document.getElementById('email');
const telefonoInput = document.getElementById('telefono');
const modalTitle = document.getElementById('proveedorModalLabel');
const btnAbrirModalAgregar = document.getElementById('btnAbrirModalAgregar');
const noProveedoresMessage = document.getElementById('noProveedoresMessage');

const searchInput = document.getElementById('searchInput');
const filterSelect = document.getElementById('filterSelect');
const clearSearchButton = document.getElementById('clearSearchButton');

let allProveedores = [];

function showToast(message, type = 'success') {
    // Implement a proper toast notification system here if needed
    console.log(`Mensaje (${type}): ${message}`);
}

function renderProveedores(proveedoresToRender) {
    proveedoresTableBody.innerHTML = '';
    if (proveedoresToRender.length === 0) {
        noProveedoresMessage.classList.remove('d-none');
    } else {
        noProveedoresMessage.classList.add('d-none');
        proveedoresToRender.forEach(proveedor => {
            const row = proveedoresTableBody.insertRow();
            row.innerHTML = `
                <td>${proveedor.id}</td>
                <td>${proveedor.nombre}</td>
                <td>${proveedor.nit}</td>
                <td>${proveedor.contacto || ''}</td>
                <td>${proveedor.direccion || ''}</td>
                <td>${proveedor.email || ''}</td>
                <td>${proveedor.telefono || ''}</td>
                <td>
                    <button class="btn btn-sm btn-info btn-editar me-2" data-id="${proveedor.id}">
                        <i class="bi bi-pencil-square"></i> Editar
                    </button>
                    <button class="btn btn-sm btn-danger btn-eliminar" data-id="${proveedor.id}">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            `;
        });
    }
}

async function cargarProveedores() {
    try {
        const response = await fetch(API_BASE_URL);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        allProveedores = await response.json();
        applyFiltersAndSearch();
    } catch (error) {
        console.error('Error al cargar proveedores:', error);
        showToast('Error al cargar proveedores.', 'danger');
    }
}

function applyFiltersAndSearch() {
    let filteredProveedores = [...allProveedores];
    const searchTerm = searchInput.value.toLowerCase().trim();
    const filterBy = filterSelect.value;

    if (searchTerm) {
        filteredProveedores = filteredProveedores.filter(proveedor =>
            proveedor.nombre.toLowerCase().includes(searchTerm)
        );
    }

    // This part of the filter seems a bit redundant if searchTerm already filtered by name.
    // If you want to filter by other fields, you might need to adjust the logic.
    // For example, if searchTerm is present, it will filter by name AND then try to filter by the selected field with the same searchTerm.
    // If you intend for the select to be a primary filter *or* an additional filter, adjust accordingly.
    // For now, I'll keep it as is based on your original code's logic.
    if (filterBy) {
        filteredProveedores = filteredProveedores.filter(proveedor =>
            proveedor[filterBy] && proveedor[filterBy].toLowerCase().includes(searchTerm)
        );
    }

    renderProveedores(filteredProveedores);
}

// Add event listeners AFTER the elements are guaranteed to be in the DOM
searchInput.addEventListener('input', applyFiltersAndSearch);
filterSelect.addEventListener('change', applyFiltersAndSearch);
clearSearchButton.addEventListener('click', () => {
    searchInput.value = '';
    filterSelect.value = '';
    applyFiltersAndSearch();
});

function limpiarFormulario() {
    proveedorIdInput.value = '';
    nombreInput.value = '';
    nitInput.value = '';
    contactoInput.value = '';
    direccionInput.value = '';
    emailInput.value = '';
    telefonoInput.value = '';
    modalTitle.textContent = 'Agregar Nuevo Proveedor';
}

btnAbrirModalAgregar.addEventListener('click', () => {
    limpiarFormulario();
    proveedorModal.show();
});

proveedorForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const id = proveedorIdInput.value;
    const proveedorData = {
        nombre: nombreInput.value,
        nit: nitInput.value,
        // Ensure that empty strings are sent as null or omitted if your backend expects that for optional fields
        contacto: contactoInput.value || null,
        direccion: direccionInput.value || null,
        email: emailInput.value || null,
        telefono: telefonoInput.value || null
    };

    try {
        let response;
        if (id) {
            response = await fetch(`${API_BASE_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(proveedorData)
            });
            if (!response.ok) {
                // Read error message from backend if available
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al actualizar proveedor: ${errorData.message || response.statusText}`);
            }
            showToast('Proveedor actualizado exitosamente.');
        } else {
            response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(proveedorData)
            });
            if (!response.ok) {
                 // Read error message from backend if available
                const errorData = await response.json().catch(() => ({ message: response.statusText }));
                throw new Error(`Error al agregar proveedor: ${errorData.message || response.statusText}`);
            }
            showToast('Proveedor agregado exitosamente.');
        }

        proveedorModal.hide();
        limpiarFormulario();
        cargarProveedores(); // Reload data after successful operation
    } catch (error) {
        console.error('Error al guardar proveedor:', error);
        showToast(`Error al guardar proveedor: ${error.message}`, 'danger');
    }
});

proveedoresTableBody.addEventListener('click', async (event) => {
    const editarBtn = event.target.closest('.btn-editar');
    const eliminarBtn = event.target.closest('.btn-eliminar');

    if (editarBtn) {
        const id = editarBtn.dataset.id;
        try {
            const response = await fetch(`${API_BASE_URL}/${id}`);
            if (!response.ok) {
                throw new Error(`Error al obtener proveedor: ${response.statusText}`);
            }
            const proveedor = await response.json();
            proveedorIdInput.value = proveedor.id;
            nombreInput.value = proveedor.nombre;
            nitInput.value = proveedor.nit;
            contactoInput.value = proveedor.contacto || '';
            direccionInput.value = proveedor.direccion || '';
            emailInput.value = proveedor.email || '';
            telefonoInput.value = proveedor.telefono || '';
            modalTitle.textContent = 'Editar Proveedor';
            proveedorModal.show();
        } catch (error) {
            console.error('Error al cargar datos del proveedor para edición:', error);
            showToast('Error al cargar datos del proveedor para edición.', 'danger');
        }
    }

    if (eliminarBtn) {
        const id = eliminarBtn.dataset.id;
        if (confirm(`¿Estás seguro de que quieres eliminar el proveedor con ID ${id}?`)) {
            try {
                const response = await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
                if (!response.ok) {
                    throw new Error(`Error al eliminar proveedor: ${response.statusText}`);
                }
                showToast('Proveedor eliminado exitosamente.');
                cargarProveedores(); // Reload data after successful deletion
            } catch (error) {
                console.error('Error al eliminar proveedor:', error);
                showToast('Error al eliminar proveedor.', 'danger');
            }
        }
    }
});

// Initial load of suppliers when the script executes (which is after injectLayout has run)
cargarProveedores();

// }); // REMOVE THIS LINE