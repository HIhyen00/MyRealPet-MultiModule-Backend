package petlifecycle.adminapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "petlifecycle.adminapi",
        "petlifecycle.core",
        "petlifecycle.client",
        "com.myrealpet.common"
})
@EntityScan(basePackages = "petlifecycle.dto")
@EnableJpaRepositories(basePackages = "petlifecycle.core")
@EnableJpaAuditing
public class PetLifeCycleAdminApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetLifeCycleAdminApiApplication.class, args);
    }

}
