// Variable para el mensaje de notificación (asumo que tienes una función `mostrarMensaje` global)
// Si no la tienes, necesitarías implementarla o adaptar esta parte.
// Ejemplo: function mostrarMensaje(tipo, mensaje) { /* tu lógica aquí */ }

document.addEventListener('DOMContentLoaded', () => {
    const CLIENTES_API_URL = 'http://localhost:8080/api/clientes'; // URL de tu API de clientes
    const clientesTableBody = document.getElementById('clientesTableBody');
    const noClientesMessage = document.getElementById('noClientesMessage');
    const clienteModal = new bootstrap.Modal(document.getElementById('clienteModal'));
    const clienteForm = document.getElementById('clienteForm');
    const clienteIdInput = document.getElementById('clienteId');
    const dniInput = document.getElementById('dni');
    const nombreInput = document.getElementById('nombreCliente'); // Usamos nombreCliente para evitar conflicto
    const contactoInput = document.getElementById('contactoCliente'); // Usamos contactoCliente para evitar conflicto
    const clienteModalLabel = document.getElementById('clienteModalLabel');
    const btnAbrirModalAgregarCliente = document.getElementById('btnAbrirModalAgregarCliente');


    // --- Funciones de Utilidad ---

    // Función para mostrar mensajes de alerta (asumiendo que tienes una función global)
    // Reemplaza con tu implementación si es diferente
    function mostrarMensaje(tipo, mensaje) {
        const messageContainer = document.getElementById('messageContainer') || document.createElement('div');
        if (!document.getElementById('messageContainer')) {
            messageContainer.id = 'messageContainer';
            message.container.classList.add('fixed-top', 'p-3');
            document.body.appendChild(messageContainer);
        }
        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${tipo} alert-dismissible fade show`;
        alertDiv.setAttribute('role', 'alert');
        alertDiv.innerHTML = `
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        `;
        messageContainer.appendChild(alertDiv);

        // Auto-cerrar el mensaje después de unos segundos
        setTimeout(() => {
            bootstrap.Alert.getInstance(alertDiv)?.close();
        }, 5000);
    }


    // --- Cargar Clientes ---

    async function cargarClientes() {
        try {
            const response = await fetch(CLIENTES_API_URL);
            if (!response.ok) {
                // Si la respuesta no es 2xx, lanza un error con el mensaje del servidor
                const errorData = await response.text(); // O .json() si el backend devuelve JSON para errores
                throw new Error(`Error al cargar clientes: ${errorData || response.statusText}`);
            }
            const clientes = await response.json();
            mostrarClientesEnTabla(clientes);
        } catch (error) {
            console.error('Error al cargar clientes:', error);
            mostrarMensaje('danger', `Error al cargar la lista de clientes: ${error.message}`);
            clientesTableBody.innerHTML = ''; // Limpiar la tabla en caso de error
            noClientesMessage.classList.remove('d-none');
        }
    }

    function mostrarClientesEnTabla(clientes) {
        clientesTableBody.innerHTML = ''; // Limpiar tabla
        if (clientes.length === 0) {
            noClientesMessage.classList.remove('d-none');
            return;
        }
        noClientesMessage.classList.add('d-none');

        clientes.forEach(cliente => {
            const row = clientesTableBody.insertRow();
            row.innerHTML = `
                <td>${cliente.id}</td>
                <td>${cliente.dni}</td>
                <td>${cliente.nombre}</td>
                <td>${cliente.contacto || 'N/A'}</td>
                <td>
                    <button class="btn btn-info btn-sm btn-editar-cliente" data-id="${cliente.id}">
                        <i class="bi bi-pencil-square"></i> Editar
                    </button>
                    <button class="btn btn-danger btn-sm btn-eliminar-cliente" data-id="${cliente.id}">
                        <i class="bi bi-trash"></i> Eliminar
                    </button>
                </td>
            `;
        });

        // Añadir listeners a los botones de editar y eliminar
        document.querySelectorAll('.btn-editar-cliente').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.currentTarget.dataset.id;
                cargarClienteParaEdicion(id);
            });
        });

        document.querySelectorAll('.btn-eliminar-cliente').forEach(button => {
            button.addEventListener('click', (e) => {
                const id = e.currentTarget.dataset.id;
                if (confirm('¿Estás seguro de que quieres eliminar este cliente?')) {
                    eliminarCliente(id);
                }
            });
        });
    }

    // --- Guardar/Actualizar Cliente ---

    btnAbrirModalAgregarCliente.addEventListener('click', () => {
        clienteForm.reset(); // Limpiar el formulario
        clienteIdInput.value = ''; // Asegurarse de que el ID esté vacío para "agregar"
        clienteModalLabel.textContent = 'Agregar Nuevo Cliente';
    });

    clienteForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const clienteData = {
            id: clienteIdInput.value ? parseInt(clienteIdInput.value) : null,
            dni: dniInput.value.trim(),
            nombre: nombreInput.value.trim(),
            contacto: contactoInput.value.trim()
        };

        try {
            const url = clienteData.id ? `${CLIENTES_API_URL}/${clienteData.id}` : CLIENTES_API_URL;
            const method = clienteData.id ? 'PUT' : 'POST';

            const response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    // Si necesitas autenticación, añádela aquí:
                    // 'Authorization': `Bearer ${localStorage.getItem('token')}`
                },
                body: JSON.stringify(clienteData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al ${clienteData.id ? 'actualizar' : 'guardar'} el cliente: ${errorText || response.statusText}`);
            }

            clienteModal.hide(); // Cerrar el modal
            cargarClientes(); // Recargar la lista de clientes
            mostrarMensaje('success', `Cliente ${clienteData.id ? 'actualizado' : 'agregado'} exitosamente.`);

        } catch (error) {
            console.error(`Error al procesar el cliente:`, error);
            mostrarMensaje('danger', `No se pudo ${clienteData.id ? 'actualizar' : 'guardar'} el cliente: ${error.message}`);
        }
    });

    // --- Cargar Cliente para Edición ---

    async function cargarClienteParaEdicion(id) {
        try {
            const response = await fetch(`${CLIENTES_API_URL}/${id}`);
            if (!response.ok) {
                const errorData = await response.text();
                throw new Error(`Error al cargar los datos del cliente para edición: ${errorData || response.statusText}`);
            }
            const cliente = await response.json();

            clienteIdInput.value = cliente.id;
            dniInput.value = cliente.dni;
            nombreInput.value = cliente.nombre;
            contactoInput.value = cliente.contacto;

            clienteModalLabel.textContent = 'Editar Cliente';
            clienteModal.show();

        } catch (error) {
            console.error('Error al cargar cliente para edición:', error);
            mostrarMensaje('danger', `No se pudo cargar el cliente para edición: ${error.message}`);
        }
    }

    // --- Eliminar Cliente ---

    async function eliminarCliente(id) {
        try {
            const response = await fetch(`${CLIENTES_API_URL}/${id}`, {
                method: 'DELETE',
                headers: {
                    // Si necesitas autenticación, añádela aquí:
                    // 'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al eliminar el cliente: ${errorText || response.statusText}`);
            }

            mostrarMensaje('success', 'Cliente eliminado exitosamente.');
            cargarClientes(); // Recargar la lista de clientes

        } catch (error) {
            console.error('Error al eliminar cliente:', error);
            mostrarMensaje('danger', `No se pudo eliminar el cliente: ${error.message}`);
        }
    }

    // --- Carga inicial de datos ---
    cargarClientes();
});