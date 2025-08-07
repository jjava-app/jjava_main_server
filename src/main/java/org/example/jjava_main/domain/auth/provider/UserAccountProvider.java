package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_account_provider_tb")
public class UserAccountProvider {
    @GeneratedValue
    @Id
    private Integer id;
    private Integer userId;
    private Integer providerId;

    @Builder
    public UserAccountProvider(Integer id, Integer userId, Integer providerId) {
        this.id = id;
        this.userId = userId;
        this.providerId = providerId;
    }
}
