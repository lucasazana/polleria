<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Alertas de Inventario - Pollería</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <style>body{padding:1.5rem;background:#fafafa;color:#374151}</style>
</head>
<body>
<div class="max-w-7xl mx-auto">
    <!-- Cabecera -->
    <div class="flex justify-between items-center mb-8">
        <div>
            <h1 class="text-3xl font-bold text-gray-800 flex items-center gap-2">
                <i data-lucide="alert-triangle" class="h-8 w-8 text-yellow-500"></i>
                Alertas de Inventario
            </h1>
            <p class="text-gray-600 mt-2">Monitoreo de productos con stock bajo y próximos a vencer</p>
        </div>
        <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-600">Usuario: <strong>${sessionScope.username}</strong></span>
            <a href="${pageContext.request.contextPath}/admin/inventario/dashboard" 
               class="text-gray-600 hover:text-gray-800 flex items-center gap-1">
                <i data-lucide="arrow-left" class="h-4 w-4"></i>
                Dashboard
            </a>
        </div>
    </div>

    <!-- Resumen de alertas -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm font-medium text-gray-600">Productos con Stock Bajo</p>
                    <p class="text-2xl font-bold text-red-600">
                        <c:choose>
                            <c:when test="${not empty lowStockProducts}">
                                ${lowStockProducts.size()}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="p-3 bg-red-50 rounded-full">
                    <i data-lucide="trending-down" class="h-6 w-6 text-red-500"></i>
                </div>
            </div>
            <p class="text-xs text-gray-500 mt-2">Productos por debajo del stock mínimo</p>
        </div>

        <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-sm font-medium text-gray-600">Próximos a Vencer</p>
                    <p class="text-2xl font-bold text-yellow-600">
                        <c:choose>
                            <c:when test="${not empty expiringProducts}">
                                ${expiringProducts.size()}
                            </c:when>
                            <c:otherwise>0</c:otherwise>
                        </c:choose>
                    </p>
                </div>
                <div class="p-3 bg-yellow-50 rounded-full">
                    <i data-lucide="calendar-x" class="h-6 w-6 text-yellow-500"></i>
                </div>
            </div>
            <p class="text-xs text-gray-500 mt-2">Productos que vencen en los próximos 30 días</p>
        </div>
    </div>

    <!-- Productos con Stock Bajo -->
    <div class="mb-8">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                    <i data-lucide="trending-down" class="h-5 w-5 text-red-500"></i>
                    Productos con Stock Bajo
                </h2>
                <p class="text-sm text-gray-600 mt-1">Productos que están por debajo de su stock mínimo</p>
            </div>
            
            <div class="p-6">
                <c:choose>
                    <c:when test="${not empty lowStockProducts}">
                        <div class="overflow-x-auto">
                            <table class="min-w-full divide-y divide-gray-200">
                                <thead class="bg-gray-50">
                                    <tr>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Producto</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoría</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Actual</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock Mínimo</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Estado</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody class="bg-white divide-y divide-gray-200">
                                    <c:forEach items="${lowStockProducts}" var="product">
                                        <tr class="hover:bg-gray-50">
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="flex items-center">
                                                    <i data-lucide="package" class="h-4 w-4 text-gray-400 mr-2"></i>
                                                    <div>
                                                        <div class="text-sm font-medium text-gray-900">${product.nombre}</div>
                                                        <div class="text-sm text-gray-500">${product.proveedor}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                                    ${product.category.nombre}
                                                </span>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900 font-medium">
                                                    <fmt:formatNumber value="${product.stockActual}" pattern="#,##0.##"/> ${product.unidadMedida}
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900">
                                                    <fmt:formatNumber value="${product.stockMinimo}" pattern="#,##0.##"/> ${product.unidadMedida}
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <c:choose>
                                                    <c:when test="${product.stockActual <= 0}">
                                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                                            <i data-lucide="x-circle" class="h-3 w-3 mr-1"></i>
                                                            Sin Stock
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                            <i data-lucide="alert-triangle" class="h-3 w-3 mr-1"></i>
                                                            Stock Bajo
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                <div class="flex items-center space-x-2">
                                                    <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/stock" 
                                                       class="inline-flex items-center px-3 py-1 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                                                        <i data-lucide="plus" class="h-3 w-3 mr-1"></i>
                                                        Agregar Stock
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/editar" 
                                                       class="text-gray-600 hover:text-gray-900">
                                                        <i data-lucide="edit" class="h-4 w-4"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-8">
                            <i data-lucide="check-circle" class="h-12 w-12 text-green-400 mx-auto mb-4"></i>
                            <h3 class="text-lg font-medium text-gray-900 mb-2">¡Excelente!</h3>
                            <p class="text-gray-600">Todos los productos tienen stock suficiente</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Productos Próximos a Vencer -->
    <div class="mb-8">
        <div class="bg-white rounded-lg shadow-sm border border-gray-200">
            <div class="px-6 py-4 border-b border-gray-200">
                <h2 class="text-xl font-semibold text-gray-800 flex items-center gap-2">
                    <i data-lucide="calendar-x" class="h-5 w-5 text-yellow-500"></i>
                    Productos Próximos a Vencer
                </h2>
                <p class="text-sm text-gray-600 mt-1">Productos que vencen en los próximos 30 días</p>
            </div>
            
            <div class="p-6">
                <c:choose>
                    <c:when test="${not empty expiringProducts}">
                        <div class="overflow-x-auto">
                            <table class="min-w-full divide-y divide-gray-200">
                                <thead class="bg-gray-50">
                                    <tr>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Producto</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Categoría</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Stock</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Fecha Vencimiento</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Días Restantes</th>
                                        <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Acciones</th>
                                    </tr>
                                </thead>
                                <tbody class="bg-white divide-y divide-gray-200">
                                    <c:forEach items="${expiringProducts}" var="product">
                                        <tr class="hover:bg-gray-50">
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="flex items-center">
                                                    <i data-lucide="package" class="h-4 w-4 text-gray-400 mr-2"></i>
                                                    <div>
                                                        <div class="text-sm font-medium text-gray-900">${product.nombre}</div>
                                                        <div class="text-sm text-gray-500">${product.proveedor}</div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                                    ${product.category.nombre}
                                                </span>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900 font-medium">
                                                    <fmt:formatNumber value="${product.stockActual}" pattern="#,##0.##"/> ${product.unidadMedida}
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <div class="text-sm text-gray-900">
                                                    <c:if test="${not empty product.fechaVencimiento}">
                                                        ${product.fechaVencimiento}
                                                    </c:if>
                                                </div>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap">
                                                <c:if test="${not empty product.fechaVencimiento}">
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
                                                        Próximo a vencer
                                                    </span>
                                                </c:if>
                                                <c:if test="${empty product.fechaVencimiento}">
                                                    <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-gray-100 text-gray-800">
                                                        Sin fecha
                                                    </span>
                                                </c:if>
                                            </td>
                                            <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                                                <div class="flex items-center space-x-2">
                                                    <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/stock" 
                                                       class="inline-flex items-center px-3 py-1 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50">
                                                        <i data-lucide="minus" class="h-3 w-3 mr-1"></i>
                                                        Reducir Stock
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/editar" 
                                                       class="text-gray-600 hover:text-gray-900">
                                                        <i data-lucide="edit" class="h-4 w-4"></i>
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="text-center py-8">
                            <i data-lucide="check-circle" class="h-12 w-12 text-green-400 mx-auto mb-4"></i>
                            <h3 class="text-lg font-medium text-gray-900 mb-2">¡Todo en orden!</h3>
                            <p class="text-gray-600">No hay productos próximos a vencer en los próximos 30 días</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Enlaces rápidos -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i data-lucide="zap" class="h-5 w-5 text-blue-500"></i>
            Acciones Rápidas
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
               class="flex items-center p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                <i data-lucide="package" class="h-6 w-6 text-gray-400 mr-3"></i>
                <div>
                    <div class="font-medium text-gray-900">Gestionar Productos</div>
                    <div class="text-sm text-gray-500">Ver todos los productos</div>
                </div>
            </a>
            <a href="${pageContext.request.contextPath}/admin/inventario/productos/crear" 
               class="flex items-center p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                <i data-lucide="plus" class="h-6 w-6 text-gray-400 mr-3"></i>
                <div>
                    <div class="font-medium text-gray-900">Agregar Producto</div>
                    <div class="text-sm text-gray-500">Crear nuevo producto</div>
                </div>
            </a>
            <a href="${pageContext.request.contextPath}/admin/inventario/dashboard" 
               class="flex items-center p-4 border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
                <i data-lucide="bar-chart-3" class="h-6 w-6 text-gray-400 mr-3"></i>
                <div>
                    <div class="font-medium text-gray-900">Dashboard</div>
                    <div class="text-sm text-gray-500">Ver estadísticas</div>
                </div>
            </a>
        </div>
    </div>
</div>

<script>
    // Inicializar Lucide Icons
    lucide.createIcons();
</script>
</body>
</html>