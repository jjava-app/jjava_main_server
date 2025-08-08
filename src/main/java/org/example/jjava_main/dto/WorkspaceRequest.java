package org.example.jjava_main.dto;

import lombok.Data;

public class WorkspaceRequest {
    @Data
    public static class UpdateDTO {
        public String title;
        public String serializedJson;
        public String blockExtensionJson;
    }
}
