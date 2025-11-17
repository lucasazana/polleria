<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Asistencia</title>
    <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
    <style>body{padding:1.5rem;background:#f8fafc}</style>
</head>
<body>
<div class="max-w-3xl mx-auto bg-white rounded shadow p-6">
    <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold">Asistencia</h1>
        <a href="${pageContext.request.contextPath}/admin" class="text-sm text-gray-600">← Volver al dashboard</a>
    </div>

    <div class="mb-6">
        <h2 class="text-lg font-semibold mb-2">Asistencia de empleados - ${fechaHoy}</h2>
        <div class="overflow-x-auto">
            <table class="min-w-full bg-white border rounded">
                <thead>
                    <tr>
                        <th class="px-4 py-2 border">Empleado</th>
                        <th class="px-4 py-2 border">Entrada</th>
                        <th class="px-4 py-2 border">Refrigerio</th>
                        <th class="px-4 py-2 border">Salida</th>
                        <th class="px-4 py-2 border">Observaciones</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="empleado" items="${empleados}">
                        <c:set var="asistenciaEncontrada" value="" />
                        <c:forEach var="a" items="${asistencias}">
                            <c:if test="${a.empleado.id == empleado.id}">
                                <c:set var="asistenciaEncontrada" value="${a}" scope="page" />
                            </c:if>
                        </c:forEach>
                        <tr>
                            <td class="px-4 py-2 border">${empleado.username}</td>
                            <td class="px-4 py-2 border">
                                <c:choose>
                                    <c:when test="${not empty asistenciaEncontrada && asistenciaEncontrada.horaEntrada != null}">
                                        ${asistenciaEncontrada.horaEntrada}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-gray-400">No registrada</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="px-4 py-2 border">
                                <c:choose>
                                    <c:when test="${not empty asistenciaEncontrada && asistenciaEncontrada.horaRefrigerio != null}">
                                        ${asistenciaEncontrada.horaRefrigerio}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-gray-400">No registrada</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="px-4 py-2 border">
                                <c:choose>
                                    <c:when test="${not empty asistenciaEncontrada && asistenciaEncontrada.horaSalida != null}">
                                        ${asistenciaEncontrada.horaSalida}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-gray-400">No registrada</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td class="px-4 py-2 border">
                                <c:choose>
                                    <c:when test="${not empty asistenciaEncontrada && asistenciaEncontrada.observaciones != null}">
                                        ${asistenciaEncontrada.observaciones}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-gray-400">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</div>
</body>
</html>