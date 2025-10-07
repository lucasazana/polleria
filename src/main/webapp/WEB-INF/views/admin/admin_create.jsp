<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Create Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:2rem}</style>
</head>
<body>
<div class="max-w-md mx-auto bg-white p-6 rounded shadow">
    <h1 class="text-2xl mb-4">Crear Usuario Administrador</h1>

    <c:if test="${not empty error}">
        <div role="alert" class="bg-red-100 text-red-800 p-2 mb-3">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div role="status" class="bg-green-100 text-green-800 p-2 mb-3">${success}</div>
    </c:if>

    <form method="post" action="/admin/create" novalidate>
        <label for="username" class="block mb-2">Usuario</label>
        <input id="username" name="username" required minlength="3" class="w-full p-2 border mb-3" />

        <label for="password" class="block mb-2">Contraseña</label>
        <input id="password" name="password" type="password" required minlength="6" class="w-full p-2 border mb-3" />

        <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Crear Administrador</button>
        </div>
    </form>

    <p class="mt-4 text-sm text-gray-600">Solo los administradores que han iniciado sesión pueden crear nuevos usuarios administradores.</p>
</div>
</body>
</html>
