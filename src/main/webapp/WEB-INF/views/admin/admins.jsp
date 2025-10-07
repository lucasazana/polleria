<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Administradores</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-4xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Administradores</h1>
        <div>
            <a href="${pageContext.request.contextPath}/admin/create" class="bg-blue-500 text-white px-3 py-1 rounded">Crear admin</a>
            <a href="${pageContext.request.contextPath}/admin" class="text-sm text-gray-600 ml-4">← Volver</a>
        </div>
    </div>

    <table class="w-full table-auto text-left">
        <thead>
        <tr class="border-b">
            <th class="p-2">ID</th>
            <th class="p-2">Usuario</th>
            <th class="p-2">Activo</th>
            <th class="p-2">Acciones</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${admins}">
            <tr class="border-b">
                <td class="p-2">${u.id}</td>
                <td class="p-2">${u.username}</td>
                <td class="p-2">
                    <c:choose>
                        <c:when test="${u.active}">si</c:when>
                        <c:otherwise>no</c:otherwise>
                    </c:choose>
                </td>
                <td class="p-2">
                    <c:choose>
                        <c:when test="${u.username == 'admin'}">
                            <span class="text-sm text-gray-500">Cuenta principal (no editable)</span>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${u.active}">
                                    <a href="${pageContext.request.contextPath}/admin/users/${u.id}/edit" class="text-blue-600 mr-3">Editar</a>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/delete" style="display:inline" onsubmit="return confirm('Desactivar administrador?');">
                                        <button type="submit" class="text-red-600">Desactivar</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users/${u.id}/reactivate" style="display:inline">
                                        <button type="submit" class="text-green-600">Reactivar</button>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>