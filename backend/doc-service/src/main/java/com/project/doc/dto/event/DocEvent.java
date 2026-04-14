package com.project.doc.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DocEvent {

    private Long docId;
    private String filePath;
    private String fileName;
    private Long fileSize;
    private String status;
    private Long createdBy;
}