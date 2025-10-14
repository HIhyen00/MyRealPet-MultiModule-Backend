package petlifecycle.client.pet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterPetAccountResponse {
    private final String message;

    public static RegisterPetAccountResponse from(String petName) {
        String message = "\"" + petName + "\"" + "이(가) 성공적으로 등록되었습니다!";
        return RegisterPetAccountResponse.builder()
                .message(message)
                .build();
    }
}
