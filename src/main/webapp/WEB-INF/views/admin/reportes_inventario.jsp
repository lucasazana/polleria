<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Reportes de Inventario</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f5f5f5}</style>
</head>
<body>
<div class="max-w-6xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Reportes de Inventario</h1>
        <a href="${pageContext.request.contextPath}/admin/reportes" class="text-sm text-gray-500 hover:text-gray-700">← Volver a reportes</a>
    </div>

    <!-- Filtros -->
    <form method="get" action="${pageContext.request.contextPath}/admin/reportes/inventario" class="bg-gray-100 p-4 rounded mb-6 border border-gray-200">
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Tipo de Reporte</label>
                <select name="tipoReporte" class="w-full border border-gray-300 rounded px-3 py-2 bg-white focus:outline-none focus:border-gray-500">
                    <option value="productos" ${tipoReporte == 'productos' ? 'selected' : ''}>Listado de Productos</option>
                    <option value="stockBajo" ${tipoReporte == 'stockBajo' ? 'selected' : ''}>Productos con Stock Bajo</option>
                    <option value="porVencer" ${tipoReporte == 'porVencer' ? 'selected' : ''}>Productos por Vencer</option>
                    <option value="movimientos" ${tipoReporte == 'movimientos' ? 'selected' : ''}>Movimientos de Inventario</option>
                </select>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Categoría</label>
                <select name="categoriaId" class="w-full border border-gray-300 rounded px-3 py-2 bg-white focus:outline-none focus:border-gray-500">
                    <option value="">Todas las categorías</option>
                    <c:forEach var="cat" items="${categorias}">
                        <option value="${cat.id}" ${categoriaId == cat.id ? 'selected' : ''}>${cat.nombre}</option>
                    </c:forEach>
                </select>
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Desde</label>
                <input type="date" name="desde" value="${desde}" class="w-full border border-gray-300 rounded px-3 py-2 bg-white focus:outline-none focus:border-gray-500" />
            </div>
            <div>
                <label class="block text-sm font-medium text-gray-700 mb-1">Hasta</label>
                <input type="date" name="hasta" value="${hasta}" class="w-full border border-gray-300 rounded px-3 py-2 bg-white focus:outline-none focus:border-gray-500" />
            </div>
        </div>
        <div class="mt-4 flex gap-2">
            <button type="submit" class="px-4 py-2 bg-gray-700 text-white rounded hover:bg-gray-800 transition-colors">Filtrar</button>
            <a href="${pageContext.request.contextPath}/admin/reportes/inventario/pdf?tipoReporte=${tipoReporte}&categoriaId=${categoriaId}&desde=${desde}&hasta=${hasta}" 
               class="px-4 py-2 bg-gray-900 text-white rounded hover:bg-black transition-colors inline-flex items-center">
                📄 Descargar PDF
            </a>
        </div>
    </form>

    <!-- Resumen -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
        <div class="bg-gray-50 p-4 rounded text-center border border-gray-200">
            <p class="text-3xl font-bold text-gray-800">${totalProductos}</p>
            <p class="text-sm text-gray-500">Productos Activos</p>
        </div>
        <div class="bg-gray-50 p-4 rounded text-center border border-gray-200">
            <p class="text-3xl font-bold text-gray-800">${productosStockBajo}</p>
            <p class="text-sm text-gray-500">Stock Bajo</p>
        </div>
        <div class="bg-gray-50 p-4 rounded text-center border border-gray-200">
            <p class="text-3xl font-bold text-gray-800">${productosPorVencer}</p>
            <p class="text-sm text-gray-500">Por Vencer (30 días)</p>
        </div>
        <div class="bg-gray-50 p-4 rounded text-center border border-gray-200">
            <p class="text-3xl font-bold text-gray-800">S/ <fmt:formatNumber value="${valorTotal}" pattern="#,##0.00"/></p>
            <p class="text-sm text-gray-500">Valor Total</p>
        </div>
    </div>

    <!-- Tabla de resultados -->
    <c:if test="${tipoReporte == 'productos' || tipoReporte == 'stockBajo' || tipoReporte == 'porVencer' || tipoReporte == null}">
        <div class="overflow-x-auto">
            <table class="min-w-full bg-white border border-gray-300">
                <thead class="bg-gray-200">
                    <tr>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Producto</th>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Categoría</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Stock Actual</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Stock Mínimo</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Precio Unit.</th>
                        <th class="px-4 py-2 text-center border border-gray-300 text-gray-700">Vencimiento</th>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Ubicación</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${productos}">
                        <tr class="${p.stockActual.compareTo(p.stockMinimo) <= 0 ? 'bg-gray-100' : 'hover:bg-gray-50'}">
                            <td class="px-4 py-2 border border-gray-300">${p.nombre}</td>
                            <td class="px-4 py-2 border border-gray-300">${p.category.nombre}</td>
                            <td class="px-4 py-2 border border-gray-300 text-right">${p.stockActual} ${p.unidadMedida}</td>
                            <td class="px-4 py-2 border border-gray-300 text-right">${p.stockMinimo} ${p.unidadMedida}</td>
                            <td class="px-4 py-2 border border-gray-300 text-right">S/ <fmt:formatNumber value="${p.precioUnitario}" pattern="#,##0.00"/></td>
                            <td class="px-4 py-2 border border-gray-300 text-center">
                                <c:if test="${p.fechaVencimiento != null}">
                                    <fmt:parseDate value="${p.fechaVencimiento}" pattern="yyyy-MM-dd" var="parsedDate" type="date" />
                                    <fmt:formatDate value="${parsedDate}" pattern="dd/MM/yyyy" />
                                </c:if>
                            </td>
                            <td class="px-4 py-2 border border-gray-300">${p.ubicacion}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty productos}">
                        <tr>
                            <td colspan="7" class="px-4 py-8 text-center text-gray-500">No se encontraron productos</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </c:if>

    <!-- Tabla de movimientos -->
    <c:if test="${tipoReporte == 'movimientos'}">
        <div class="overflow-x-auto">
            <table class="min-w-full bg-white border border-gray-300">
                <thead class="bg-gray-200">
                    <tr>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Fecha</th>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Producto</th>
                        <th class="px-4 py-2 text-center border border-gray-300 text-gray-700">Tipo</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Cantidad</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Stock Anterior</th>
                        <th class="px-4 py-2 text-right border border-gray-300 text-gray-700">Stock Nuevo</th>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Motivo</th>
                        <th class="px-4 py-2 text-left border border-gray-300 text-gray-700">Usuario</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="m" items="${movimientos}">
                        <tr class="hover:bg-gray-50">
                            <td class="px-4 py-2 border border-gray-300">
                                <fmt:parseDate value="${m.fechaMovimiento}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                <fmt:formatDate value="${parsedDateTime}" pattern="dd/MM/yyyy HH:mm" />
                            </td>
                            <td class="px-4 py-2 border border-gray-300">${m.product.nombre}</td>
                            <td class="px-4 py-2 border border-gray-300 text-center">
                                <span class="px-2 py-1 rounded text-xs font-bold
                                    ${m.tipoMovimiento == 'ENTRADA' ? 'bg-gray-200 text-gray-800' : ''}
                                    ${m.tipoMovimiento == 'SALIDA' ? 'bg-gray-400 text-white' : ''}
                                    ${m.tipoMovimiento == 'AJUSTE' ? 'bg-gray-300 text-gray-700' : ''}">
                                    ${m.tipoMovimiento}
                                </span>
                            </td>
                            <td class="px-4 py-2 border text-right">${m.cantidad}</td>
                            <td class="px-4 py-2 border text-right">${m.stockAnterior}</td>
                            <td class="px-4 py-2 border text-right">${m.stockNuevo}</td>
                            <td class="px-4 py-2 border">${m.motivo}</td>
                            <td class="px-4 py-2 border">${m.usuarioResponsable}</td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty movimientos}">
                        <tr>
                            <td colspan="8" class="px-4 py-8 text-center text-gray-500">No se encontraron movimientos. Seleccione un rango de fechas.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </c:if>
</div>
</body>
</html>
