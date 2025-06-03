// FRONTEND/src/admin/proveedores.js

document.addEventListener('DOMContentLoaded', function() {
    const API_BASE_URL = 'http://localhost:8080/api/proveedores'; // URL de tu API
    const proveedoresTableBody = document.getElementById('proveedoresTableBody');
    const proveedorModal = new bootstrap.Modal(document.getElementById('proveedorModal'));
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

    // Función para mostrar mensajes (Toast de Bootstrap, si lo configuras)
    function showToast(message, type = 'success') {
        // Aquí podrías integrar un sistema de Toast de Bootstrap o una alerta simple
        console.log(`Mensaje (${type}): ${message}`);
        // Ejemplo de alerta simple:
        // alert(message);
    }

    // Función para obtener y mostrar todos los proveedores
    async function cargarProveedores() {
        try {
            const response = await fetch(API_BASE_URL);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const proveedores = await response.json();
            proveedoresTableBody.innerHTML = ''; // Limpiar tabla antes de cargar
            if (proveedores.length === 0) {
                noProveedoresMessage.classList.remove('d-none');
            } else {
                noProveedoresMessage.classList.add('d-none');
                proveedores.forEach(proveedor => {
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
        } catch (error) {
            console.error('Error al cargar proveedores:', error);
            showToast('Error al cargar proveedores.', 'danger');
        }
    }

    // Función para limpiar el formulario
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

    // Evento para abrir el modal de agregar
    btnAbrirModalAgregar.addEventListener('click', () => {
        limpiarFormulario();
        proveedorModal.show();
    });

    // Evento para guardar (agregar o actualizar) un proveedor
    proveedorForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const id = proveedorIdInput.value;
        const proveedorData = {
            nombre: nombreInput.value,
            nit: nitInput.value,
            contacto: contactoInput.value,
            direccion: direccionInput.value,
            email: emailInput.value,
            telefono: telefonoInput.value
        };

        try {
            let response;
            if (id) { // Actualizar
                response = await fetch(`${API_BASE_URL}/${id}`, {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(proveedorData)
                });
                if (!response.ok) {
                    throw new Error(`Error al actualizar proveedor: ${response.statusText}`);
                }
                showToast('Proveedor actualizado exitosamente.');
            } else { // Crear
                response = await fetch(API_BASE_URL, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(proveedorData)
                });
                if (!response.ok) {
                    throw new Error(`Error al agregar proveedor: ${response.statusText}`);
                }
                showToast('Proveedor agregado exitosamente.');
            }

            proveedorModal.hide();
            limpiarFormulario();
            cargarProveedores(); // Recargar la tabla
        } catch (error) {
            console.error('Error al guardar proveedor:', error);
            showToast(`Error al guardar proveedor: ${error.message}`, 'danger');
        }
    });

    // Evento para editar un proveedor (delegación de eventos)
    proveedoresTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-editar') || event.target.closest('.btn-editar')) {
            const id = event.target.dataset.id || event.target.closest('.btn-editar').dataset.id;
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
    });

    // Evento para eliminar un proveedor (delegación de eventos)
    proveedoresTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-eliminar') || event.target.closest('.btn-eliminar')) {
            const id = event.target.dataset.id || event.target.closest('.btn-eliminar').dataset.id;
            if (confirm(`¿Estás seguro de que quieres eliminar el proveedor con ID ${id}?`)) {
                try {
                    const response = await fetch(`${API_BASE_URL}/${id}`, {
                        method: 'DELETE'
                    });
                    if (!response.ok) {
                        throw new Error(`Error al eliminar proveedor: ${response.statusText}`);
                    }
                    showToast('Proveedor eliminado exitosamente.');
                    cargarProveedores(); // Recargar la tabla
                } catch (error) {
                    console.error('Error al eliminar proveedor:', error);
                    showToast('Error al eliminar proveedor.', 'danger');
                }
            }
        }
    });

    // Cargar proveedores al iniciar la página
    cargarProveedores();
});