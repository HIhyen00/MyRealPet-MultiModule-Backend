package com.myrealpet.common.interceptor;

import com.myrealpet.common.session.UserSessionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final UserSessionUtil userSessionUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 인증이 필요하지 않은 경로는 건너뛰기
        if (uri.startsWith("/api/user/session") ||
            uri.startsWith("/api/auth") ||
            uri.startsWith("/api/kakao-maps") ||
            uri.startsWith("/actuator")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for URI: {}", uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authorization header required\"}");
            response.setContentType("application/json");
            return false;
        }

        // 간단한 방법: 프론트에서 X-User-ID 헤더로 userId 전송
        String userIdHeader = request.getHeader("X-User-ID");
        if (userIdHeader == null) {
            log.warn("Missing X-User-ID header for URI: {}", uri);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"X-User-ID header required\"}");
            response.setContentType("application/json");
            return false;
        }

        try {
            Long userId = Long.parseLong(userIdHeader);

            // Redis에서 세션 확인
            Map<String, Object> session = userSessionUtil.getUserSession(userId);
            if (session == null) {
                log.warn("No active session found for userId: {}", userId);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"No active session found\"}");
                response.setContentType("application/json");
                return false;
            }

            // 요청에 userId 저장하여 컨트롤러에서 사용할 수 있도록 함
            request.setAttribute("userId", userId);
            request.setAttribute("username", session.get("username"));

            log.debug("Authentication successful for userId: {}, username: {}", userId, session.get("username"));
            return true;

        } catch (NumberFormatException e) {
            log.warn("Invalid userId format: {}", userIdHeader);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Invalid userId format\"}");
            response.setContentType("application/json");
            return false;
        }
    }
}