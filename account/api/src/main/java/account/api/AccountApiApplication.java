package account.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "account.api",
    "account.core",
    "account.client",
    "com.myrealpet.common"
})
@EntityScan("account.core")
@EnableJpaRepositories("account.core")
@EnableJpaAuditing
public class AccountApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountApiApplication.class, args);
    }
}