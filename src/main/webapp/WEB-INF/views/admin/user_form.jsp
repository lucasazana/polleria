<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title><c:choose><c:when test="${not empty user}">Editar usuario</c:when><c:otherwise>Crear usuario</c:otherwise></c:choose></title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-md mx-auto bg-white p-6 rounded shadow">
    <div class="flex justify-between items-center mb-4">
        <h1 class="text-2xl"><c:choose><c:when test="${not empty user}">Editar usuario</c:when><c:otherwise>Crear usuario</c:otherwise></c:choose></h1>
        <a href="${pageContext.request.contextPath}/admin/users" class="text-sm text-gray-600">← Volver</a>
    </div>

    <c:if test="${not empty error}">
        <div role="alert" class="bg-red-100 text-red-800 p-2 mb-3">${error}</div>
    </c:if>
    <c:if test="${not empty success}">
        <div role="status" class="bg-green-100 text-green-800 p-2 mb-3">${success}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty user}">
            <form method="post" action="${pageContext.request.contextPath}/admin/users/${user.id}/edit" novalidate>
                <label for="username" class="block mb-2">Usuario</label>
                <input id="username" name="username" value="${user.username}" class="w-full p-2 border mb-3" <c:if test="${user.username == 'admin'}">disabled</c:if> />

                <label for="password" class="block mb-2">Nueva contraseña</label>
                <input id="password" name="password" type="password" class="w-full p-2 border mb-3" />

                <div class="flex justify-end">
                    <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Guardar</button>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <form method="post" action="${pageContext.request.contextPath}/admin/users/create" novalidate>
                <label for="username" class="block mb-2">Usuario</label>
                <input id="username" name="username" required minlength="3" class="w-full p-2 border mb-3" />

                <label for="password" class="block mb-2">Contraseña</label>
                <input id="password" name="password" type="password" required minlength="6" class="w-full p-2 border mb-3" />

                <div class="flex justify-end">
                    <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Crear</button>
                </div>
            </form>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>