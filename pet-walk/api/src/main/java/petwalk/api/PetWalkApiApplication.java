package petwalk.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// JPA 관련 import는 엔티티 추가 시 활성화
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
    "petwalk.api",
    "petwalk.core",
    "petwalk.client",
    "com.myrealpet.common"
})
// JPA 설정은 엔티티 추가 시 활성화
@EntityScan("petwalk.core")
@EnableJpaRepositories("petwalk.core")
@EnableJpaAuditing
public class PetWalkApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetWalkApiApplication.class, args);
    }
}