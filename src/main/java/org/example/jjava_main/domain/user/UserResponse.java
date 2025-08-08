package org.example.jjava_main.domain.user;

import lombok.Data;

@Data
public class UserResponse {
    private Integer id;
    private String email;
    private String level;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.level = user.getLevel().toString();
    }
}