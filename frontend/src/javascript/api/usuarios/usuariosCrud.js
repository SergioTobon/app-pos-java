// FRONTEND/src/javascript/api/usuarios/usuariosCrud.js

function initUsuariosCRUD() {
    const API_USUARIOS_URL = 'http://localhost:8080/api/usuarios';
    const API_ROLES_URL = 'http://localhost:8080/api/roles'; // Asumiendo que tienes un endpoint para roles

    const usuariosTableBody = document.getElementById('usuariosTableBody');
    const noUsuariosMessage = document.getElementById('noUsuariosMessage');

    const usuarioForm = document.getElementById('usuarioForm');
    const usuarioIdInput = document.getElementById('usuarioId');
    const dniUsuarioInput = document.getElementById('dniUsuario');
    const nombreUsuarioInput = document.getElementById('nombreUsuario');
    const apellidoUsuarioInput = document.getElementById('apellidoUsuario');
    const contactoUsuarioInput = document.getElementById('contactoUsuario');
    const idRolUsuarioSelect = document.getElementById('idRolUsuarioSelect');
    const passwordUsuarioInput = document.getElementById('passwordUsuario');
    const confirmPasswordUsuarioInput = document.getElementById('confirmPasswordUsuario');
    const passwordHint = document.getElementById('passwordHint');

    const btnGuardarUsuario = document.getElementById('btnGuardarUsuario');
    const modalTitle = document.getElementById('usuarioModalLabel');
    const btnAbrirModalAgregarUsuario = document.getElementById('btnAbrirModalAgregarUsuario');

    let allRoles = []; // Para almacenar todos los roles disponibles

    // --- Helper Functions ---
    function getUsuarioModalInstance() {
        const modalElement = document.getElementById('usuarioModal');
        if (modalElement) {
            return bootstrap.Modal.getInstance(modalElement) || new bootstrap.Modal(modalElement);
        }
        console.error("El elemento del modal #usuarioModal no fue encontrado.");
        return null;
    }

    function showToast(message, type = 'success') {
        console.log(`Mensaje (${type}): ${message}`);
        // Implementar un Toast de Bootstrap o un sistema de alertas aquí
        alert(message); // Para demostración, usar un alert simple
    }

    // --- Carga de datos para Selects ---
    async function cargarRolesEnSelect(selectedId = null) {
        try {
            const response = await fetch(API_ROLES_URL);
            if (!response.ok) throw new Error(`Error al cargar roles: ${response.statusText}`);
            allRoles = await response.json();

            idRolUsuarioSelect.innerHTML = '<option value="">Seleccione un rol</option>';
            allRoles.forEach(rol => {
                const option = document.createElement('option');
                option.value = rol.id; // Asumiendo que el ID del rol es 'id'
                option.textContent = rol.nombre; // Asumiendo que el nombre del rol es 'nombre'
                idRolUsuarioSelect.appendChild(option);
            });

            if (selectedId) {
                idRolUsuarioSelect.value = selectedId;
            }
        } catch (error) {
            console.error('Error al cargar roles:', error);
            showToast('Error al cargar la lista de roles.', 'danger');
        }
    }

    // --- Limpieza y Carga Inicial ---
    function limpiarFormularioUsuario() {
        usuarioIdInput.value = '';
        dniUsuarioInput.value = '';
        nombreUsuarioInput.value = '';
        apellidoUsuarioInput.value = '';
        contactoUsuarioInput.value = '';
        idRolUsuarioSelect.value = ''; // Resetear el select del rol
        passwordUsuarioInput.value = ''; // Limpiar campo de contraseña
        confirmPasswordUsuarioInput.value = ''; // Limpiar campo de confirmación
        
        modalTitle.textContent = 'Agregar Nuevo Usuario';
        btnGuardarUsuario.textContent = 'Guardar Usuario';

        // Asegurarse de que los campos de contraseña estén habilitados y visibles para un nuevo usuario
        passwordUsuarioInput.removeAttribute('disabled');
        passwordUsuarioInput.setAttribute('required', 'true'); // Contraseña es requerida para nuevo usuario
        confirmPasswordUsuarioInput.removeAttribute('disabled');
        confirmPasswordUsuarioInput.setAttribute('required', 'true');
        passwordHint.textContent = ''; // No hay hint de "dejar en blanco" para nuevo usuario
    }

    async function cargarUsuarios() {
        try {
            const response = await fetch(API_USUARIOS_URL);
            if (!response.ok) throw new Error(`Error al cargar usuarios: ${response.statusText}`);
            const usuarios = await response.json();

            usuariosTableBody.innerHTML = '';
            if (usuarios.length === 0) {
                noUsuariosMessage.classList.remove('d-none');
            } else {
                noUsuariosMessage.classList.add('d-none');
                usuarios.forEach(usuario => {
                    const row = usuariosTableBody.insertRow();
                    // Obtener el nombre del rol del usuario
                    const rolNombre = usuario.rol ? usuario.rol.nombre : 'N/A';

                    row.innerHTML = `
                        <td>${usuario.id}</td>
                        <td>${usuario.dni}</td>
                        <td>${usuario.nombre}</td>
                        <td>${usuario.apellido}</td>
                        <td>${usuario.contacto || 'N/A'}</td>
                        <td>${rolNombre}</td>
                        <td>
                            <button class="btn btn-sm btn-info btn-editar-usuario me-2" data-id="${usuario.id}" data-rol-id="${usuario.rol ? usuario.rol.id : ''}" data-rol-nombre="${rolNombre}">
                                <i class="bi bi-pencil"></i> Editar
                            </button>
                            <button class="btn btn-sm btn-danger btn-eliminar-usuario" data-id="${usuario.id}" data-rol-id="${usuario.rol ? usuario.rol.id : ''}" data-rol-nombre="${rolNombre}">
                                <i class="bi bi-trash"></i> Eliminar
                            </button>
                        </td>
                    `;
                    // Lógica para deshabilitar botones de editar/eliminar si el rol no es ADMIN y el usuario a editar es EMPLEADO
                    // ***** NOTA DE SEGURIDAD Y ROLES *****
                    // Aquí se simula la lógica del frontend. En un sistema real con autenticación,
                    // primero se obtendría el rol del usuario logueado. Luego, se usaría ese rol
                    // para decidir si mostrar o deshabilitar los botones.
                    // ¡La verdadera seguridad (quién puede hacer qué) DEBE ser implementada en el BACKEND!
                    // Por ahora, asumimos que si accedes a esta página, eres "ADMIN" para fines de prueba.
                    // Para la lógica de "solamente los que tengan rol de ADMIN puedan editar EMPLEADO":
                    // Esta lógica debe ir en el backend y aquí simplemente se reflejaría si se permitiera o no la acción.
                    // Dejaré los botones siempre visibles/habilitados por ahora para facilitar la prueba de CRUD completo,
                    // pero ten en cuenta que aquí es donde iría la lógica del frontend para ocultar/deshabilitar.
                });
            }
        } catch (error) {
            console.error('Error al cargar usuarios:', error);
            showToast(`Error al cargar usuarios: ${error.message}`, 'danger');
        }
    }

    // --- Event Listeners ---

    btnAbrirModalAgregarUsuario.addEventListener('click', async () => {
        limpiarFormularioUsuario();
        await cargarRolesEnSelect(); // Cargar roles al abrir el modal
        const usuarioModal = getUsuarioModalInstance();
        if (usuarioModal) {
            setTimeout(() => {
                usuarioModal.show();
            }, 0);
        }
    });

    usuarioForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        const id = usuarioIdInput.value ? parseInt(usuarioIdInput.value) : null;
        const dni = dniUsuarioInput.value;
        const nombre = nombreUsuarioInput.value;
        const apellido = apellidoUsuarioInput.value;
        const contacto = contactoUsuarioInput.value;
        const idRol = parseInt(idRolUsuarioSelect.value);
        const password = passwordUsuarioInput.value;
        const confirmPassword = confirmPasswordUsuarioInput.value;

        // Validaciones básicas del formulario
        if (!dni || !nombre || !apellido || !idRol) {
            showToast('Por favor, complete todos los campos requeridos (DNI, Nombre, Apellido, Rol).', 'danger');
            return;
        }

        // Validar contraseñas solo si se están ingresando o es un nuevo usuario
        if (password || !id) { // Si hay password ingresada O es un nuevo usuario
            if (password !== confirmPassword) {
                showToast('Las contraseñas no coinciden.', 'danger');
                return;
            }
            if (!id && !password) { // Si es nuevo usuario y no hay contraseña
                showToast('La contraseña es requerida para un nuevo usuario.', 'danger');
                return;
            }
        }
        
        // Crear el DTO de usuario
        const usuarioDTO = {
            id: id,
            dni: dni,
            nombre: nombre,
            apellido: apellido,
            contacto: contacto,
            rol: { id: idRol } // Solo necesitamos el ID del rol para el DTO
        };

        let url = API_USUARIOS_URL;
        let method;

        if (id) { // Actualizar usuario
            method = 'PUT';
            url = `${API_USUARIOS_URL}/${id}`;
            // Si el password está vacío, no se envía el parámetro 'password' en la URL
            // El backend manejará @RequestParam(required = false) String password
            if (password) { 
                url += `?password=${encodeURIComponent(password)}`;
            }
        } else { // Crear nuevo usuario
            method = 'POST';
            url += `?password=${encodeURIComponent(password)}`; // La contraseña es requerida para POST
        }

        try {
            const response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(usuarioDTO)
            });

            if (response.status === 400) {
                const errorBody = await response.text(); // A veces el error es un string simple
                showToast(`Error de datos: ${errorBody || 'Algunos datos son inválidos.'}`, 'danger');
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al guardar el usuario: ${response.status} - ${errorText}`);
            }

            showToast(`Usuario ${id ? 'actualizado' : 'registrado'} exitosamente.`, 'success');
            const usuarioModal = getUsuarioModalInstance();
            if (usuarioModal) {
                usuarioModal.hide(); // Oculta el modal
            }
            cargarUsuarios(); // Recargar la tabla de usuarios
        } catch (error) {
            console.error('Error al guardar el usuario:', error);
            showToast(`Error al guardar el usuario: ${error.message}`, 'danger');
        }
    });

    // Evento para Editar un Usuario existente
    usuariosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-editar-usuario') || event.target.closest('.btn-editar-usuario')) {
            const id = event.target.dataset.id || event.target.closest('.btn-editar-usuario').dataset.id;
            const targetUserRolNombre = event.target.dataset.rolNombre || event.target.closest('.btn-editar-usuario').dataset.rolNombre;
            // ***** LÓGICA DE ROLES PARA EDICIÓN EN FRONTEND (EJEMPLO) *****
            // Asume que el usuario logueado es 'ADMIN' (para fines de prueba sin login)
            // Si tuvieras un usuario logueado, obtendrías su rol de alguna parte (localStorage, etc.)
            const currentUserRol = 'ADMIN'; // Simulación: asumimos que el usuario actual es ADMIN

            if (currentUserRol === 'ADMIN') {
                // ADMIN puede editar a todos, incluyendo a EMPLEADO
                // No hay restricción extra aquí para ADMIN editando EMPLEADO.
            } else if (currentUserRol === 'EMPLEADO') {
                // Un empleado NO debería poder editar a otros usuarios ni a sí mismo en esta lógica
                // (a menos que se añada un 'editar mi perfil')
                showToast('No tienes permisos para editar usuarios.', 'danger');
                return;
            }
            // ***************************************************************

            try {
                const response = await fetch(`${API_USUARIOS_URL}/${id}`);
                if (!response.ok) throw new Error(`Error al obtener detalles de usuario: ${response.statusText}`);
                const usuario = await response.json();

                modalTitle.textContent = `Editar Usuario: ${usuario.nombre} ${usuario.apellido}`;
                btnGuardarUsuario.textContent = 'Actualizar Usuario';

                usuarioIdInput.value = usuario.id;
                dniUsuarioInput.value = usuario.dni;
                nombreUsuarioInput.value = usuario.nombre;
                apellidoUsuarioInput.value = usuario.apellido;
                contactoUsuarioInput.value = usuario.contacto || ''; // Manejar nulos
                
                // Cargar roles y seleccionar el rol actual del usuario
                await cargarRolesEnSelect(usuario.rol ? usuario.rol.id : null);

                // Limpiar campos de contraseña y establecer el hint
                passwordUsuarioInput.value = '';
                confirmPasswordUsuarioInput.value = '';
                passwordUsuarioInput.removeAttribute('required'); // Contraseña no es requerida para actualización
                confirmPasswordUsuarioInput.removeAttribute('required');
                passwordHint.textContent = 'Dejar en blanco para no cambiar la contraseña.';

                const usuarioModal = getUsuarioModalInstance();
                if (usuarioModal) {
                    setTimeout(() => {
                        usuarioModal.show();
                    }, 0);
                }
            } catch (error) {
                console.error('Error al cargar detalles de usuario:', error);
                showToast('Error al cargar detalles del usuario.', 'danger');
            }
        }
    });

    // Evento para Eliminar un Usuario
    usuariosTableBody.addEventListener('click', async (event) => {
        if (event.target.classList.contains('btn-eliminar-usuario') || event.target.closest('.btn-eliminar-usuario')) {
            const id = event.target.dataset.id || event.target.closest('.btn-eliminar-usuario').dataset.id;
            const targetUserRolNombre = event.target.dataset.rolNombre || event.target.closest('.btn-eliminar-usuario').dataset.rolNombre;

            // ***** LÓGICA DE ROLES PARA ELIMINACIÓN EN FRONTEND (EJEMPLO) *****
            // Asume que el usuario logueado es 'ADMIN' (para fines de prueba sin login)
            const currentUserRol = 'ADMIN'; 

            if (currentUserRol === 'ADMIN') {
                // ADMIN puede eliminar a EMPLEADO.
                // Si quisieras evitar que un ADMIN elimine a otro ADMIN:
                // if (targetUserRolNombre === 'ADMIN') {
                //     showToast('Un administrador no puede eliminar a otro administrador directamente desde aquí.', 'danger');
                //     return;
                // }
            } else if (currentUserRol === 'EMPLEADO') {
                showToast('No tienes permisos para eliminar usuarios.', 'danger');
                return;
            }
            // ***************************************************************

            if (!confirm(`¿Está seguro de que desea eliminar el usuario con ID: ${id}?`)) {
                return; // Si el usuario cancela, no hacer nada
            }

            try {
                const response = await fetch(`${API_USUARIOS_URL}/${id}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al eliminar el usuario: ${response.status} - ${errorText}`);
                }

                showToast('Usuario eliminado exitosamente.', 'success');
                cargarUsuarios(); // Recargar la tabla de usuarios
            } catch (error) {
                console.error('Error al eliminar usuario:', error);
                showToast(`Error al eliminar usuario: ${error.message}`, 'danger');
            }
        }
    });

    // --- Initial Load ---
    cargarUsuarios();

} // End of initUsuariosCRUD.

document.addEventListener('DOMContentLoaded', initUsuariosCRUD);