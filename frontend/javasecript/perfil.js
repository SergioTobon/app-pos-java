document.addEventListener("DOMContentLoaded", () => {
    const usuario = JSON.parse(localStorage.getItem("usuario"));

    if (!usuario || !usuario.id_usuarios) {
        alert("⚠️ Error: Sesión no iniciada.");
        window.location.href = "/frontend/html/login.html";
        return;
    }

    console.log("Usuario autenticado:", usuario); // Verificar datos en consola

    fetch(`http://localhost:8080/api/usuarios/perfil/${usuario.id_usuarios}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Error ${response.status}: No se pudo cargar el perfil`);
            }
            return response.json();
        })
        .then(userData => {
            console.log("Datos recibidos:", userData); // Verificar respuesta del backend
            document.getElementById("adminNombre").textContent = `${userData.nombre} ${userData.apellido}`;
            document.getElementById("adminContacto").textContent = userData.contacto;
        })
        .catch(error => {
            console.error("Error al cargar el perfil:", error);
            alert("❌ No se pudo cargar el perfil.");
        });
});
