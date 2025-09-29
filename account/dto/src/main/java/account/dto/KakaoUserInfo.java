package account.dto;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class KakaoUserInfo {
    private Long id;
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class Properties {
        private String nickname;
        @JsonProperty("profile_image")
        private String profileImage;
        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Data
    public static class KakaoAccount {
        @JsonProperty("profile_needs_agreement")
        private Boolean profileNeedsAgreement;
        private Profile profile;
        @JsonProperty("email_needs_agreement")
        private Boolean emailNeedsAgreement;
        private String email;
        @JsonProperty("gender_needs_agreement")
        private Boolean genderNeedsAgreement;
        private String gender;
        @JsonProperty("age_range_needs_agreement")
        private Boolean ageRangeNeedsAgreement;
        @JsonProperty("age_range")
        private String ageRange;
    }

    @Data
    public static class Profile {
        private String nickname;
        @JsonProperty("thumbnail_image_url")
        private String thumbnailImageUrl;
        @JsonProperty("profile_image_url")
        private String profileImageUrl;
        @JsonProperty("is_default_image")
        private Boolean isDefaultImage;
    }
}
