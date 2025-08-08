package org.example.jjava_main.domain.workspace;

import jakarta.persistence.*;
import lombok.*;
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
    private Integer userId;
    private String title;
    private String serializedJson;
    private String blockExtensionJson;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Workspace(Integer id, Integer userId, String title, String serializedJson, String blockExtensionJson, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.serializedJson = serializedJson;
        this.blockExtensionJson = blockExtensionJson;
        this.createdAt = createdAt;
    }

    public void update(String title, String serializedJson, String blockExtensionJson) {
        this.title = title == null ? this.title : title;
        this.serializedJson = serializedJson == null ? this.serializedJson : serializedJson;
        this.blockExtensionJson = blockExtensionJson == null ? this.blockExtensionJson : blockExtensionJson;
    }
}
