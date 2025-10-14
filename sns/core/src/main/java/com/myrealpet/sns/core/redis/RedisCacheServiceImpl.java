package com.myrealpet.sns.core.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    final private StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SESSION_PREFIX = "session:";

    @Override
    public <K, V> void setKeyAndValue(K key, V value) {
        setKeyAndValue(key, value, Duration.ofMinutes(720));
    }

    @Override
    public <K, V> void setKeyAndValue(K key, V value, Duration timeout) {
        String keyAsString = SESSION_PREFIX + String.valueOf(key);
        String valueAsString = String.valueOf(value);

        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        valueOps.set(keyAsString, valueAsString, timeout);
    }

    @Override
    public <T> T getValueByKey(String key, Class<T> clazz) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(SESSION_PREFIX + key);

        if(value == null) {
            return null;
        }

        // Account 서버의 세션 JSON 파싱
        if(clazz == Long.class && value.startsWith("{")) {
            try {
                Map<String, Object> sessionData = objectMapper.readValue(value, Map.class);
                Object userId = sessionData.get("userId");
                if (userId instanceof Number) {
                    return clazz.cast(((Number) userId).longValue());
                }
            } catch (Exception e) {
                log.error("Failed to parse session JSON: {}", value, e);
                return null;
            }
        }

        if(clazz == String.class) {
            return clazz.cast(value);
        }

        if(clazz == Integer.class) {
            return clazz.cast(Integer.valueOf(value));
        }

        if(clazz == Long.class) {
            return clazz.cast(Long.valueOf(value));
        }

        throw new IllegalArgumentException("Unsupported class: " + clazz);
    }

    @Override
    public void deleteByKey(String token) {
        redisTemplate.delete(SESSION_PREFIX + token);
    }

    @Override
    public String getUsernameByToken(String token) {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get(SESSION_PREFIX + token);

        if(value == null) {
            return null;
        }

        // Account 서버의 세션 JSON에서 username 추출
        if(value.startsWith("{")) {
            try {
                Map<String, Object> sessionData = objectMapper.readValue(value, Map.class);
                Object username = sessionData.get("username");
                return username != null ? username.toString() : null;
            } catch (Exception e) {
                log.error("Failed to parse session JSON for username: {}", value, e);
                return null;
            }
        }

        return null;
    }
}
