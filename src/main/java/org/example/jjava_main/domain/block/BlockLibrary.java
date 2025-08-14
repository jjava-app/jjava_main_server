package org.example.jjava_main.domain.block;


import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "block_library_tb")
public class BlockLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer userId;
    private String libraryJson;

    @Builder
    public BlockLibrary(Integer id, Integer userId, String libraryJson) {
        this.id = id;
        this.userId = userId;
        this.libraryJson = libraryJson;
    }

    public void update(String libraryJson) {
        this.libraryJson = libraryJson == null ? this.libraryJson : libraryJson;
    }
}
