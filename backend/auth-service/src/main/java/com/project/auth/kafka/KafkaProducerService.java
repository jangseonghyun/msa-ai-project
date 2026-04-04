package com.project.auth.kafka;

import com.project.auth.dto.event.AuthEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, AuthEvent> kafkaTemplate;

    private static final String TOPIC = "log-topic";

    public void send(AuthEvent event) {
        kafkaTemplate.send(TOPIC, event);
    }
}
