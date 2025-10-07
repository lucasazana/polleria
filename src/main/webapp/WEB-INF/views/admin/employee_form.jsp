<%-- formulario reutilizable para crear y editar usuarios --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <!-- titulo dinamico -->
    <title><c:choose><c:when test="${not empty user}">Editar</c:when><c:otherwise>Crear</c:otherwise></c:choose> Usuario</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:2rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-md mx-auto bg-white rounded shadow p-6">
    <!-- header -->
    <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl font-bold">
            <c:choose>
                <c:when test="${not empty user}">Editar Empleado</c:when>
                <c:otherwise>Crear Empleado</c:otherwise>
            </c:choose>
        </h1>
        <a href="${pageContext.request.contextPath}/admin/empleados" class="text-sm text-gray-600">← Volver</a>
    </div>

    <!-- mensajes de error y exito -->
    <c:if test="${not empty error}">
        <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="mb-4 p-3 bg-green-100 text-green-700 rounded">${success}</div>
    </c:if>

    <!-- logica condicional, si existe user es edicion sino es creacion -->
    <c:choose>
        <c:when test="${not empty user}">
            <!-- formulario de edicion -->
            <form method="post" action="${pageContext.request.contextPath}/admin/empleados/${user.id}/editar" novalidate>
                <div class="mb-4">
                    <label for="username" class="block text-sm font-medium text-gray-700 mb-2">Empleado</label>
                    <input type="text" id="username" name="username" value="${user.username}" 
                           class="w-full p-2 border border-gray-300 rounded" minlength="3">
                </div>
                
                <div class="mb-4">
                    <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Nueva Contraseña (opcional)</label>
                    <input type="password" id="password" name="password" 
                           class="w-full p-2 border border-gray-300 rounded" minlength="6">
                    <p class="text-xs text-gray-500 mt-1">Deja en blanco para mantener la contraseña actual</p>
                </div>
                
                <!-- botones para edicion -->
                <div class="flex justify-end space-x-3">
                    <a href="${pageContext.request.contextPath}/admin/empleados" 
                       class="bg-gray-500 text-white px-4 py-2 rounded">Cancelar</a>
                    <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Actualizar</button>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <!-- formulario de creacion -->
            <form method="post" action="${pageContext.request.contextPath}/admin/empleados/crear" novalidate>
                <div class="mb-4">
                    <label for="username" class="block text-sm font-medium text-gray-700 mb-2">Usuario</label>
                    <input type="text" id="username" name="username" required minlength="3" 
                           class="w-full p-2 border border-gray-300 rounded">
                </div>
                
                <div class="mb-4">
                    <label for="password" class="block text-sm font-medium text-gray-700 mb-2">Contraseña</label>
                    <input type="password" id="password" name="password" required minlength="6" 
                           class="w-full p-2 border border-gray-300 rounded">
                </div>
                
                <!-- botones para creacion -->
                <div class="flex justify-end space-x-3">
                    <a href="${pageContext.request.contextPath}/admin/empleados" 
                       class="bg-gray-500 text-white px-4 py-2 rounded">Cancelar</a>
                    <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Crear Empleado</button>
                </div>
            </form>
        </c:otherwise>
    </c:choose>

    <%-- texto informativo sobre permisos --%>
    <p class="mt-4 text-sm text-gray-600">Solo los administradores pueden gestionar empleados.</p>
</div>
</body>
</html>