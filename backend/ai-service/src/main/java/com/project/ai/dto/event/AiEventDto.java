package com.project.ai.dto.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiEventDto {
    private String serviceName;
    private String eventType;
    private String message;
    private String userId;
}