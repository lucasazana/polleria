<!-- lista de usuarios con rol USER para gestion administrativa -->
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Gestionar Empleados</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-4xl mx-auto bg-white rounded shadow p-6">
    <!-- header -->
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Empleados</h1>
        <div>
            <a href="${pageContext.request.contextPath}/admin/empleados/crear" class="bg-blue-500 text-white px-3 py-1 rounded">Crear empleado</a>
            <a href="${pageContext.request.contextPath}/admin" class="text-sm text-gray-600 ml-4">← Volver</a>
        </div>
    </div>

    <!-- mensajes flash -->
    <c:if test="${not empty success}">
        <div class="mb-4 p-3 bg-green-100 text-green-700 rounded">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="mb-4 p-3 bg-red-100 text-red-700 rounded">${error}</div>
    </c:if>

    <!-- tabla de usuarios -->
    <table class="w-full table-auto text-left">
        <thead>
        <tr class="border-b">
            <th class="p-2">ID</th>
            <th class="p-2">Usuario</th>
            <th class="p-2">Activo</th>
            <th class="p-2">Acciones</th>
        </tr>
        </thead>
        <!-- contenido de la tabla -->
        <tbody>
        <c:forEach var="emp" items="${employees}">
            <tr class="border-b">
                <td class="p-2">${emp.id}</td>
                <td class="p-2">${emp.username}</td>
                <td class="p-2">
                    <c:choose>
                        <c:when test="${emp.active}">si</c:when>
                        <c:otherwise>no</c:otherwise>
                    </c:choose>
                </td>
                <!-- botones -->
                <td class="p-2">
                    <c:choose>
                        <c:when test="${emp.active}">
                            <!-- usuario activo -->
                            <a href="${pageContext.request.contextPath}/admin/empleados/${emp.id}/editar" class="text-blue-600 mr-3">Editar</a>
                            <form method="post" action="${pageContext.request.contextPath}/admin/empleados/${emp.id}/eliminar" style="display:inline" onsubmit="return confirm('Desactivar usuario?');">
                                <button type="submit" class="text-red-600">Desactivar</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <!-- usuario inactivo -->
                            <form method="post" action="${pageContext.request.contextPath}/admin/empleados/${emp.id}/reactivar" style="display:inline">
                                <button type="submit" class="text-green-600">Reactivar</button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <!-- mensaje si no hay usuarios -->
        <c:if test="${empty employees}">
            <tr>
                <td colspan="4" class="p-2 text-center text-gray-500">No hay empleados registrados</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>