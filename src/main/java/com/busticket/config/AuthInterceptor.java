package com.busticket.config;

import com.busticket.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (uri.contains("/admin/")) {
            if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vùng Admin!");
                return false;
            }
        }

        if (uri.contains("/staff/")) {
            if (currentUser == null || (!"STAFF".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole()))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập vùng Nhân viên!");
                return false;
            }
        }

        return true;
    }
}