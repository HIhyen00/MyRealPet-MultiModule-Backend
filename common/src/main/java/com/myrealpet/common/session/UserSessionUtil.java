package com.myrealpet.common.session;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSessionUtil {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SESSION_PREFIX = "session:";
    private static final Duration DEFAULT_EXPIRY = Duration.ofHours(24); // 24시간

    /**
     * 세션 저장 (로그인 시 호출) - 토큰을 키로 사용
     */
    public void saveSession(String token, Long userId, String username, String role) {
        try {
            Map<String, Object> sessionData = new HashMap<>();
            sessionData.put("userId", userId);
            sessionData.put("username", username);
            sessionData.put("role", role);
            sessionData.put("loginTime", System.currentTimeMillis());

            String sessionJson = objectMapper.writeValueAsString(sessionData);
            redisTemplate.opsForValue().set(SESSION_PREFIX + token, sessionJson, DEFAULT_EXPIRY);

            log.info("Session saved: token={}, userId={}, username={}", token, userId, username);
        } catch (Exception e) {
            log.error("Failed to save session: token={}, userId={}", token, userId, e);
        }
    }

    /**
     * 토큰으로 세션 조회
     */
    public Map<String, Object> getSession(String token) {
        try {
            String sessionJson = redisTemplate.opsForValue().get(SESSION_PREFIX + token);
            if (sessionJson != null) {
                return objectMapper.readValue(sessionJson, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to get session: token={}", token, e);
        }
        return null;
    }

    /**
     * 세션 삭제 (로그아웃 시 호출)
     */
    public void removeSession(String token) {
        redisTemplate.delete(SESSION_PREFIX + token);
        log.info("Session removed: token={}", token);
    }

    /**
     * 세션이 존재하는지 확인
     */
    public boolean isSessionExists(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(SESSION_PREFIX + token));
    }

    /**
     * 세션 갱신 (TTL 연장)
     */
    public void refreshSession(String token) {
        try {
            redisTemplate.expire(SESSION_PREFIX + token, DEFAULT_EXPIRY);
            log.debug("Session refreshed: token={}", token);
        } catch (Exception e) {
            log.error("Failed to refresh session: token={}", token, e);
        }
    }
}