package petlifecycle.client.pet.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UpdatePetAccountResponse {
    private final String message;

    public static UpdatePetAccountResponse from(String petName) {
        String message = "\"" + petName + "\"" + " 정보가 성공적으로 수정되었습니다.";
        return UpdatePetAccountResponse.builder()
                .message(message)
                .build();
    }
}
