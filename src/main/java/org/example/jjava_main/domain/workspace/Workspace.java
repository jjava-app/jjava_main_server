package org.example.jjava_main.domain.workspace;

import jakarta.persistence.*;
import lombok.*;
import org.example.jjava_main.domain.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Entity
@Table(name = "workspace_tb")
public class Workspace {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String serializedJson;
    @Column(columnDefinition = "TEXT")
    private String blockExtensionJson;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Workspace(Integer id, User user, String title, String serializedJson, String blockExtensionJson, Timestamp createdAt) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.serializedJson = serializedJson;
        this.blockExtensionJson = blockExtensionJson;
        this.createdAt = createdAt;
    }

    public void update(String title, String serializedJson) {
        this.title = title == null ? this.title : title;
        this.serializedJson = serializedJson == null ? this.serializedJson : serializedJson;
    }
}
