package com.project.doc.dto.event;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiResultDto {

    private Long docId;
    private String content;
    private String summary;
}
