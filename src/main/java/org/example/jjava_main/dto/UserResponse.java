package org.example.jjava_main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;

import java.util.Collections;
import java.util.List;

@Data
public class UserResponse {
    @Data
    public static class DTO {
        private Integer id;
        private String email;
        private String username;
        private String level;
        private Integer score;
        private Integer rank;

        private List<LinkedAccountDTO> linked = Collections.emptyList();


        public DTO(User user, Integer rank) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.level = user.getLevel() != null ? user.getLevel().name() : UserLevel.BEGINNER.name();
            this.score = user.getScore() != null ? user.getScore() : 0;
            this.rank = rank;
        }
    }

    @Data
    public static class LinkedAccountDTO {
        private final String provider; // 'naver'|'google'|'kakao' (원하면 'local'도)
        private final String email;    // null 방지 위해 빈 문자열로 넣어줄 것

    }

    @Data
    public static class UpdateDTO {
        private Integer id;
        private String email;
        private String username;
        private String level;

        public UpdateDTO(User u) {
            this.id = u.getId();
            this.email = u.getEmail();
            this.username = u.getUsername();
            this.level = u.getLevel().toString();
        }
    }

    @Data
    public static class ListDTO {
        private List<UserDTO> userList;
        private int page;
        private String order;
        private int totalCount;
        private int sort;

        public ListDTO(List<UserDTO> userList, int page, String order, int totalCount, int sort) {
            this.userList = userList;
            this.page = page;
            this.order = order;
            this.totalCount = totalCount;
            this.sort = sort;
        }
    }

    // 응답 전용 DTO (필요한 필드만)
    @Getter
    @AllArgsConstructor
    public static class UserDTO {
        private Integer id;
        private String email;
        private String username;
        private UserLevel level;
        private UserRole role;
        private Integer score;

        public static UserDTO from(User u) {
            return new UserDTO(
                    u.getId(),
                    u.getEmail(),
                    u.getUsername(),
                    u.getLevel(),
                    u.getRole(),
                    u.getScore()
            );
        }
    }

    @Data
    public static class CheckNicknameDTO {
        private Boolean available;

        public CheckNicknameDTO(Boolean available) {
            this.available = available;
        }
    }

    @Data
    public static class CheckEmailDTO {
        private Boolean verified;

        public CheckEmailDTO(Boolean verified) {
            this.verified = verified;
        }
    }

    @Data
    public static class JoinDTO {
        private String accessToken;
        private String email;
        private String nickname;
        private UserLevel level;
        private UserRole role;
        private Boolean isNewUser;

        public JoinDTO(User user, String accessToken) {
            this.accessToken = accessToken;
            this.email = user.getEmail();
            this.nickname = user.getUsername();
            this.level = user.getLevel();
            this.role = user.getRole();
            this.isNewUser = true;
        }
    }

    @Data
    public static class LoginDTO {
        private String accessToken;
        private Integer id;
        private String email;
        private String nickname;
        private UserLevel level;
        private UserRole role;
        private Integer score;

        public LoginDTO(User user, String accessToken) {
            this.accessToken = accessToken;
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getUsername();
            this.level = user.getLevel();
            this.role = user.getRole();
            this.score = user.getScore();
        }
    }

    @Data
    public static class UserUpdateDTO {
        private Integer id;
        private String username;
        private String email;
        private UserRole role;
        private Integer score;

        public UserUpdateDTO(User user) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.score = user.getScore();
        }
    }
}