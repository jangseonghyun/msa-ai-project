package com.project.log.kafka;

import com.project.log.dto.event.LogEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class LogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendSignupLog(String userId) {
        try {
            LogEventDto dto = new LogEventDto();
            dto.setServiceName("auth-service");
            dto.setEventType("SIGNUP");
            dto.setUserId(userId);
            dto.setMessage(userId + " 회원가입 성공");

            String json = objectMapper.writeValueAsString(dto);

            kafkaTemplate.send("log-topic", json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}