document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const dniInput = document.getElementById('dni');
    const passwordInput = document.getElementById('password');
    const messageDiv = document.getElementById('message');

    const API_LOGIN_URL = 'http://localhost:8080/api/auth/login';

    loginForm.addEventListener('submit', async (event) => {
        event.preventDefault();

        messageDiv.classList.add('d-none');
        const dni = dniInput.value;
        const password = passwordInput.value;

        if (!dni || !password) {
            showMessage('Por favor, ingresa tu DNI y contraseña.', 'danger');
            return;
        }

        try {
            const response = await fetch(API_LOGIN_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ dni, password })
            });

            if (response.ok) {
                const data = await response.json();
                showMessage('¡Inicio de sesión exitoso! Redirigiendo...', 'success');
                localStorage.setItem('currentUser', JSON.stringify(data)); 

                if (data.rol && data.rol.nombre) {
                    const userRole = data.rol.nombre.toUpperCase();

                    setTimeout(() => {
                        let redirectUrl; // Variable para la URL de redirección
                        if (userRole === 'ADMIN') {
                            redirectUrl = '/src/admin/dashboard.html'; 
                        } else if (userRole === 'EMPLEADO') {
                            redirectUrl = '/src/employee/dashboard.html';
                        } else {
                            redirectUrl = '/src/dashboard.html'; 
                        }
                        // *** CAMBIO AQUÍ: Usamos window.location.replace() ***
                        window.location.replace(redirectUrl); 
                    }, 1500); 

                } else {
                    showMessage('Login exitoso, pero el rol del usuario no está definido. Redirigiendo a default.', 'warning');
                    setTimeout(() => {
                        // *** CAMBIO AQUÍ: Usamos window.location.replace() ***
                        window.location.replace('/src/dashboard.html');
                    }, 1500);
                }

            } else { 
                let errorMessage = 'Error desconocido al iniciar sesión.';
                try {
                    const errorData = await response.json();
                    errorMessage = errorData.message || 'Error del servidor.';
                } catch (jsonError) {
                    errorMessage = `Error ${response.status}: ${response.statusText || 'Respuesta inesperada del servidor.'}`;
                    if (response.status === 401) {
                        errorMessage = 'Credenciales inválidas. Por favor, verifica tu DNI y contraseña.';
                    } else if (response.status === 500) {
                        errorMessage = 'Error interno del servidor. Inténtalo de nuevo más tarde.';
                    }
                }
                showMessage(errorMessage, 'danger');
            }

        } catch (error) {
            console.error('Error al intentar iniciar sesión:', error);
            showMessage('Error de conexión o problema en el servidor. Inténtalo de nuevo más tarde.', 'danger');
        }
    });

    function showMessage(msg, type) {
        if (messageDiv) {
            messageDiv.textContent = msg;
            messageDiv.classList.remove('d-none', 'alert-success', 'alert-danger', 'alert-warning');
            messageDiv.classList.add(`alert-${type}`);
        } else {
            console.error('Error: el elemento "messageDiv" no se encontró para mostrar el mensaje:', msg);
        }
    }
});