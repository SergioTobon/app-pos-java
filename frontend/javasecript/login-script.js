document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault();

        const dni = document.getElementById("dni").value;
        const password = document.getElementById("password").value;

        try {
            const response = await fetch("http://localhost:8080/api/usuarios/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ dni, password })
            });

            if (!response.ok) throw new Error("Usuario o contraseña incorrectos");

            const data = await response.json();

            if (!data.id_usuarios) {
                throw new Error("El servidor no devolvió el ID del usuario.");
            }

            // Guardar usuario en localStorage
            localStorage.setItem("usuario", JSON.stringify(data));

            alert(`✅ Bienvenido, ${data.nombre}`);

            // Redirigir según id_rol
            if (data.id_rol === 1) {
                window.location.href = "/frontend/html/home-admin.html";
            } else if (data.id_rol === 2) {
                window.location.href = "/frontend/html/home-empleado.html";
            } else {
                alert("⚠️ Rol no reconocido");
            }

        } catch (error) {
            alert("❌ Error: " + error.message);
        }
    });
});
