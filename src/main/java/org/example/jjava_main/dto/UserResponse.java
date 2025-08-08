package org.example.jjava_main.dto;


import lombok.Data;
import org.example.jjava_main.domain.user.User;

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
}