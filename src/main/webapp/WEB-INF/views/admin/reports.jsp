<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Reportes</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f5f5f5}</style>
</head>
<body>
<div class="max-w-3xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Reportes</h1>
        <a href="${pageContext.request.contextPath}/admin" class="text-sm text-gray-500 hover:text-gray-700">← Volver al dashboard</a>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 gap-6 mt-8">
        <div class="bg-gray-50 rounded shadow p-6 flex flex-col items-center border border-gray-200">
            <h2 class="text-xl font-bold mb-2 text-gray-800">Reportes de Inventario</h2>
            <p class="text-gray-500 mb-4 text-center">Visualiza y descarga reportes detallados del inventario.</p>
            <a href="${pageContext.request.contextPath}/admin/reportes/inventario" class="px-6 py-2 bg-gray-700 text-white rounded shadow hover:bg-gray-800 transition-colors duration-200 font-semibold">Ver reportes de inventario</a>
        </div>
        <div class="bg-gray-50 rounded shadow p-6 flex flex-col items-center border border-gray-200">
            <h2 class="text-xl font-bold mb-2 text-gray-800">Reportes de Asistencia</h2>
            <p class="text-gray-500 mb-4 text-center">Consulta la asistencia diaria y el historial de empleados.</p>
            <a href="${pageContext.request.contextPath}/admin/reportes/asistencia" class="px-6 py-2 bg-gray-700 text-white rounded shadow hover:bg-gray-800 transition-colors duration-200 font-semibold">Ver reportes de asistencia</a>
        </div>
    </div>
</div>
</body>
</html>