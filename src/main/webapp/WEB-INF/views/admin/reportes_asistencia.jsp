<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Reportes de Asistencia</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-4xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Reportes de Asistencia</h1>
        <a href="${pageContext.request.contextPath}/admin/reportes" class="inline-block px-4 py-2 bg-blue-600 text-white rounded shadow hover:bg-blue-700 transition-colors duration-200 text-sm font-semibold">← Volver a reportes</a>
    </div>

    <form method="get" action="" class="flex flex-col md:flex-row gap-4 mb-6 items-end">
        <div>
            <label class="block text-gray-700 text-sm mb-1">Empleado</label>
            <select name="empleadoId" class="border rounded px-3 py-2 w-48">
                <option value="">Todos</option>
                <c:forEach var="empleado" items="${empleados}">
                    <option value="${empleado.id}" <c:if test='${empleadoId == empleado.id}'>selected</c:if>>${empleado.username}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label class="block text-gray-700 text-sm mb-1">Desde</label>
            <input type="date" name="desde" class="border rounded px-3 py-2" value="${desde}" />
        </div>
        <div>
            <label class="block text-gray-700 text-sm mb-1">Hasta</label>
            <input type="date" name="hasta" class="border rounded px-3 py-2" value="${hasta}" />
        </div>
        <button type="submit" class="px-4 py-2 bg-green-600 text-white rounded shadow hover:bg-green-700 transition-colors duration-200 font-semibold">Filtrar</button>
        <button type="submit" formaction="${pageContext.request.contextPath}/admin/reportes/asistencia/pdf" formtarget="_blank" class="px-4 py-2 bg-red-600 text-white rounded shadow hover:bg-red-700 transition-colors duration-200 font-semibold">Exportar PDF</button>
    </form>

    <!-- Aquí irá la tabla de resultados filtrados -->
    <div class="overflow-x-auto">
        <table class="min-w-full bg-white border rounded">
            <thead>
                <tr>
                    <th class="px-4 py-2 border">Empleado</th>
                    <th class="px-4 py-2 border">Fecha</th>
                    <th class="px-4 py-2 border">Entrada</th>
                    <th class="px-4 py-2 border">Refrigerio</th>
                    <th class="px-4 py-2 border">Salida</th>
                    <th class="px-4 py-2 border">Observaciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="asistencia" items="${asistencias}">
                    <tr>
                        <td class="px-4 py-2 border">${asistencia.empleado.username}</td>
                        <td class="px-4 py-2 border">${asistencia.fecha}</td>
                        <td class="px-4 py-2 border">${asistencia.horaEntrada}</td>
                        <td class="px-4 py-2 border">${asistencia.horaRefrigerio}</td>
                        <td class="px-4 py-2 border">${asistencia.horaSalida}</td>
                        <td class="px-4 py-2 border">${asistencia.observaciones}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>
