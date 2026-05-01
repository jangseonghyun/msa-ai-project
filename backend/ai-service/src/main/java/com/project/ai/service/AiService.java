package com.project.ai.service;

import com.project.ai.dto.event.AiResultDto;
import com.project.ai.dto.request.AiRequestDto;
import com.project.ai.entity.AiVector;
import com.project.ai.kafka.AiEventProducer;
import com.project.ai.repository.AiVectorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.antlr.v4.runtime.misc.Utils.readFile;

@Service
public class AiService {

    private final AiVectorRepository aiVectorRepository;
    private final AiEventProducer aiEventProducer;

    public AiService(AiEventProducer aiEventProducer, AiVectorRepository aiVectorRepository) {
        this.aiEventProducer = aiEventProducer;
        this.aiVectorRepository = aiVectorRepository;
    }

    private static final String RESULT_TOPIC = "doc-topic";

    @Transactional
    public void processDocument(AiRequestDto dto) {

        Long docId = dto.getId();
        String filePath = dto.getFilePath();

        String content = readFile(filePath);
        String summary = summarize(content);
        List<String> chunks = chunkText(content, 500);
        List<List<Float>> embeddings = createEmbeddings(chunks);
        saveVectorDB(docId, chunks, embeddings);

        AiResultDto result = new AiResultDto(docId, content, summary);
        aiEventProducer.send(RESULT_TOPIC, docId.toString(), result);
    }

    private String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String summarize(String content) {
        if (content.length() <= 300) return content;
        return content.substring(0, 300);
    }

    private List<String> chunkText(String text, int size) {
        List<String> chunks = new ArrayList<>();
        int length = text.length();
        for (int i = 0; i < length; i += size) {
            chunks.add(text.substring(i, Math.min(length, i + size)));
        }
        return chunks;
    }

    private List<List<Float>> createEmbeddings(List<String> chunks) {
        List<List<Float>> result = new ArrayList<>();

        for (String chunk : chunks) {
            List<Float> vector = getEmbedding(chunk);

            if (vector.size() != 768) {
                throw new RuntimeException("embedding dimension 오류: " + vector.size());
            }

            result.add(vector);
        }

        return result;
    }

    private List<Float> getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/embeddings";

        Map<String, Object> request = new HashMap<>();
        request.put("model", "nomic-embed-text");
        request.put("prompt", text);

        Map response = restTemplate.postForObject(url, request, Map.class);

        List<Double> embedding = (List<Double>) response.get("embedding");

        List<Float> result = new ArrayList<>();
        for (Double d : embedding) {
            result.add(d.floatValue());
        }

        return result;
    }

    private void saveVectorDB(Long docId, List<String> chunks, List<List<Float>> embeddings) {
        for (int i = 0; i < chunks.size(); i++) {

            String chunk = chunks.get(i);
            List<Float> vector = embeddings.get(i);

            float[] arr = new float[vector.size()];
            for (int j = 0; j < vector.size(); j++) {
                arr[j] = vector.get(j);
            }

            String vectorStr = convertToVectorString(arr);

            aiVectorRepository.insertVector(
                    "doc-service",
                    docId,
                    (long) i,
                    chunk,
                    vectorStr
            );
        }
    }

    private String convertToVectorString(float[] arr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
