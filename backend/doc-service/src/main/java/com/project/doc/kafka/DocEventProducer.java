package com.project.doc.kafka;

import com.project.doc.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocEventProducer {

    private final KafkaTemplate<String, Document> kafkaTemplate;

    private static final String TOPIC = "ai-topic";

    public void send(Document doc) {
        kafkaTemplate.send(TOPIC, doc.getId().toString(), doc);
    }
}