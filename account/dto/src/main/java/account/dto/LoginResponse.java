package account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private Long userId;
    private String username;
    private Duration expiresIn;

    public static LoginResponse of(String accessToken, Long userId, String username, Duration expiresIn) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .userId(userId)
                .username(username)
                .expiresIn(expiresIn)
                .build();
    }
}