// frontend/src/admin/adminProductos.js

const baseUrlProductos = 'http://localhost:8080/api/productos';
let currentProductId = null; // Para saber si estamos editando o añadiendo

// Función para cargar los datos de productos
// DEBE ser una función global (adjunta a window) para que layout.js la pueda llamar
window.fetchProductosData = async function() {
    const productsTableBody = document.getElementById('productsTableBody');
    const totalProductsCountElement = document.getElementById('totalProductsCount');
    const lowStockCountElement = document.getElementById('lowStockCount');

    if (!productsTableBody || !totalProductsCountElement || !lowStockCountElement) {
        console.warn("Elementos de productos no encontrados en el DOM. Puede que el HTML no se haya cargado o los IDs estén incorrectos.");
        return;
    }

    try {
        const response = await fetch(baseUrlProductos);
        if (!response.ok) {
            if (response.status === 204) { // No Content
                console.warn('No hay productos para mostrar.');
                productsTableBody.innerHTML = '<tr><td colspan="8" class="text-center">No hay productos registrados.</td></tr>';
                totalProductsCountElement.textContent = '0';
                lowStockCountElement.textContent = '0';
                return;
            }
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const products = await response.json();
        displayProducts(products);
        updateProductCounts(products);
    } catch (error) {
        console.error('Error al obtener datos de productos:', error);
        productsTableBody.innerHTML = '<tr><td colspan="8" class="text-center text-danger">Error al cargar productos. Por favor, intente de nuevo.</td></tr>';
        totalProductsCountElement.textContent = 'N/A';
        lowStockCountElement.textContent = 'N/A';
    }
};

function displayProducts(products) {
    const productsTableBody = document.getElementById('productsTableBody');
    if (!productsTableBody) return; 

    productsTableBody.innerHTML = '';
    if (products.length === 0) {
        productsTableBody.innerHTML = '<tr><td colspan="8" class="text-center">No hay productos registrados.</td></tr>';
        return;
    }

    products.forEach(product => {
        const row = productsTableBody.insertRow();
        const formattedPrecioCompra = product.precioCompra != null ? `$${product.precioCompra.toFixed(2)}` : 'N/A';
        const formattedPrecioVenta = product.precioVenta != null ? `$${product.precioVenta.toFixed(2)}` : 'N/A';

        row.innerHTML = `
            <td>${product.id}</td>
            <td>${product.nombre}</td>
            <td>${product.stock}</td>
            <td>${product.descripcion}</td>
            <td>${formattedPrecioCompra}</td>
            <td>${formattedPrecioVenta}</td>
            <td>${product.nombreProveedor || product.idProveedor || 'N/A'}</td>
            <td>
                <button class="btn btn-sm btn-warning edit-btn" data-id="${product.id}">
                    <i class="bi bi-pencil-square"></i>
                </button>
                <button class="btn btn-sm btn-danger delete-btn" data-id="${product.id}">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
    });
}

function updateProductCounts(products) {
    const totalProductsCountElement = document.getElementById('totalProductsCount');
    const lowStockCountElement = document.getElementById('lowStockCount');

    if (totalProductsCountElement) {
        totalProductsCountElement.textContent = products.length;
    }

    if (lowStockCountElement) {
        const lowStockProducts = products.filter(p => p.stock !== null && p.stock < 10);
        lowStockCountElement.textContent = lowStockProducts.length;
    }
}

// Referencias a elementos del modal y formulario de productos
// Asegúrate de que productModalElement exista antes de crear la instancia de Modal
const productModalElement = document.getElementById('productModal');
const productModal = productModalElement ? new bootstrap.Modal(productModalElement) : null;
const productForm = document.getElementById('productForm');

// =======================================================
// !!! Manejadores de eventos usando delegación de eventos !!!
// Esto es crucial porque los elementos como botones son inyectados dinámicamente.
// =======================================================

// Delegación para el envío del formulario de producto
document.body.addEventListener('submit', async (event) => {
    if (event.target && event.target.id === 'productForm') {
        event.preventDefault();

        const productData = {
            nombre: document.getElementById('productName').value,
            stock: parseInt(document.getElementById('productStock').value),
            descripcion: document.getElementById('productDescription').value,
            precioCompra: parseFloat(document.getElementById('productPurchasePrice').value),
            precioVenta: parseFloat(document.getElementById('productSalePrice').value),
            idProveedor: parseInt(document.getElementById('productSupplierId').value)
        };

        try {
            let response;
            let method;
            let url;

            if (currentProductId) {
                method = 'PUT';
                url = `${baseUrlProductos}/${currentProductId}`;
            } else {
                method = 'POST';
                url = baseUrlProductos;
            }

            response = await fetch(url, {
                method: method,
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(productData)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Error al ${currentProductId ? 'actualizar' : 'guardar'} producto: ${response.status} - ${errorText}`);
            }

            if (productModal) productModal.hide();
            if (productForm) productForm.reset();
            currentProductId = null;
            document.getElementById('productModalLabel').textContent = 'Añadir Nuevo Producto';
            await window.fetchProductosData(); // Volver a cargar los datos
            alert(`Producto ${method === 'PUT' ? 'actualizado' : 'añadido'} con éxito.`);

        } catch (error) {
            console.error('Error en la operación de producto:', error);
            alert(`Error al ${currentProductId ? 'actualizar' : 'añadir'} producto: ${error.message}`);
        }
    }
});

// Delegación para clics en botones de editar/eliminar y añadir nuevo producto
document.body.addEventListener('click', async (event) => {
    // Lógica para el botón de EDITAR
    if (event.target.closest('.edit-btn')) {
        const productId = event.target.closest('.edit-btn').dataset.id;
        try {
            const response = await fetch(`${baseUrlProductos}/${productId}`);
            if (!response.ok) {
                throw new Error(`Error al obtener producto para edición: ${response.status}`);
            }
            const productToEdit = await response.json();

            // Llenar el formulario con los datos del producto
            document.getElementById('productId').value = productToEdit.id || '';
            document.getElementById('productName').value = productToEdit.nombre || '';
            document.getElementById('productStock').value = productToEdit.stock || 0;
            document.getElementById('productDescription').value = productToEdit.descripcion || '';
            document.getElementById('productPurchasePrice').value = productToEdit.precioCompra || 0.0;
            document.getElementById('productSalePrice').value = productToEdit.precioVenta || 0.0;
            document.getElementById('productSupplierId').value = productToEdit.idProveedor || '';

            currentProductId = productToEdit.id;
            document.getElementById('productModalLabel').textContent = 'Editar Producto';
            if (productModal) productModal.show();
        } catch (error) {
            console.error('Error al cargar datos para edición:', error);
            alert('No se pudo cargar la información del producto para edición.');
        }
    }

    // Lógica para el botón de ELIMINAR
    if (event.target.closest('.delete-btn')) {
        const productId = event.target.closest('.delete-btn').dataset.id;
        if (confirm(`¿Estás seguro de que deseas eliminar el producto con ID ${productId}?`)) {
            try {
                const response = await fetch(`${baseUrlProductos}/${productId}`, {
                    method: 'DELETE'
                });

                if (!response.ok) {
                    const errorText = await response.text();
                    throw new Error(`Error al eliminar producto: ${response.status} - ${errorText}`);
                }

                alert('Producto eliminado con éxito.');
                await window.fetchProductosData(); // Refrescar la tabla
            } catch (error) {
                console.error('Error al eliminar producto:', error);
                alert(`Error al eliminar producto: ${error.message}`);
            }
        }
    }

    // Lógica para el botón de "Añadir Nuevo Producto"
    // Usamos `closest` para asegurarnos de capturar el botón correcto, incluso si se hace clic en su icono
    if (event.target.closest('button.btn-primary.mb-3')) { 
        if (productForm) productForm.reset();
        currentProductId = null;
        if (document.getElementById('productModalLabel')) {
            document.getElementById('productModalLabel').textContent = 'Añadir Nuevo Producto';
        }
        if (productModal) productModal.show();
    }
});

// Delegación para el evento de cierre del modal de producto (para resetear el formulario)
document.body.addEventListener('hidden.bs.modal', (event) => {
    if (event.target && event.target.id === 'productModal') {
        if (productForm) productForm.reset();
        currentProductId = null;
        if (document.getElementById('productModalLabel')) {
            document.getElementById('productModalLabel').textContent = 'Añadir Nuevo Producto';
        }
    }
});

// No necesitas document.addEventListener('DOMContentLoaded') aquí.
// La función fetchProductosData será llamada por layout.js cuando el contenido de productos.html esté cargado.