package org.example.jjava_main.domain.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "user_tb")
public class User implements UserDetails {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private String email;
    private String password;
    private String username;

    @Enumerated(EnumType.STRING)
    private UserLevel level;

    @Enumerated(EnumType.STRING)
    private UserRole role;
    private Integer score;

    @Builder
    public User(Integer id, String email, String password, String username, UserLevel level, UserRole role, Integer score) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.level = level;
        this.role = role;
        this.score = score;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(() -> "ROLE_" + role);

        return authorities;
    }

    public void userUpdate(UserLevel level, String username) {
        this.level = level;
        this.username = username;
    }


    // score update 함수
    public void scoreUpdate(Integer newScore) {
        this.score = newScore;
    }
}
