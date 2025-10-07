<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>${empty product ? 'Crear' : 'Editar'} Producto</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <style>body{padding:1.5rem;background:#fafafa;color:#374151}</style>
</head>
<body>
<div class="max-w-4xl mx-auto bg-white rounded shadow p-6">
    <!-- cabecera -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">
            ${empty product ? 'Crear Nuevo Producto' : 'Editar Producto'}
        </h1>
        <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
           class="text-sm text-blue-600 hover:underline">← Volver a productos</a>
    </div>

    <!-- mensajes flash -->
    <c:if test="${not empty success}">
        <div class="mb-4 p-3 bg-green-100 text-green-700 rounded">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">${error}</div>
    </c:if>

    <!-- formulario de producto -->
    <form method="post" action="${empty product ? 
        pageContext.request.contextPath.concat('/admin/inventario/productos/crear') : 
        pageContext.request.contextPath.concat('/admin/inventario/productos/').concat(product.id).concat('/editar')}" 
        class="space-y-6">

        <!-- informacion basica -->
        <div class="bg-gray-50 p-4 rounded-lg">
            <h3 class="text-lg font-medium mb-4">Información Básica</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- nombre del producto -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                        Nombre del Producto <span class="text-red-500">*</span>
                    </label>
                    <input type="text" name="nombre" value="${product.nombre}" required
                           class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                           placeholder="Ej: Pollo entero">
                </div>

                <!-- categoria -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                        Categoría <span class="text-red-500">*</span>
                    </label>
                    <select name="categoryId" required
                            class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                        <option value="">Seleccionar categoría</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.id}" 
                                    <c:if test="${not empty product && product.category.id == category.id}">selected</c:if>>
                                ${category.nombre}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- unidad de medida -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                        Unidad de Medida <span class="text-red-500">*</span>
                    </label>
                    <select name="unidadMedida" required
                            class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                        <option value="">Seleccionar unidad</option>
                        <option value="kg" <c:if test="${product.unidadMedida == 'kg'}">selected</c:if>>Kilogramo (kg)</option>
                        <option value="g" <c:if test="${product.unidadMedida == 'g'}">selected</c:if>>Gramo (g)</option>
                        <option value="litro" <c:if test="${product.unidadMedida == 'litro'}">selected</c:if>>Litro</option>
                        <option value="ml" <c:if test="${product.unidadMedida == 'ml'}">selected</c:if>>Mililitro (ml)</option>
                        <option value="unidad" <c:if test="${product.unidadMedida == 'unidad'}">selected</c:if>>Unidad</option>
                        <option value="paquete" <c:if test="${product.unidadMedida == 'paquete'}">selected</c:if>>Paquete</option>
                        <option value="caja" <c:if test="${product.unidadMedida == 'caja'}">selected</c:if>>Caja</option>
                    </select>
                </div>

                <!-- proveedor -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Proveedor</label>
                    <input type="text" name="proveedor" value="${product.proveedor}"
                           class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                           placeholder="Ej: Distribuidora Norte">
                </div>
            </div>
        </div>

        <!-- informacion de stock y precio -->
        <div class="bg-gray-50 p-4 rounded-lg">
            <h3 class="text-lg font-medium mb-4">Stock y Precio</h3>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <!-- stock actual - solo para productos nuevos -->
                <c:if test="${empty product}">
                    <div>
                        <label class="block text-sm font-medium text-gray-700 mb-1">
                            Stock Inicial <span class="text-red-500">*</span>
                        </label>
                        <input type="number" name="stockActual" step="0.01" min="0" value="0" required
                               class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                               placeholder="0.00">
                    </div>
                </c:if>

                <!-- stock minimo -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                        Stock Mínimo <span class="text-red-500">*</span>
                    </label>
                    <input type="number" name="stockMinimo" step="0.01" min="0" 
                           value="${not empty product ? product.stockMinimo : '0'}" required
                           class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                           placeholder="0.00">
                </div>

                <!-- precio unitario -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">
                        Precio Unitario (S/.) <span class="text-red-500">*</span>
                    </label>
                    <input type="number" name="precioUnitario" step="0.01" min="0" 
                           value="${not empty product ? product.precioUnitario : '0'}" required
                           class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500"
                           placeholder="0.00">
                </div>
            </div>
        </div>

        <!-- informacion adicional -->
        <div class="bg-gray-50 p-4 rounded-lg">
            <h3 class="text-lg font-medium mb-4">Información Adicional</h3>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                <!-- ubicacion -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Ubicación</label>
                    <select name="ubicacion"
                            class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                        <option value="">Seleccionar ubicación</option>
                        <option value="Refrigeradora" <c:if test="${product.ubicacion == 'Refrigeradora'}">selected</c:if>>Refrigeradora</option>
                        <option value="Congelador" <c:if test="${product.ubicacion == 'Congelador'}">selected</c:if>>Congelador</option>
                        <option value="Depósito" <c:if test="${product.ubicacion == 'Depósito'}">selected</c:if>>Depósito</option>
                        <option value="Almacén seco" <c:if test="${product.ubicacion == 'Almacén seco'}">selected</c:if>>Almacén seco</option>
                        <option value="Vitrina" <c:if test="${product.ubicacion == 'Vitrina'}">selected</c:if>>Vitrina</option>
                    </select>
                </div>

                <!-- fecha de compra -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-1">Fecha de Compra</label>
                    <input type="date" name="fechaCompra" 
                           value="${not empty product.fechaCompra ? product.fechaCompra : ''}"
                           class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                </div>

                <!-- fecha de vencimiento -->
                <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 mb-1">Fecha de Vencimiento</label>
                    <input type="date" name="fechaVencimiento" 
                           value="${not empty product.fechaVencimiento ? product.fechaVencimiento : ''}"
                           class="w-full md:w-1/2 p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                    <p class="text-xs text-gray-500 mt-1">Opcional: Solo para productos perecederos</p>
                </div>
            </div>
        </div>

        <!-- botones de accion -->
        <div class="flex justify-end space-x-4 pt-6 border-t">
            <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
               class="px-4 py-2 border border-gray-300 rounded text-gray-700 hover:bg-gray-50 flex items-center gap-2">
                <i data-lucide="x" class="h-4 w-4"></i>
                Cancelar
            </a>
            <button type="submit" 
                    class="px-6 py-2 bg-gray-800 text-white rounded hover:bg-gray-700 focus:ring-2 focus:ring-gray-500 flex items-center gap-2 border border-gray-800">
                <i data-lucide="${empty product ? 'plus' : 'save'}" class="h-4 w-4"></i>
                ${empty product ? 'Crear Producto' : 'Actualizar Producto'}
            </button>
        </div>
    </form>

    <!-- informacion adicional para edicion -->
    <c:if test="${not empty product}">
        <div class="mt-6 p-4 bg-gray-50 rounded-lg border border-gray-200">
            <h4 class="font-medium text-gray-800 mb-2 flex items-center gap-2">
                <i data-lucide="info" class="h-4 w-4"></i>
                Información del Producto
            </h4>
            <div class="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-gray-700">
                <div>Stock actual: <strong><fmt:formatNumber value="${product.stockActual}" pattern="#,##0.##"/> ${product.unidadMedida}</strong></div>
                <div>Valor total: <strong>S/. <fmt:formatNumber value="${product.valorTotal}" pattern="#,##0.00"/></strong></div>
                <c:if test="${not empty product.createdAt}">
                    <div>Creado: <strong>${product.createdAt.toString().replace('T', ' ').substring(0, 16)}</strong></div>
                </c:if>
                <c:if test="${not empty product.updatedAt}">
                    <div>Actualizado: <strong>${product.updatedAt.toString().replace('T', ' ').substring(0, 16)}</strong></div>
                </c:if>
            </div>
            <p class="text-xs text-gray-600 mt-2 flex items-center gap-1">
                <i data-lucide="lightbulb" class="h-3 w-3"></i>
                Para cambiar el stock actual, use la opción "Actualizar Stock" desde la lista de productos.
            </p>
        </div>
    </c:if>
</div>

<!-- Inicializar Lucide Icons -->
<script>
    lucide.createIcons();
</script>
</body>
</html>