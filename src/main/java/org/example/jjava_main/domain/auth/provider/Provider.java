package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "provider_tb")
public class Provider {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ProviderType type;

    @Builder
    public Provider(Integer id, ProviderType type) {
        this.id = id;
        this.type = type;
    }
}
