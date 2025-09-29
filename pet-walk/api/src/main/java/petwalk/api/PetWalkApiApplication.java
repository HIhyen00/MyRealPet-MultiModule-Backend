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
        // Spring 시작 전에 .env 파일 로드
        loadDotEnv();

        System.out.println("PetWalk Current working directory: " + System.getProperty("user.dir"));
        System.out.println("PetWalk PETWALK_DB_URL from System.getProperty(): " + System.getProperty("PETWALK_DB_URL"));
        System.out.println("PetWalk KAKAO_API_KEY from System.getProperty(): " + System.getProperty("KAKAO_API_KEY"));

        SpringApplication.run(PetWalkApiApplication.class, args);
    }

    private static void loadDotEnv() {
        java.io.File currentDir = new java.io.File(System.getProperty("user.dir"));

        // 최대 3단계까지 상위로 올라가면서 .env 파일 찾기
        for (int i = 0; i < 3; i++) {
            java.io.File envFile = new java.io.File(currentDir, ".env");
            if (envFile.exists()) {
                System.out.println("Found .env file at: " + currentDir.getAbsolutePath());
                loadEnvFile(envFile);
                return;
            }
            currentDir = currentDir.getParentFile();
            if (currentDir == null) break;
        }

        System.out.println("No .env file found");
    }

    private static void loadEnvFile(java.io.File envFile) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(envFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                int equalIndex = line.indexOf('=');
                if (equalIndex > 0) {
                    String key = line.substring(0, equalIndex).trim();
                    String value = line.substring(equalIndex + 1).trim();
                    System.setProperty(key, value);
                    System.out.println("Loaded: " + key + "=" + value);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load .env file: " + e.getMessage());
        }
    }
}