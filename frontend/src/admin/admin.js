// frontend/src/admin/admin.js

document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');
    const sidebarLogoutButton = document.getElementById('sidebarLogoutButton');
    const userNameDisplay = document.getElementById('userNameDisplay');

    // Recuperar información del usuario (asumiendo que se guardó en localStorage)
    const currentUser = JSON.parse(localStorage.getItem('currentUser'));

    if (currentUser && currentUser.nombre) {
        userNameDisplay.textContent = currentUser.nombre; // Muestra el nombre del usuario
    } else {
        // Si no hay usuario logueado, redirigir a la página de login
        window.location.replace('/src/features/auth/LoginPage.html');
    }

    // Función para cerrar sesión
    const logout = () => {
        localStorage.removeItem('currentUser'); // Limpia la información del usuario
        // Redirige a la página de login, reemplazando la entrada en el historial
        window.location.replace('/src/features/auth/LoginPage.html'); 
    };

    // Asignar evento de click a los botones de cerrar sesión
    if (logoutButton) {
        logoutButton.addEventListener('click', (event) => {
            event.preventDefault(); // Evita que el enlace recargue la página
            logout();
        });
    }

    if (sidebarLogoutButton) {
        sidebarLogoutButton.addEventListener('click', (event) => {
            event.preventDefault();
            logout();
        });
    }

    // Lógica para el toggle del sidebar (opcional, para dispositivos pequeños)
    // const menuToggle = document.getElementById('menu-toggle');
    // const wrapper = document.getElementById('wrapper');

    // if (menuToggle && wrapper) {
    //     menuToggle.addEventListener('click', () => {
    //         wrapper.classList.toggle('toggled');
    //     });
    // }
});


// frontend/src/admin/admin.js

// Este listener asegura que el DOM esté completamente cargado antes de ejecutar el script.
document.addEventListener('DOMContentLoaded', () => {
    // Asegúrate de que layout.js se carga antes de admin.js en el HTML
    // y que la función loadAdminLayout está disponible globalmente (window.loadAdminLayout)
    if (window.loadAdminLayout) {
        window.loadAdminLayout();
    } else {
        console.error("loadAdminLayout function not found. Ensure layout.js is loaded correctly and executed before admin.js.");
    }

    // Llama a la función para cargar los datos de compras al iniciar el dashboard
    fetchPurchaseData();

    // Puedes añadir más lógica específica del dashboard aquí.
    // Por ejemplo, para otros gráficos, eventos de botones, etc.
});
