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
    <h1 class="text-2xl mb-4">Create Admin User</h1>

    <c:if test="${not empty error}">
        <div class="bg-red-100 text-red-800 p-2 mb-3">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="bg-green-100 text-green-800 p-2 mb-3">${success}</div>
    </c:if>

    <form method="post" action="/admin/create">
        <label class="block mb-2">Username</label>
        <input name="username" class="w-full p-2 border mb-3" />

        <label class="block mb-2">Password</label>
        <input name="password" type="password" class="w-full p-2 border mb-3" />

        <div class="flex justify-end">
            <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded">Create Admin</button>
        </div>
    </form>

    <p class="mt-4 text-sm text-gray-600">Only logged-in admins can create new admin users.</p>
</div>
</body>
</html>
