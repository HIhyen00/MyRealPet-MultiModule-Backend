package com.myrealpet.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${KAKAO_DAPI_URL:https://dapi.kakao.com}")
    private String kakaoDapiUrl;

    @Value("${KAKAO_KAPI_URL:https://kapi.kakao.com}")
    private String kakaoKapiUrl;

    @Bean
    public WebClient kakaoWebClient() {
        return WebClient.builder()
                .baseUrl(kakaoDapiUrl)
                .build();
    }

    @Bean
    public WebClient kakaoApiClient() {
        return WebClient.builder()
                .baseUrl(kakaoKapiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
