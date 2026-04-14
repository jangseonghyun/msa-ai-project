package com.project.doc.dto.response;

public class DocResponse {

    public record UploadResponse(
            boolean success,
            String message,
            Long docId
    ) {}
}