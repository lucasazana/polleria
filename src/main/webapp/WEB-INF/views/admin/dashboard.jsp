<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-4xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Dashboard - Admin</h1>
        <div>
            <span class="text-sm text-gray-600 mr-4">Usuario: <strong>${sessionScope.username}</strong></span>
            <a href="${pageContext.request.contextPath}/logout" class="text-red-600">Cerrar sesión</a>
        </div>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
    <t:card title="gestion de administradores" description="crear y administrar cuentas de administradores" link="${pageContext.request.contextPath}/admin/administradores" />
    <t:card title="gestion de empleados" description="crear y administrar cuentas de empleados" link="${pageContext.request.contextPath}/admin/empleados" />
    <t:card title="gestion de inventario" description="control de stock y productos" link="${pageContext.request.contextPath}/admin/inventario" />
    <t:card title="asistencia" description="registro de asistencia de empleados" link="${pageContext.request.contextPath}/admin/asistencia" />
    <t:card title="reportes" description="generar reportes de inventario y asistencia" link="${pageContext.request.contextPath}/admin/reportes" />
    </div>

    <div class="mt-6">
        <h3 class="font-semibold mb-2">Actividad reciente</h3>
        <p class="text-sm text-gray-600">No hay actividad registrada todavía.</p>
    </div>
</div>
</body>
</html>
