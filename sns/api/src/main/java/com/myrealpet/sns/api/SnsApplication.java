package com.myrealpet.sns.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "com.myrealpet.sns.api",
    "com.myrealpet.sns.core",
    "com.myrealpet.sns.client",
    "com.myrealpet.common"
})
@EntityScan(basePackages = "com.myrealpet.sns.core")
@EnableJpaRepositories(basePackages = "com.myrealpet.sns.core")
public class SnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }
}
