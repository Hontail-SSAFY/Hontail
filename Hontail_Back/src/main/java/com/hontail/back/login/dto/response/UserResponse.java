//package com.hontail.back.login.dto.response;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Builder;
//import lombok.Getter;
//
//@Getter
//@Builder
//@Schema(description = "사용자 응답 DTO")
//public class UserResponse {
//    @Schema(description = "사용자 ID", example = "1")
//    private Integer id;
//
//    @Schema(description = "사용자 이메일", example = "user@example.com")
//    private String email;
//
//    @Schema(description = "사용자 닉네임", example = "홍길동")
//    private String nickname;
//
//    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
//    private String profileImage;
//
//}

package com.hontail.back.login.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 응답 DTO")
public class UserResponse {
    @JsonProperty("user_id")
    @Schema(description = "사용자 ID", example = "1")
    private Integer id;

    @JsonProperty("user_email")
    @Schema(description = "사용자 이메일", example = "user@example.com")
    private String email;

    @JsonProperty("user_nickname")
    @Schema(description = "사용자 닉네임", example = "홍길동")
    private String nickname;

    @JsonProperty("profile_image")
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;
}