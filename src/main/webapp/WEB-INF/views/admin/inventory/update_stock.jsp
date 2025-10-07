<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Actualizar Stock - ${product.nombre}</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <!-- Lucide Icons -->
    <script src="https://unpkg.com/lucide@latest/dist/umd/lucide.js"></script>
    <style>body{padding:1.5rem;background:#fafafa;color:#374151}</style>
</head>
<body>
<div class="max-w-5xl mx-auto bg-white rounded shadow p-6">
    
    <!-- Cabecera -->
    <div class="flex justify-between items-center mb-6">
        <div>
            <h1 class="text-2xl font-bold text-gray-800">Actualizar Stock</h1>
            <p class="text-gray-600 mt-1">Producto: <strong>${product.nombre}</strong></p>
        </div>
        <div class="flex items-center space-x-4">
            <span class="text-sm text-gray-600">Usuario: <strong>${sessionScope.username}</strong></span>
            <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
               class="text-gray-600 hover:text-gray-800 flex items-center gap-1">
                <i data-lucide="arrow-left" class="h-4 w-4"></i>
                Volver a productos
            </a>
        </div>
    </div>

    <!-- Mensajes Flash -->
    <c:if test="${not empty success}">
        <div class="mb-4 p-3 bg-green-100 text-green-700 rounded border border-green-200">
            <i data-lucide="check-circle" class="h-4 w-4 inline mr-2"></i>
            ${success}
        </div>
    </c:if>
    
    <c:if test="${not empty error}">
        <div class="mb-4 p-3 bg-red-100 text-red-700 rounded border border-red-200">
            <i data-lucide="x-circle" class="h-4 w-4 inline mr-2"></i>
            ${error}
        </div>
    </c:if>

    <!-- Información del producto -->
    <div class="bg-gray-50 p-4 rounded-lg mb-6 border border-gray-200">
        <h3 class="font-semibold text-gray-800 mb-3 flex items-center gap-2">
            <i data-lucide="info" class="h-4 w-4"></i>
            Información del Producto
        </h3>
        <div class="grid grid-cols-1 md:grid-cols-4 gap-4 text-sm">
            <div>
                <span class="text-gray-600">Stock Actual:</span>
                <p class="font-bold text-lg ${product.lowStock ? 'text-red-600' : 'text-green-600'}">
                    <fmt:formatNumber value="${product.stockActual}" pattern="#,##0.##"/> ${product.unidadMedida}
                </p>
                <c:if test="${product.lowStock}">
                    <span class="text-xs text-red-600 font-medium">¡STOCK BAJO!</span>
                </c:if>
            </div>
            <div>
                <span class="text-gray-600">Stock Mínimo:</span>
                <p class="font-semibold text-gray-800">
                    <fmt:formatNumber value="${product.stockMinimo}" pattern="#,##0.##"/> ${product.unidadMedida}
                </p>
            </div>
            <div>
                <span class="text-gray-600">Categoría:</span>
                <p class="font-semibold text-gray-800">${product.category.nombre}</p>
            </div>
            <div>
                <span class="text-gray-600">Precio Unitario:</span>
                <p class="font-semibold text-gray-800">
                    S/. <fmt:formatNumber value="${product.precioUnitario}" pattern="#,##0.00"/>
                </p>
            </div>
        </div>
    </div>

    <!-- Formulario de actualización -->
    <div class="bg-white border border-gray-200 rounded-lg p-6 mb-6">
        <h3 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
            <i data-lucide="edit" class="h-5 w-5"></i>
            Actualizar Stock
        </h3>
        
        <form method="post" action="${pageContext.request.contextPath}/admin/inventario/productos/${product.id}/stock" id="stockForm">
            <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                
                <!-- Tipo de movimiento -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i data-lucide="arrow-up-down" class="h-4 w-4 inline mr-1"></i>
                        Tipo de Movimiento *
                    </label>
                    <select name="tipoMovimiento" required 
                            class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" 
                            id="tipoMovimiento">
                        <option value="">Seleccione el tipo de movimiento</option>
                        <option value="ENTRADA"><i data-lucide="trending-up" class="h-3 w-3 inline mr-1"></i> Entrada de Stock (+)</option>
                        <option value="SALIDA"><i data-lucide="trending-down" class="h-3 w-3 inline mr-1"></i> Salida de Stock (-)</option>
                        <option value="AJUSTE"><i data-lucide="settings" class="h-3 w-3 inline mr-1"></i> Ajuste de Inventario (±)</option>
                    </select>
                </div>

                <!-- Cantidad -->
                <div>
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i data-lucide="hash" class="h-4 w-4 inline mr-1"></i>
                        <span id="cantidadLabel">Cantidad *</span>
                    </label>
                    <input type="number" name="cantidad" step="0.01" min="0.01" required
                           placeholder="Ingrese la cantidad"
                           class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                           id="cantidad">
                    <p class="text-xs text-gray-500 mt-1">Unidad: ${product.unidadMedida}</p>
                </div>

                <!-- Motivo -->
                <div class="md:col-span-2">
                    <label class="block text-sm font-medium text-gray-700 mb-2">
                        <i data-lucide="message-circle" class="h-4 w-4 inline mr-1"></i>
                        Motivo del Movimiento *
                    </label>
                    <textarea name="motivo" rows="3" required
                              placeholder="Describa el motivo de este movimiento de stock (ej: Compra de mercadería, Venta, Ajuste por inventario, etc.)"
                              class="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                              id="motivo"></textarea>
                </div>

                <!-- Vista previa -->
                <div class="md:col-span-2 bg-blue-50 p-4 rounded-lg border border-blue-200" id="previewBox" style="display: none;">
                    <h4 class="font-medium text-blue-800 mb-3 flex items-center gap-2">
                        <i data-lucide="calculator" class="h-4 w-4"></i>
                        Vista Previa del Resultado
                    </h4>
                    <div class="grid grid-cols-3 gap-4 text-sm">
                        <div>
                            <span class="text-blue-600">Stock Actual:</span>
                            <p class="font-semibold text-blue-800" id="previewCurrent">--</p>
                        </div>
                        <div>
                            <span class="text-blue-600" id="operacionLabel">Operación:</span>
                            <p class="font-semibold text-blue-800" id="previewOperation">--</p>
                        </div>
                        <div>
                            <span class="text-blue-600">Stock Final:</span>
                            <p class="font-semibold text-blue-800" id="previewFinal">--</p>
                        </div>
                    </div>
                    <div class="mt-3">
                        <p class="text-sm" id="alertMessage"></p>
                    </div>
                </div>
            </div>

            <!-- Botones de acción -->
            <div class="flex justify-end space-x-4 pt-6 border-t mt-6">
                <a href="${pageContext.request.contextPath}/admin/inventario/productos" 
                   class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 flex items-center gap-2">
                    <i data-lucide="x" class="h-4 w-4"></i>
                    Cancelar
                </a>
                <button type="submit" 
                        class="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 flex items-center gap-2"
                        id="submitBtn" disabled>
                    <i data-lucide="check" class="h-4 w-4"></i>
                    Actualizar Stock
                </button>
            </div>
        </form>
    </div>

    <!-- Historial de movimientos recientes -->
    <c:if test="${not empty recentMovements}">
        <div class="bg-white border border-gray-200 rounded-lg p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4 flex items-center gap-2">
                <i data-lucide="history" class="h-5 w-5"></i>
                Movimientos Recientes
            </h3>
            <div class="space-y-3">
                <c:forEach items="${recentMovements}" var="movement" varStatus="loop">
                    <c:if test="${loop.index < 5}">
                        <div class="bg-gray-50 p-4 rounded-lg border border-gray-200">
                            <div class="flex justify-between items-start">
                                <div class="flex items-center gap-3">
                                    <span class="inline-flex px-3 py-1 rounded-full text-xs font-medium
                                        <c:choose>
                                            <c:when test='${movement.tipoMovimiento == "ENTRADA"}'>bg-green-100 text-green-800</c:when>
                                            <c:when test='${movement.tipoMovimiento == "SALIDA"}'>bg-red-100 text-red-800</c:when>
                                            <c:otherwise>bg-yellow-100 text-yellow-800</c:otherwise>
                                        </c:choose>">
                                        ${movement.tipoMovimiento}
                                    </span>
                                    <span class="font-medium text-gray-900">
                                        <c:choose>
                                            <c:when test='${movement.tipoMovimiento == "ENTRADA"}'>+</c:when>
                                            <c:when test='${movement.tipoMovimiento == "SALIDA"}'>-</c:when>
                                            <c:otherwise>±</c:otherwise>
                                        </c:choose>
                                        <fmt:formatNumber value="${movement.cantidad}" pattern="#,##0.##"/> ${product.unidadMedida}
                                    </span>
                                </div>
                                <div class="text-right text-gray-600 text-sm">
                                    <div class="font-medium">
                                        ${movement.fechaMovimientoFormatted}
                                    </div>
                                    <div>${movement.usuarioResponsable}</div>
                                </div>
                            </div>
                            <c:if test="${not empty movement.motivo}">
                                <p class="text-gray-600 mt-2 text-sm italic">"${movement.motivo}"</p>
                            </c:if>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </c:if>
</div>

<!-- JavaScript -->
<script>
    // Variables del producto desde el servidor
    var stockActual = parseFloat('${product.stockActual}') || 0;
    var stockMinimo = parseFloat('${product.stockMinimo}') || 0;
    var unidadMedida = '${product.unidadMedida}';
    
    // Referencias a elementos DOM
    var tipoMovimientoSelect = document.getElementById('tipoMovimiento');
    var cantidadInput = document.getElementById('cantidad');
    var previewBox = document.getElementById('previewBox');
    var submitBtn = document.getElementById('submitBtn');
    var cantidadLabel = document.getElementById('cantidadLabel');
    
    function updateLabels() {
        var tipoMovimiento = tipoMovimientoSelect.value;
        
        switch(tipoMovimiento) {
            case 'ENTRADA':
                cantidadLabel.textContent = 'Cantidad a Agregar *';
                break;
            case 'SALIDA':
                cantidadLabel.textContent = 'Cantidad a Descontar *';
                break;
            case 'AJUSTE':
                cantidadLabel.textContent = 'Nueva Cantidad Total *';
                break;
            default:
                cantidadLabel.textContent = 'Cantidad *';
        }
        updatePreview();
    }
    
    function updatePreview() {
        var tipoMovimiento = tipoMovimientoSelect.value;
        var cantidad = parseFloat(cantidadInput.value) || 0;
        
        if (!tipoMovimiento || cantidad <= 0) {
            previewBox.style.display = 'none';
            submitBtn.disabled = true;
            return;
        }
        
        previewBox.style.display = 'block';
        
        var previewCurrent = document.getElementById('previewCurrent');
        var operacionLabel = document.getElementById('operacionLabel');
        var previewOperation = document.getElementById('previewOperation');
        var previewFinal = document.getElementById('previewFinal');
        var alertMessage = document.getElementById('alertMessage');
        
        previewCurrent.textContent = stockActual.toLocaleString() + ' ' + unidadMedida;
        
        var nuevoStock = stockActual;
        var operacion = '';
        
        switch(tipoMovimiento) {
            case 'ENTRADA':
                nuevoStock = stockActual + cantidad;
                operacion = '+ ' + cantidad.toLocaleString();
                operacionLabel.textContent = 'Se agregará:';
                break;
            case 'SALIDA':
                nuevoStock = stockActual - cantidad;
                operacion = '- ' + cantidad.toLocaleString();
                operacionLabel.textContent = 'Se descontará:';
                break;
            case 'AJUSTE':
                nuevoStock = cantidad;
                operacion = '= ' + cantidad.toLocaleString();
                operacionLabel.textContent = 'Nuevo total:';
                break;
        }
        
        previewOperation.textContent = operacion + ' ' + unidadMedida;
        previewFinal.textContent = nuevoStock.toLocaleString() + ' ' + unidadMedida;
        
        // Validaciones y alertas
        if (nuevoStock < 0) {
            alertMessage.innerHTML = '<i data-lucide="alert-triangle" class="h-3 w-3 inline mr-1"></i> ERROR: El stock no puede ser negativo';
            alertMessage.className = 'text-sm text-red-600 font-medium';
            submitBtn.disabled = true;
        } else if (nuevoStock < stockMinimo) {
            alertMessage.innerHTML = '<i data-lucide="alert-triangle" class="h-3 w-3 inline mr-1"></i> ALERTA: El stock quedará por debajo del mínimo (' + stockMinimo + ' ' + unidadMedida + ')';
            alertMessage.className = 'text-sm text-yellow-600 font-medium';
            submitBtn.disabled = false;
        } else {
            alertMessage.innerHTML = '<i data-lucide="check-circle" class="h-3 w-3 inline mr-1"></i> Stock dentro del rango normal';
            alertMessage.className = 'text-sm text-green-600 font-medium';
            submitBtn.disabled = false;
        }
        
        // Reinicializar los iconos después de cambiar el contenido
        lucide.createIcons();
    }
    
    // Event listeners
    document.addEventListener('DOMContentLoaded', function() {
        tipoMovimientoSelect.addEventListener('change', updateLabels);
        cantidadInput.addEventListener('input', updatePreview);
        
        // Inicializar Lucide Icons
        lucide.createIcons();
    });
</script>
</body>
</html>
