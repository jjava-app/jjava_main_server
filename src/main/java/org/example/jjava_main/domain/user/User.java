package org.example.jjava_main.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

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
    private UserLevel level;
    private UserRole role;

    @Builder
    public User(Integer id, String email, String password, String username, UserLevel level, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.username = username;
        this.level = level;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        authorities.add(() -> "ROLE_" + role);

        return authorities;
    }
}
