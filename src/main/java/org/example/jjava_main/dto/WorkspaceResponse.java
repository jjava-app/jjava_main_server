package org.example.jjava_main.dto;

import lombok.Data;
import org.example.jjava_main.domain.block.BlockLibrary;
import org.example.jjava_main.domain.workspace.Workspace;
import org.hibernate.jdbc.Work;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceResponse {
    @Data
    public static class ListDTO {
        private List<WorkspaceDTO> workspaceList;

        @Data
        public static class WorkspaceDTO {
            private Integer id;
            private Integer userId;
            private String title;
            private String createdAt;

            public WorkspaceDTO(Workspace workspace) {
                this.id = workspace.getId();
                this.userId = workspace.getUser().getId();
                this.title = workspace.getTitle();
                this.createdAt = workspace.getCreatedAt().toString();
            }
        }

        public ListDTO(List<Workspace> workspaceList) {
            this.workspaceList = workspaceList.stream().map(WorkspaceDTO::new).collect(Collectors.toList());
        }
    }

    @Data
    public static class DTO {
        private Integer id;
        private Integer userId;
        private String title;
        private String serializedJson;
        private String libraryJson;

        public DTO(Workspace workspace, BlockLibrary blockLibrary) {
            this.id = workspace.getId();
            this.userId = workspace.getUser().getId();
            this.title = workspace.getTitle();
            this.serializedJson = workspace.getSerializedJson();
            this.libraryJson = blockLibrary.getLibraryJson();
        }
    }

    @Data
    public static class CreateDTO {
        private Integer id;
        private Integer userId;
        private String title;
        private String createdAt;

        public CreateDTO(Workspace workspace) {
            this.id = workspace.getId();
            this.userId = workspace.getUser().getId();
            this.title = workspace.getTitle();
            this.createdAt = workspace.getCreatedAt().toString();
        }
    }

}
