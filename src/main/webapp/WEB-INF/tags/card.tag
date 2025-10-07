<%@ tag description="reusable card component" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="description" required="false" %>
<%@ attribute name="link" required="false" %>

<c:choose>
    <c:when test="${not empty link and link ne '#'}">
        <a href="${link}" class="block no-underline" aria-label="${title}">
            <div class="p-8 border rounded-lg shadow-lg bg-white hover:shadow-2xl transition-shadow duration-150">
                <h3 class="text-2xl font-semibold mb-3">${title}</h3>
                <p class="text-base text-gray-700 mb-4">${description}</p>
                <span class="inline-block bg-blue-600 text-white px-4 py-2 rounded">abrir</span>
            </div>
        </a>
    </c:when>
    <c:otherwise>
        <div class="p-8 border rounded-lg shadow-lg bg-gray-50">
            <h3 class="text-2xl font-semibold mb-3">${title}</h3>
            <p class="text-base text-gray-700 mb-4">${description}</p>
            <span class="text-sm text-gray-400">sin accion</span>
        </div>
    </c:otherwise>
</c:choose>
