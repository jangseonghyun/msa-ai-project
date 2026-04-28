package com.project.doc.kafka;

import java.util.Map;
import com.project.doc.entity.Document;
import com.project.doc.repository.DocRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocConsumer {

    private final DocRepository docRepository;

    @KafkaListener(topics = "doc-topic", groupId="doc-group")
    public void consume(Map<String, Object> data) {

        Long docId = Long.valueOf(data.get("docId").toString());
        String content = (String) data.get("content");
        String summary = (String) data.get("summary");

        Document entity = docRepository.findById(docId)
                .orElse(new Document());

        entity.setId(docId);
        entity.setContent(content);
        entity.setSummary(summary);

        docRepository.save(entity);
    }

}
