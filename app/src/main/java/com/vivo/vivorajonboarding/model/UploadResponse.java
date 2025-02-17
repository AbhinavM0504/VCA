package com.vivo.vivorajonboarding.model;

import java.util.ArrayList;
import java.util.List;

public class UploadResponse {
    private boolean success;
    private String message;
    private List<String> errors;
    private int uploadedFileCount;

    public static class ResponseData {
        private List<String> uploadedFiles;
        private List<String> errors;

        public List<String> getUploadedFiles() {
            return uploadedFiles != null ? uploadedFiles : new ArrayList<>();
        }

        public void setUploadedFiles(List<String> uploadedFiles) {
            this.uploadedFiles = uploadedFiles;
        }

        public List<String> getErrors() {
            return errors != null ? errors : new ArrayList<>();
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors != null ? errors : new ArrayList<>();
    }

    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    public String getFirstError() {
        return hasErrors() ? errors.get(0) : message;
    }

    public int getUploadedFileCount() {
        return uploadedFileCount;
    }

    // toString method for logging
    @Override
    public String toString() {
        return "UploadResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", errors=" + errors +
                ", uploadedFileCount=" + uploadedFileCount +
                '}';
    }
}
