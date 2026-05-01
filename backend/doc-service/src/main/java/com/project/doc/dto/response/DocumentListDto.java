package com.project.doc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DocumentListDto {

    private Long id;
    private String title;
    private String fileName;
    private Long fileSize;
    private String status;
    private String summary;
    private LocalDateTime createdAt;
}