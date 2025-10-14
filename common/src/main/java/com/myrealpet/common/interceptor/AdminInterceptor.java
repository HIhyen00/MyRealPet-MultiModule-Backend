package com.myrealpet.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 요청 허용
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String role = (String) request.getAttribute("role");
        Long userId = (Long) request.getAttribute("userId");
        String uri = request.getRequestURI();

        if (role == null) {
            log.warn("Role not found in request attributes");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return false;
        }

        // ADMIN 권한 확인
        if (!"ADMIN".equals(role)) {
            log.warn("Access denied for role: {} on URI: {}", role, request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Admin access required\"}");
            return false;
        }

        log.debug("Admin access granted for userId: {}", request.getAttribute("userId"));
        return true;
    }
}
