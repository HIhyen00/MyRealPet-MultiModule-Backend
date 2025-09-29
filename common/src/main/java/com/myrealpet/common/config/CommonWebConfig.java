package com.myrealpet.common.config;

import com.myrealpet.common.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.auth.interceptor.enabled", havingValue = "true", matchIfMissing = false)
public class CommonWebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")  // 모든 API 경로에 적용
                .excludePathPatterns(
                        "/api/auth/**",           // 인증 관련 API 제외
                        "/api/user/session/**",  // 세션 조회 API 제외
                        "/actuator/**"           // 모니터링 API 제외
                );
    }
}