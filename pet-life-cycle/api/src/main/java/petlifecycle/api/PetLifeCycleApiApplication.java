package petlifecycle.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"petlifecycle.api",
		"petlifecycle.core",
		"petlifecycle.client",
		"com.myrealpet.common"
})
@EntityScan(basePackages = "petlifecycle.dto")
@EnableJpaRepositories(basePackages = "petlifecycle.core")
public class PetLifeCycleApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetLifeCycleApiApplication.class, args);
	}

}
