<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Gestión de Empleados</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-3xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Gestión de Empleados</h1>
        <a href="${pageContext.request.contextPath}/admin" class="text-sm text-gray-600">← Volver al dashboard</a>
    </div>

    <p class="text-sm text-gray-600">Aquí ira la lista y formularios para crear/editar empleados. Por ahora es un placeholder.</p>

    <div class="mt-6">
        <a href="${pageContext.request.contextPath}/admin/create" class="bg-blue-500 text-white px-4 py-2 rounded">Crear admin (ejemplo)</a>
    </div>
</div>
</body>
</html>