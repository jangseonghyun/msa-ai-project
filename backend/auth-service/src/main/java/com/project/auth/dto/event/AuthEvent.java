package com.project.auth.dto.event;

public class AuthEvent {

    private String serviceName;
    private String eventType;
    private String userId;
    private String message;

    public AuthEvent() {}

    public AuthEvent(String serviceName, String eventType, String userId, String message) {
        this.serviceName = serviceName;
        this.eventType = eventType;
        this.userId = userId;
        this.message = message;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEventType() {
        return eventType;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
