// frontend/src/javascript/layout.js

/**
 * Genera el HTML del sidebar.
 * @returns {string} HTML del sidebar.
 */
function generateSidebarHTML() {
    return `
        <div class="bg-light border-end" id="sidebar-wrapper">
            <div class="sidebar-heading text-center py-4 primary-text fs-4 fw-bold text-uppercase border-bottom">
                <i class="bi bi-tools me-2"></i>Herramientas
            </div>
            <div class="list-group list-group-flush my-3">
                <a href="/public/index.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-page="dashboard">
                    <i class="bi bi-house-door-fill me-2"></i>Home
                </a>
                <a href="/src/admin/vender.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-page="vender">
                    <i class="bi bi-box-seam me-2"></i>Vender
                </a>
                <a href="/src/admin/productos.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-page="productos">
                    <i class="bi bi-box-seam me-2"></i>Productos
                </a>
                <a href="/src/admin/dashboard.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-page="admin-dashboard">
                    <i class="bi bi-speedometer2 me-2"></i>Dashboard
                </a>
                <a href="/src/admin/proveedores.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold">
                    <i class="bi bi-truck me-2"></i>Proveedores
                </a>
                <a href="/src/admin/ventas.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold">
                    <i class="bi bi-cash-coin me-2"></i>Ventas
                </a>
                <a href="/src/admin/compra.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold">
                    <i class="bi bi-cart-plus me-2"></i>Compras
                </a>
                <a href="/src/admin/usuarios.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold">
                    <i class="bi bi-people me-2"></i>Usuarios
                </a>
                <a href="/src/admin/clientes.html" class="list-group-item list-group-item-action bg-transparent second-text fw-bold">
                    <i class="bi bi-person-bounding-box me-2"></i>Clientes
                </a>
                <a href="#" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-bs-toggle="modal" data-bs-target="#modalReportes">
                    <i class="bi bi-graph-up me-2"></i>Reportes
                </a>
                <a href="#" class="list-group-item list-group-item-action bg-transparent second-text fw-bold" data-bs-toggle="modal" data-bs-target="#modalConfiguracion">
                    <i class="bi bi-sliders me-2"></i>Configuración
                </a>
            </div>
        </div>
    `;
}

/**
 * Genera el HTML del navbar.
 * @returns {string} HTML del navbar.
 */
function generateNavbarHTML() {
    return `
        <nav class="navbar navbar-expand-lg navbar-light bg-light py-4 px-4 border-bottom">
            <div class="d-flex align-items-center">
                <i class="fas fa-align-left primary-text fs-4 me-3" id="menu-toggle"></i>
                <h2 class="fs-2 m-0" id="navbar-title">Dashboard</h2>
            </div>

            <button class="navbar-toggler" type="button" data-bs-toggle="collapse"
                data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>

            <div class="collapse navbar-collapse" id="navbarSupportedContent">
                <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle second-text fw-bold" href="#" id="navbarDropdown"
                            role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="bi bi-person-circle me-2"></i>Usuario Ejemplo
                        </a>
                        <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
                            <li><a class="dropdown-item" href="#">Perfil</a></li>
                            <li><a class="dropdown-item" href="#">Configuración</a></li>
                            <li><a class="dropdown-item" href="#">Cerrar Sesión</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
    `;
}

/**
 * Inyecta el layout (sidebar y navbar) en el DOM y ejecuta un callback.
 * @param {string} pageTitle El título a mostrar en el navbar.
 * @param {string} pageContentHTML El HTML del contenido específico de la página.
 * @param {function} [callback] Una función para ejecutar después de que el contenido se haya inyectado.
 */
function injectLayout(pageTitle = "Dashboard", pageContentHTML = "", callback = null) {
    // Crear el contenedor principal del layout
    const wrapperDiv = document.createElement('div');
    wrapperDiv.className = 'd-flex';
    wrapperDiv.id = 'wrapper';

    // Añadir el sidebar al wrapper
    wrapperDiv.innerHTML = generateSidebarHTML();

    // Crear el contenedor para el contenido de la página y el navbar
    const pageContentWrapperDiv = document.createElement('div');
    pageContentWrapperDiv.id = 'page-content-wrapper';

    // Añadir el navbar al page-content-wrapper
    pageContentWrapperDiv.innerHTML = generateNavbarHTML();

    // Obtener la referencia al div del navbar y añadir el título
    const navbarTitleElement = pageContentWrapperDiv.querySelector('#navbar-title');
    if (navbarTitleElement) {
        navbarTitleElement.textContent = pageTitle;
    }

    // Crear un div para el contenido específico de la página
    const pageSpecificContentDiv = document.createElement('div');
    pageSpecificContentDiv.className = 'container-fluid px-4'; // Añadir clases de Bootstrap para padding
    // Set the innerHTML of pageSpecificContentDiv to the provided pageContentHTML
    pageSpecificContentDiv.innerHTML = pageContentHTML;

    // Finalmente, añadir el contenido específico de la página DENTRO de page-content-wrapper, después del navbar
    pageContentWrapperDiv.appendChild(pageSpecificContentDiv);

    // Limpiar el body actual antes de insertar el nuevo contenido
    document.body.innerHTML = '';
    document.body.appendChild(wrapperDiv); // Añadir el wrapper al body
    wrapperDiv.appendChild(pageContentWrapperDiv); // Insertar el pageContentWrapperDiv en el wrapper

    // Lógica para el toggle del sidebar (anteriormente en script.js)
    const el = document.getElementById('wrapper');
    const toggleButton = document.getElementById('menu-toggle');

    if (toggleButton && el) {
        toggleButton.onclick = function () {
            el.classList.toggle('toggled');
        };
    }

    // Activar el enlace del sidebar correspondiente a la página actual
    const currentPath = window.location.pathname;
    const sidebarLinks = document.querySelectorAll('#sidebar-wrapper .list-group-item-action');
    sidebarLinks.forEach(link => {
        // Normalize paths for comparison (remove leading/trailing slashes if needed)
        const linkHref = link.getAttribute('href').replace(/\/$/, '');
        const currentPathClean = currentPath.replace(/\/$/, '');

        if (linkHref === currentPathClean) {
            link.classList.add('active'); // Clase 'active' para Bootstrap
        }
    });

    // Execute the callback after all content is injected
    if (typeof callback === 'function') {
        callback();
    }
}