package org.example.jjava_main.dto;


import lombok.*;
import org.example.jjava_main.domain.user.*;

import java.util.List;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String username;
    private String level;
    private Integer score;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.level = user.getLevel().toString();
        this.score = user.getScore();
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
    public static class UserUpdateDTO {
        private String username;
        private String email;
        private UserRole role;
        private Integer score;

        public UserUpdateDTO(User user) {
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.score = user.getScore();
        }
    }
}