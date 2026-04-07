package com.project.log.kafka;

import com.project.log.dto.event.LogEventDto;
import com.project.log.entity.LogEntity;
import com.project.log.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final LogRepository logRepository;

    @KafkaListener(topics = "log-topic", groupId ="log-group")
    public void consume(LogEventDto dto) throws Exception {

        LogEntity entity = new LogEntity();
        entity.setServiceName(dto.getServiceName());
        entity.setEventType(dto.getEventType());
        entity.setUserId(dto.getUserId());
        entity.setMessage(dto.getMessage());

        logRepository.save(entity);
    }
}