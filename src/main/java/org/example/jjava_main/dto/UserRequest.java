package org.example.jjava_main.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;

public class UserRequest {

    @Data
    public static class JoinDTO {
        @Email(message = "유효한 이메일 형식을 입력해주세요.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
                message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
        private String password;

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$",
                message = "닉네임은 한글, 영문, 숫자만 가능합니다.")
        private String nickname;

        @NotNull(message = "레벨은 필수 입력값입니다.")
        private UserLevel level;

        public User toEntity(String encodedPassword) {
            return User.builder()
                    .email(email)
                    .password(encodedPassword)
                    .username(nickname)
                    .level(level)
                    .role(UserRole.USER)
                    .score(0)
                    .build();
        }
    }

    @Data
    public static class LoginDTO {
        @Email(message = "유효한 이메일 형식을 입력해주세요.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        private String email;
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;
    }
}
