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

        // Authorization 헤더 확인
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header for URI: {}", uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authorization header required\"}");
            return false;
        }

        // Bearer 토큰 추출
        String token = authHeader.substring(7);

        // Redis에서 세션 조회 (단일 조회로 간소화)
        Map<String, Object> session = userSessionUtil.getSession(token);
        if (session == null) {
            log.warn("Invalid or expired session for URI: {}", uri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return false;
        }

        // 세션에서 userId와 username 추출
        Object userIdObj = session.get("userId");
        Long userId = userIdObj instanceof Integer ? ((Integer) userIdObj).longValue() : (Long) userIdObj;
        String username = (String) session.get("username");

        // 요청에 사용자 정보 저장 (컨트롤러에서 사용 가능)
        request.setAttribute("userId", userId);
        request.setAttribute("username", username);

        // 세션 갱신 (매 요청마다 TTL 연장)
        userSessionUtil.refreshSession(token);

        log.debug("Authentication successful for userId: {}, username: {}, uri: {}", userId, username, uri);
        return true;
    }
}