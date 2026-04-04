package com.project.log.dto.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogEventDto {
    private String serviceName;
    private String eventType;
    private String message;
    private String userId;
}