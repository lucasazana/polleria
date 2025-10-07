package com.polleria.polleria.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        Object role = request.getSession(false) != null ? request.getSession(false).getAttribute("role") : null;
        String uri = request.getRequestURI();
        if (uri.startsWith(request.getContextPath() + "/admin")) {
            if (role == null || !"ADMIN".equalsIgnoreCase(role.toString())) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        } else if (uri.startsWith(request.getContextPath() + "/user")) {
            if (role == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
