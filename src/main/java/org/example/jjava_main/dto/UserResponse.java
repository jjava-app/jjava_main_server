package org.example.jjava_main.dto;


import lombok.Data;
import org.example.jjava_main.domain.user.User;

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
        private List<User> userList;
        private int page;
        private String order;
        private int totalCount;
        private int sort;

        public ListDTO(List<User> userList, int page, String order, int totalCount, int sort) {
            this.userList = userList;
            this.page = page;
            this.order = order;
            this.totalCount = totalCount;
            this.sort = sort;
        }
    }
}