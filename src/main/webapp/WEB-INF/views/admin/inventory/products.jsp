<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Productos - Inventario</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <style>body{padding:1.5rem;background:#fafafa;color:#374151}</style>
</head>
<body>
<div class="max-w-7xl mx-auto bg-white rounded shadow p-6">
    <!-- cabecera con titulo y acciones -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Productos del Inventario</h1>
        <div>
            <a href="${pageContext.request.contextPath}/admin/inventario/productos/crear" 
               class="bg-gray-800 text-white px-4 py-2 rounded hover:bg-gray-700 flex items-center gap-2 border border-gray-800">
                <i data-lucide="plus" class="h-4 w-4"></i>
                Nuevo Producto
            </a>
            <a href="${pageContext.request.contextPath}/admin/inventario" 
               class="text-sm text-gray-600 ml-4">← Volver</a>
        </div>
    </div>

    <!-- mensajes flash -->
    <c:if test="${not empty success}">
        <div class="mb-4 p-3 bg-green-100 text-green-700 rounded">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">${error}</div>
    </c:if>

    <!-- filtros y busqueda -->
    <div class="bg-gray-50 p-4 rounded-lg mb-6">
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <!-- filtro por categoria -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Filtrar por categoría:</label>
                <form method="get" action="${pageContext.request.contextPath}/admin/inventario/productos">
                    <select name="categoryId" onchange="this.form.submit()" 
                            class="w-full p-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500">
                        <option value="">Todas las categorías</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category.id}" 
                                    <c:if test="${category.id == selectedCategoryId}">selected</c:if>>
                                ${category.nombre}
                            </option>
                        </c:forEach>
                    </select>
                </form>
            </div>

            <!-- busqueda por nombre -->
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Buscar producto:</label>
                <form method="get" action="${pageContext.request.contextPath}/admin/inventario/productos" class="flex">
                    <input type="text" name="search" value="${searchTerm}" 
                           placeholder="Nombre del producto..."
                           class="flex-1 p-2 border border-gray-300 rounded-l focus:ring-2 focus:ring-blue-500">
                    <button type="submit" class="bg-gray-800 text-white px-4 py-2 rounded-r hover:bg-gray-700 border border-gray-800">
                        <i data-lucide="search" class="h-4 w-4"></i>
                    </button>
                </form>
            </div>

            <!-- boton limpiar filtros -->
            <div class="flex items-end">
                <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
                   class="bg-gray-400 text-white px-4 py-2 rounded hover:bg-gray-500 flex items-center gap-2 border border-gray-400">
                    <i data-lucide="x" class="h-4 w-4"></i>
                    Limpiar filtros
                </a>
            </div>
        </div>
    </div>

    <!-- tabla de productos -->
    <div class="overflow-x-auto">
        <c:choose>
            <c:when test="${empty products}">
                <div class="text-center py-8">
                    <div class="text-6xl mb-4">📦</div>
                    <p class="text-gray-500 text-lg">No hay productos registrados</p>
                    <a href="${pageContext.request.contextPath}/admin/inventario/productos/crear" 
                       class="inline-block mt-4 bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600">
                        Crear primer producto
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <table class="w-full border-collapse border border-gray-300">
                    <!-- cabecera de tabla -->
                    <thead>
                        <tr class="bg-gray-100">
                            <th class="border border-gray-300 px-4 py-2 text-left">Producto</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Categoría</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Stock</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Precio</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Valor Total</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Estado</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Acciones</th>
                        </tr>
                    </thead>
                    <!-- contenido de tabla -->
                    <tbody>
                        <c:forEach var="product" items="${products}">
                            <tr class="hover:bg-gray-50">
                                <!-- nombre y detalles -->
                                <td class="border border-gray-300 px-4 py-2">
                                    <div>
                                        <div class="font-medium">${product.nombre}</div>
                                        <div class="text-sm text-gray-500">
                                            ${product.unidadMedida}
                                            <c:if test="${not empty product.proveedor}">
                                                | ${product.proveedor}
                                            </c:if>
                                        </div>
                                    </div>
                                </td>
                                
                                <!-- categoria -->
                                <td class="border border-gray-300 px-4 py-2">
                                    <span class="bg-gray-100 text-gray-700 px-2 py-1 rounded text-xs border border-gray-200">
                                        ${product.category.nombre}
                                    </span>
                                </td>
                                
                                <!-- stock con alerta -->
                                <td class="border border-gray-300 px-4 py-2 text-center">
                                    <div class="flex flex-col items-center">
                                        <span class="font-medium ${product.lowStock ? 'text-red-600' : 'text-green-600'}">
                                            <fmt:formatNumber value="${product.stockActual}" pattern="#,##0.##"/>
                                        </span>
                                        <span class="text-xs text-gray-500">
                                            Min: <fmt:formatNumber value="${product.stockMinimo}" pattern="#,##0.##"/>
                                        </span>
                                        <c:if test="${product.lowStock}">
                                            <span class="text-xs text-red-600 font-medium">¡STOCK BAJO!</span>
                                        </c:if>
                                    </div>
                                </td>
                                
                                <!-- precio unitario -->
                                <td class="border border-gray-300 px-4 py-2 text-center">
                                    <span class="font-medium">S/. <fmt:formatNumber value="${product.precioUnitario}" pattern="#,##0.00"/></span>
                                </td>
                                
                                <!-- valor total -->
                                <td class="border border-gray-300 px-4 py-2 text-center">
                                    <span class="font-medium text-green-600">
                                        S/. <fmt:formatNumber value="${product.valorTotal}" pattern="#,##0.00"/>
                                    </span>
                                </td>
                                
                                <!-- estado -->
                                <td class="border border-gray-300 px-4 py-2 text-center">
                                    <span class="px-2 py-1 rounded text-xs ${product.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}">
                                        ${product.active ? 'Activo' : 'Inactivo'}
                                    </span>
                                </td>
                                
                                <!-- acciones -->
                                <td class="border border-gray-300 px-4 py-2 text-center">
                                    <div class="flex flex-col space-y-1">
                                        <!-- editar producto -->
                                        <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/editar" 
                                           class="text-gray-600 text-xs hover:text-gray-800 hover:underline flex items-center justify-center gap-1">
                                            <i data-lucide="edit" class="h-3 w-3"></i> Editar
                                        </a>
                                        
                                        <!-- actualizar stock -->
                                        <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/stock" 
                                           class="text-gray-600 text-xs hover:text-gray-800 hover:underline flex items-center justify-center gap-1">
                                            <i data-lucide="package-plus" class="h-3 w-3"></i> Stock
                                        </a>
                                        
                                        <!-- desactivar -->
                                        <c:if test="${product.active}">
                                            <form method="post" action="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/desactivar" 
                                                  style="display:inline" onsubmit="return confirm('¿Desactivar este producto?');">
                                                <button type="submit" class="text-gray-500 text-xs hover:text-gray-700 hover:underline flex items-center justify-center gap-1">
                                                    <i data-lucide="x-circle" class="h-3 w-3"></i> Desactivar
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- resumen de la tabla -->
    <c:if test="${not empty products}">
        <div class="mt-4 text-sm text-gray-600">
            Total de productos mostrados: <strong>${products.size()}</strong>
        </div>
    </c:if>
</div>

<!-- Inicializar Lucide Icons -->
<script>
    lucide.createIcons();
</script>
</body>
</html>