<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Panel de Usuario</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <style>body{background:#f8fafc}</style>
</head>
<body class="p-6">
    <div class="max-w-3xl mx-auto bg-white rounded shadow p-8">
        <div class="flex flex-col items-center md:flex-row md:items-center mb-6">
            <div class="w-24 h-24 bg-gray-200 rounded-full flex items-center justify-center text-3xl md:mr-6 mb-4 md:mb-0 overflow-hidden relative group" style="min-width:6rem;min-height:6rem;align-items:center;justify-content:center;">
                <c:choose>
                    <c:when test="${not empty user.fotoPerfil}">
                        <img src="${pageContext.request.contextPath}/user/foto/${user.id}" alt="Foto de perfil" class="object-cover w-full h-full rounded-full block" />
                    </c:when>
                    <c:otherwise>
                        <span class="material-icons">person</span>
                    </c:otherwise>
                </c:choose>
                <form method="post" action="${pageContext.request.contextPath}/user/foto-perfil" enctype="multipart/form-data" class="absolute bottom-0 left-0 w-full h-1/3 flex items-center justify-center bg-black bg-opacity-40 opacity-0 group-hover:opacity-100 transition-opacity duration-200 rounded-b-full">
                    <label class="cursor-pointer text-white text-xs flex items-center">
                        <span class="material-icons mr-1">photo_camera</span>
                        <input type="file" name="foto" accept="image/*" class="hidden" onchange="this.form.submit()" />
                        Cambiar
                    </label>
                </form>
            </div>
            <div class="flex flex-col items-center md:items-start w-full">
                <h1 class="text-2xl font-bold mb-1">Bienvenido, ${sessionScope.username}</h1>
                <p class="text-gray-600 text-sm">Rol: ${user.role}</p>
            </div>
            <div class="ml-auto md:ml-0 mt-2 md:mt-0">
                <a href="${pageContext.request.contextPath}/logout" class="inline-block px-4 py-2 bg-red-500 text-white rounded shadow hover:bg-red-600 transition-colors duration-200 text-sm font-semibold">Cerrar sesión</a>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="bg-red-100 text-red-800 p-2 mb-3 rounded">${error}</div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="bg-green-100 text-green-800 p-2 mb-3 rounded">${success}</div>
        </c:if>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div class="bg-gray-50 rounded p-6 shadow">
                <h2 class="text-lg font-semibold mb-4">Acciones rápidas</h2>
                <form method="post" action="${pageContext.request.contextPath}/user/asistencia/entrada" class="mb-2">
                    <button type="submit" class="w-full bg-green-500 text-white px-4 py-2 rounded mb-2">Marcar Entrada</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/user/asistencia/refrigerio" class="mb-2">
                    <button type="submit" class="w-full bg-yellow-500 text-white px-4 py-2 rounded mb-2">Marcar Refrigerio</button>
                </form>
                <form method="post" action="${pageContext.request.contextPath}/user/asistencia/salida">
                    <button type="submit" class="w-full bg-red-500 text-white px-4 py-2 rounded">Marcar Salida</button>
                </form>
            </div>
            <div class="bg-gray-50 rounded p-6 shadow">
                <h2 class="text-lg font-semibold mb-4">Estado de asistencia de hoy</h2>
                <c:choose>
                    <c:when test="${not empty asistencia}">
                        <ul class="list-disc ml-6">
                            <li>Entrada: <b>${asistencia.horaEntrada != null ? asistencia.horaEntrada : 'No registrada'}</b></li>
                            <li>Refrigerio: <b>${asistencia.horaRefrigerio != null ? asistencia.horaRefrigerio : 'No registrado'}</b></li>
                            <li>Salida: <b>${asistencia.horaSalida != null ? asistencia.horaSalida : 'No registrada'}</b></li>
                        </ul>
                    </c:when>
                    <c:otherwise>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <!-- Iconos de Google Material Icons -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
</body>
</html>
