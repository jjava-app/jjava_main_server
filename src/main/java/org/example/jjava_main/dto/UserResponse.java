package org.example.jjava_main.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.example.jjava_main.domain.user.User;
import org.example.jjava_main.domain.user.UserLevel;
import org.example.jjava_main.domain.user.UserRole;

import java.util.List;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String username;
    private String level;
    private Integer score;
    private Integer rank;

    public UserResponse(User user) {   // 기존 테스트 호환
        this(user, null);
    }

    public UserResponse(User user, Integer rank) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.level = user.getLevel().toString();
        this.score = user.getScore();
        this.rank = rank;
    }

    @Data
    public static class LevelUpdateResponse {
        private Integer id;
        private String email;
        private String username;
        private String level;

        public LevelUpdateResponse(User u) {
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

        public JoinDTO(User user, String accessToken) {
            this.accessToken = accessToken;
            this.email = user.getEmail();
            this.nickname = user.getUsername();
            this.level = user.getLevel();
            this.role = user.getRole();
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
}