package org.example.jjava_main.domain.block;


import jakarta.persistence.*;
import lombok.*;
import org.example.jjava_main.domain.user.User;

@Data
@NoArgsConstructor
@Entity
@Table(name = "block_library_tb")
public class BlockLibrary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String libraryJson;

    @Builder
    public BlockLibrary(Integer id, User user, String libraryJson) {
        this.id = id;
        this.user = user;
        this.libraryJson = libraryJson;
    }

    public void update(String libraryJson) {
        this.libraryJson = libraryJson == null ? this.libraryJson : libraryJson;
    }
}
