<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Panel de Usuario</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="p-6">
    <h1 class="text-2xl font-bold">Panel de Usuario</h1>
    <p>Bienvenido, ${sessionScope.username}</p>
    <p><a href="${pageContext.request.contextPath}/logout">Cerrar sesión</a></p>
</body>
</html>
