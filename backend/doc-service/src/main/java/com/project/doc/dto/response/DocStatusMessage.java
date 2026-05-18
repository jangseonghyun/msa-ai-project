package com.project.doc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocStatusMessage {

    private Long docId;
    private String status;
    private String category;
}