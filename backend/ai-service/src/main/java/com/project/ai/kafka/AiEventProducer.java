package com.project.ai.kafka;

import com.project.ai.dto.event.AiResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiEventProducer {

    private final KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    public void send(String topic, String key, AiResultDto ai) {

        Map<String, Object> data = new HashMap<>();
        data.put("docId", ai.getDocId());
        data.put("content", ai.getContent());
        data.put("summary", ai.getSummary());

        kafkaTemplate.send(topic, key, data);
    }
}