document.addEventListener("DOMContentLoaded", () => {
    cargarEmpleados();

    document.getElementById("formAgregarEmpleado").addEventListener("submit", async function(event) {
        event.preventDefault();
    
        const empleado = {
            dni: document.getElementById("dni").value,
            nombre: document.getElementById("nombre").value,
            apellido: document.getElementById("apellido").value,
            contacto: document.getElementById("contacto").value,
            idRol: parseInt(document.getElementById("idRol").value), // Se obtiene el valor del select
            password: document.getElementById("password").value
        };
    
        try {
            const response = await fetch("http://localhost:8080/api/usuarios/crear", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(empleado)
            });
    
            if (!response.ok) throw new Error("Error al crear usuario");
    
            alert("Empleado agregado exitosamente");
            document.getElementById("formAgregarEmpleado").reset();
            cargarEmpleados(); // Recargar la lista de empleados
        } catch (error) {
            console.error("Error al agregar empleado:", error);
        }
    });
});

async function cargarEmpleados() {
    try {
        const response = await fetch("http://localhost:8080/api/usuarios/all");
        
        if (!response.ok) throw new Error("Error al obtener usuarios");

        const usuarios = await response.json();
        
        const tablaEmpleados = document.getElementById("tablaEmpleados");
        tablaEmpleados.innerHTML = ""; // Limpiar la tabla antes de agregar los datos

        usuarios.forEach(usuario => {
            const fila = document.createElement("tr");

            fila.innerHTML = `
                <td>${usuario.idUsuario}</td>
                <td>${usuario.nombre} ${usuario.apellido}</td>
                <td>${usuario.contacto}</td>
                <td>${usuario.idRol === 1 ? "Administrador" : "Empleado"}</td>
                <td>
                    <button class="btn btn-danger btn-sm" onclick="eliminarEmpleado(${usuario.idUsuario})">Eliminar</button>
                </td>
            `;

            tablaEmpleados.appendChild(fila);
        });

    } catch (error) {
        console.error("Error al cargar empleados:", error);
    }
}

// Asegurar que se ejecuta al cargar la página
document.addEventListener("DOMContentLoaded", () => {
    console.log("Cargando empleados...");
    cargarEmpleados();
});


async function crearEmpleado() {
    const dni = document.getElementById("dni").value;
    const nombre = document.getElementById("nombre").value;
    const apellido = document.getElementById("apellido").value;
    const contacto = document.getElementById("contacto").value;
    const idRol = document.getElementById("idRol").value;

    const nuevoEmpleado = {
        dni,
        nombre,
        apellido,
        contacto,
        idRol: parseInt(idRol, 10)
    };

    try {
        const response = await fetch("http://localhost:8080/api/usuarios/crear", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(nuevoEmpleado)
        });

        if (response.ok) {
            alert("Empleado agregado exitosamente");
            cargarEmpleados();
            document.getElementById("formAgregarEmpleado").reset();
            bootstrap.Modal.getInstance(document.getElementById("modalAgregarEmpleado")).hide();
        } else {
            alert("Error al agregar empleado");
        }
    } catch (error) {
        console.error("Error en la petición:", error);
    }
}

async function eliminarEmpleado(id) {
    if (!confirm("¿Seguro que deseas eliminar este empleado?")) return;

    try {
        const response = await fetch(`http://localhost:8080/api/usuarios/eliminar/${id}`, { method: "DELETE" });
        if (response.ok) {
            alert("Empleado eliminado");
            cargarEmpleados();
        } else {
            alert("Error al eliminar empleado");
        }
    } catch (error) {
        console.error("Error al eliminar empleado:", error);
    }
}

