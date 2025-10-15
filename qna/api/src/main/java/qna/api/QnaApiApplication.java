package qna.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "qna.api",
        "qna.core",
        "qna.client",
        "com.myrealpet.common"
})
@EntityScan(basePackages = "qna.dto")
@EnableJpaRepositories(basePackages = "qna.core")
public class QnaApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(QnaApiApplication.class, args);
    }
}
