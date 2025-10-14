package com.myrealpet.sns.client;

import com.myrealpet.sns.dto.account.AccountInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class AccountApiClient {

    private final RestTemplate restTemplate;

    public AccountApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public AccountInfoDto getAccount(Long accountId) {
        log.info("getAccount({})", accountId);
        String url = "http://localhost:8005/api/auth/" + accountId;
        log.info("url : {}", url);
        return restTemplate.getForObject(url, AccountInfoDto.class);
    }
}
