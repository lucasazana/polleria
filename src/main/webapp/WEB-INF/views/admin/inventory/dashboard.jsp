<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Dashboard Inventario</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <style>body{padding:1.5rem;background:#fafafa;color:#374151}</style>
</head>
<body>
<div class="max-w-6xl mx-auto bg-white rounded shadow p-6">
    <!-- cabecera -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-800">Dashboard de Inventario</h1>
        <div>
            <span class="text-sm text-gray-600 mr-4">Usuario: <strong>${sessionScope.username}</strong></span>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-gray-600 hover:text-gray-800 hover:underline">← Volver al panel</a>
        </div>
    </div>

    <!-- estadisticas generales -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-gray-50 border border-gray-200 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium">Total Productos</p>
                    <p class="text-2xl font-bold text-gray-800">${totalProducts}</p>
                </div>
                <div class="bg-gray-100 p-3 rounded-full border border-gray-200">
                    <i data-lucide="package" class="h-6 w-6 text-gray-600"></i>
                </div>
            </div>
        </div>

        <div class="bg-gray-50 border border-gray-200 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium">Stock Bajo</p>
                    <p class="text-2xl font-bold text-gray-800">${lowStockProducts}</p>
                </div>
                <div class="bg-gray-100 p-3 rounded-full border border-gray-200">
                    <i data-lucide="alert-triangle" class="h-6 w-6 text-gray-600"></i>
                </div>
            </div>
        </div>

        <div class="bg-gray-50 border border-gray-200 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium">Valor Total</p>
                    <p class="text-xl font-bold text-gray-800">S/. <fmt:formatNumber value="${totalValue}" pattern="#,##0.00"/></p>
                </div>
                <div class="bg-gray-100 p-3 rounded-full border border-gray-200">
                    <i data-lucide="dollar-sign" class="h-6 w-6 text-gray-600"></i>
                </div>
            </div>
        </div>

        <div class="bg-gray-50 border border-gray-200 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-gray-600 text-sm font-medium">Por Vencer</p>
                    <p class="text-2xl font-bold text-gray-800">${expiringProducts}</p>
                </div>
                <div class="bg-gray-100 p-3 rounded-full border border-gray-200">
                    <i data-lucide="calendar-x" class="h-6 w-6 text-gray-600"></i>
                </div>
            </div>
        </div>
    </div>

    <!-- acciones rapidas -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
           class="block p-6 bg-white border-2 border-gray-200 rounded-lg hover:border-gray-300 hover:bg-gray-50 transition-colors">
            <div class="text-center">
                <div class="mb-3">
                    <i data-lucide="package" class="h-12 w-12 text-gray-600 mx-auto"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Ver Productos</h3>
                <p class="text-gray-600 text-sm mt-2">Gestionar inventario de productos</p>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/admin/inventario/productos/crear" 
           class="block p-6 bg-white border-2 border-gray-200 rounded-lg hover:border-gray-300 hover:bg-gray-50 transition-colors">
            <div class="text-center">
                <div class="mb-3">
                    <i data-lucide="plus-circle" class="h-12 w-12 text-gray-600 mx-auto"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Nuevo Producto</h3>
                <p class="text-gray-600 text-sm mt-2">Agregar producto al inventario</p>
            </div>
        </a>

        <a href="${pageContext.request.contextPath}/admin/inventario/alertas" 
           class="block p-6 bg-white border-2 border-gray-200 rounded-lg hover:border-gray-300 hover:bg-gray-50 transition-colors">
            <div class="text-center">
                <div class="mb-3">
                    <i data-lucide="bell" class="h-12 w-12 text-gray-600 mx-auto"></i>
                </div>
                <h3 class="text-lg font-semibold text-gray-800">Alertas</h3>
                <p class="text-gray-600 text-sm mt-2">Stock bajo y vencimientos</p>
            </div>
        </a>
    </div>
</div>

<!-- Inicializar Lucide Icons -->
<script>
    lucide.createIcons();
</script>
</body>
</html>