package com.vivo.vivorajonboarding.model;


import java.util.Map;

public class DocumentUploadResponse {
    private boolean success;
    private String message;
    private String documentUrl;
    private DebugInfo debug;

    // Getter methods
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getDocumentUrl() { return documentUrl; }
    public DebugInfo getDebug() { return debug; }

    // Debug info inner class
    public static class DebugInfo {
        private Map<String, Object> files;
        private String request_method;
        private String content_type;

        // Getter methods
        public Map<String, Object> getFiles() { return files; }
        public String getRequestMethod() { return request_method; }
        public String getContentType() { return content_type; }
    }
}