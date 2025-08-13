package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "provider_tb")
public class Provider {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private ProviderType providerType;

    @Builder
    public Provider(Integer id, ProviderType providerType) {
        this.id = id;
        this.providerType = providerType;
    }
}
