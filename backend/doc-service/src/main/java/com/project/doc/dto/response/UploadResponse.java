package com.project.doc.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponse {

    private boolean success;
    private String message;
    private Long docId;
}