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

    private static final String USER_SESSION_PREFIX = "user:session:";
    private static final Duration DEFAULT_EXPIRY = Duration.ofHours(24); // 24시간

    /**
     * 사용자 세션 저장 (로그인 시 호출)
     */
    public void saveUserSession(Long userId, String username) {
        try {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("username", username);
            userInfo.put("loginTime", System.currentTimeMillis());

            String userInfoJson = objectMapper.writeValueAsString(userInfo);
            redisTemplate.opsForValue().set(USER_SESSION_PREFIX + userId, userInfoJson, DEFAULT_EXPIRY);

            log.debug("User session saved: userId={}, username={}", userId, username);
        } catch (Exception e) {
            log.error("Failed to save user session: userId={}", userId, e);
        }
    }

    /**
     * 사용자 세션 조회
     */
    public Map<String, Object> getUserSession(Long userId) {
        try {
            String userInfoJson = redisTemplate.opsForValue().get(USER_SESSION_PREFIX + userId);
            if (userInfoJson != null) {
                return objectMapper.readValue(userInfoJson, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to get user session: userId={}", userId, e);
        }
        return null;
    }

    /**
     * 사용자 세션 삭제 (로그아웃 시 호출)
     */
    public void removeUserSession(Long userId) {
        redisTemplate.delete(USER_SESSION_PREFIX + userId);
        log.debug("User session removed: userId={}", userId);
    }

    /**
     * 세션이 존재하는지 확인
     */
    public boolean isSessionExists(Long userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(USER_SESSION_PREFIX + userId));
    }
}