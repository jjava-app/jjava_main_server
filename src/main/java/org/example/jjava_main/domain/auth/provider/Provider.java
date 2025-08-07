package org.example.jjava_main.domain.auth.provider;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "provider_tb")
public class Provider {

    @GeneratedValue
    @Id
    private Integer id;
    private String name;

    @Builder
    public Provider(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
