package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.*;
import lombok.*;
import org.example.jjava_main.domain.user.User;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_account_provider_tb")
public class UserAccountProvider {
    @GeneratedValue
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Provider provider;

    @Builder
    public UserAccountProvider(Integer id, User user, Provider provider) {
        this.id = id;
        this.user = user;
        this.provider = provider;
    }
}
