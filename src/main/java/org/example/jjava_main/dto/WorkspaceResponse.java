package org.example.jjava_main.dto;

import lombok.Data;
import org.example.jjava_main.domain.workspace.Workspace;

public class WorkspaceResponse {

    @Data
    public static class DTO {
        public Integer id;
        public Integer userId;
        public String title;
        public String serializedJson;
        public String blockExtensionJson;

        public DTO(Workspace workspace) {
            this.id = workspace.getId();
            this.userId = workspace.getUserId();
            this.title = workspace.getTitle();
            this.serializedJson = workspace.getSerializedJson();
            this.blockExtensionJson = workspace.getBlockExtensionJson();
        }
    }

    @Data
    public static class CreateDTO {
        public Integer id;
        public Integer userId;
        public String title;
        public String createdAt;

        public CreateDTO(Workspace workspace) {
            this.id = workspace.getId();
            this.userId = workspace.getUserId();
            this.title = workspace.getTitle();
            this.createdAt = workspace.getCreatedAt().toString();
        }
    }

}
