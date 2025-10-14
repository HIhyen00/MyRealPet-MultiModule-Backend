package com.myrealpet.sns.api.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myrealpet.sns.core.redis.RedisCacheService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final RedisCacheService redisCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // OPTIONS 요청은 CORS preflight이므로 통과
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        
        // Authorization 헤더에서 토큰 추출
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            log.warn("토큰이 없는 요청: {}", request.getRequestURI());
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "로그인이 필요합니다. userToken을 Authorization 헤더에 포함해주세요.");
            return false;
        }
        
        // Redis에서 토큰으로 accountId 조회
        Long accountId = redisCacheService.getValueByKey(token, Long.class);
        
        if (accountId == null) {
            log.warn("유효하지 않은 토큰: {}", token);
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않거나 만료된 토큰입니다. 다시 로그인해주세요.");
            return false;
        }
        
        // accountId를 request attribute에 저장 (컨트롤러에서 사용 가능)
        request.setAttribute("accountId", accountId);
        log.debug("인증 성공 - accountId: {}, path: {}", accountId, request.getRequestURI());
        
        return true;
    }
    
    /**
     * Authorization 헤더에서 토큰 추출
     * 형식: "Bearer {token}" 또는 "{token}"
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && !bearerToken.isEmpty()) {
            if (bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7).trim();
            }
            // Bearer 없이 토큰만 있는 경우도 허용
            return bearerToken.trim();
        }
        return null;
    }
    
    /**
     * JSON 형식의 에러 응답 전송
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", message);
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
