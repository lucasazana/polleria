<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Inventario - Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-6xl mx-auto bg-white rounded shadow p-6">
    <!-- cabecera con titulo y navegacion -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-3xl font-bold text-gray-800">Gestión de Inventario</h1>
        <div>
            <span class="text-sm text-gray-600 mr-4">Usuario: <strong>${sessionScope.username}</strong></span>
            <a href="${pageContext.request.contextPath}/admin" class="text-sm text-blue-600 hover:underline">← Volver al dashboard</a>
        </div>
    </div>

    <!-- tarjetas de estadisticas -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <!-- total productos -->
        <div class="bg-blue-50 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-blue-600 text-sm font-medium">Total Productos</p>
                    <p class="text-2xl font-bold text-blue-800">${totalProducts}</p>
                </div>
                <div class="bg-blue-100 p-3 rounded-full">
                    📦
                </div>
            </div>
        </div>

        <!-- productos con stock bajo -->
        <div class="bg-red-50 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-red-600 text-sm font-medium">Stock Bajo</p>
                    <p class="text-2xl font-bold text-red-800">${lowStockProducts}</p>
                </div>
                <div class="bg-red-100 p-3 rounded-full">
                    ⚠️
                </div>
            </div>
        </div>

        <!-- valor total inventario -->
        <div class="bg-green-50 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-green-600 text-sm font-medium">Valor Total</p>
                    <p class="text-2xl font-bold text-green-800">S/. <fmt:formatNumber value="${totalValue}" pattern="#,##0.00"/></p>
                </div>
                <div class="bg-green-100 p-3 rounded-full">
                    💰
                </div>
            </div>
        </div>

        <!-- productos por vencer -->
        <div class="bg-yellow-50 p-6 rounded-lg">
            <div class="flex items-center justify-between">
                <div>
                    <p class="text-yellow-600 text-sm font-medium">Por Vencer (30d)</p>
                    <p class="text-2xl font-bold text-yellow-800">${expiringProducts}</p>
                </div>
                <div class="bg-yellow-100 p-3 rounded-full">
                    📅
                </div>
            </div>
        </div>
    </div>

    <!-- menu de navegacion -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
        <!-- gestion de productos -->
        <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
           class="block p-6 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="flex items-center mb-3">
                <div class="bg-blue-100 p-2 rounded-lg mr-3">📦</div>
                <h3 class="text-lg font-semibold">Productos</h3>
            </div>
            <p class="text-gray-600 text-sm">Gestionar productos, crear, editar y controlar stock</p>
        </a>

        <!-- alertas de inventario -->
        <a href="${pageContext.request.contextPath}/admin/inventario/alertas" 
           class="block p-6 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="flex items-center mb-3">
                <div class="bg-red-100 p-2 rounded-lg mr-3">⚠️</div>
                <h3 class="text-lg font-semibold">Alertas</h3>
            </div>
            <p class="text-gray-600 text-sm">Stock bajo y productos próximos a vencer</p>
        </a>

        <!-- movimientos de inventario -->
        <a href="${pageContext.request.contextPath}/admin/inventario/movimientos" 
           class="block p-6 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="flex items-center mb-3">
                <div class="bg-green-100 p-2 rounded-lg mr-3">📊</div>
                <h3 class="text-lg font-semibold">Movimientos</h3>
            </div>
            <p class="text-gray-600 text-sm">Historial de entradas, salidas y ajustes</p>
        </a>

        <!-- gestion de categorias -->
        <a href="${pageContext.request.contextPath}/admin/inventario/categorias" 
           class="block p-6 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors">
            <div class="flex items-center mb-3">
                <div class="bg-purple-100 p-2 rounded-lg mr-3">🏷️</div>
                <h3 class="text-lg font-semibold">Categorías</h3>
            </div>
            <p class="text-gray-600 text-sm">Administrar categorías de productos</p>
        </a>

        <!-- reportes futuros -->
        <div class="p-6 bg-gray-100 border border-gray-200 rounded-lg opacity-60">
            <div class="flex items-center mb-3">
                <div class="bg-gray-200 p-2 rounded-lg mr-3">📄</div>
                <h3 class="text-lg font-semibold text-gray-500">Reportes PDF</h3>
            </div>
            <p class="text-gray-500 text-sm">Próximamente: reportes en PDF</p>
        </div>

        <!-- configuracion futura -->
        <div class="p-6 bg-gray-100 border border-gray-200 rounded-lg opacity-60">
            <div class="flex items-center mb-3">
                <div class="bg-gray-200 p-2 rounded-lg mr-3">⚙️</div>
                <h3 class="text-lg font-semibold text-gray-500">Configuración</h3>
            </div>
            <p class="text-gray-500 text-sm">Próximamente: configuraciones avanzadas</p>
        </div>
    </div>
</div>
</body>
</html>