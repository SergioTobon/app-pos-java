document.addEventListener("DOMContentLoaded", () => {
    const usuario = localStorage.getItem("usuario");

    if (!usuario) {
        window.location.href = "/frontend/html/login.html"; // Si no hay usuario, volver al login
    }

    // Cerrar sesión al hacer clic en el botón
    const logoutBtn = document.getElementById("logoutBtn");
    if (logoutBtn) {
        logoutBtn.addEventListener("click", () => {
            localStorage.removeItem("usuario"); // Eliminar la sesión
            window.location.href = "/frontend/index.html"; // Redirigir al login
        });
    }

    // Evitar que el usuario vuelva al login con el botón atrás
    window.addEventListener("popstate", function () {
        window.history.pushState(null, null, window.location.href);
    });
});
