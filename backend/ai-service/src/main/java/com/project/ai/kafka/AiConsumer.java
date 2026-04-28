package com.project.ai.kafka;

import com.project.ai.dto.request.AiRequestDto;
import com.project.ai.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiConsumer {

    private final AiService aiService;

    @KafkaListener(topics = "ai-topic", groupId="ai-group")
    public void consume(AiRequestDto dto) throws Exception {

        try {
            aiService.processDocument(dto);
        } catch (Exception e) {
            log.error("처리 실패", e);
        }

    }
}
