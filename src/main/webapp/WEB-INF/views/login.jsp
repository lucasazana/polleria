<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8" />
  <title>Login - Pollería</title>
  <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-yellow-50 flex items-center justify-center min-h-screen">
  <div class="w-full max-w-md bg-white p-8 rounded shadow">
    <h2 class="text-2xl font-bold text-red-700 mb-6">Iniciar sesión</h2>

    <c:if test="${not empty error}">
      <div class="bg-red-100 text-red-700 p-2 rounded mb-4">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post" class="space-y-4">
      <div>
        <label class="block text-sm">Usuario</label>
        <input type="text" name="username" required class="w-full border rounded px-3 py-2"/>
      </div>
      <div>
        <label class="block text-sm">Contraseña</label>
        <input type="password" name="password" required class="w-full border rounded px-3 py-2"/>
      </div>
      <div>
        <button type="submit" class="w-full bg-red-600 text-white px-3 py-2 rounded">Entrar</button>
      </div>
    </form>
  </div>
</body>
</html>
