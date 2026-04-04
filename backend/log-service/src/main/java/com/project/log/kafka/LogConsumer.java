package com.project.log.kafka;

import com.project.log.dto.event.LogEventDto;
import com.project.log.entity.LogEntity;
import com.project.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final ObjectMapper objectMapper;
    private final LogRepository logRepository;

    @KafkaListener(topics = "log-topic")
    public void consume(String message) throws Exception {

        LogEventDto dto = objectMapper.readValue(message, LogEventDto.class);

        LogEntity entity = new LogEntity();
        entity.setServiceName(dto.getServiceName());
        entity.setEventType(dto.getEventType());
        entity.setUserId(dto.getUserId());
        entity.setMessage(dto.getMessage());

        logRepository.save(entity);

        System.out.println("로그 저장 완료: " + dto.getMessage());
    }
}