package com.project.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiService(AiEventProducer aiEventProducer, AiVectorRepository aiVectorRepository) {
        this.aiEventProducer = aiEventProducer;
        this.aiVectorRepository = aiVectorRepository;
    }

    private static final String RESULT_TOPIC = "doc-topic";

    @Transactional
    public void processDocument(AiRequestDto dto) {

        Long docId = dto.getId();
        String filePath = dto.getFilePath();

        try {

            String content = readFile(filePath);

            String response = summarize(content);

            JsonNode node = objectMapper.readTree(response);

            String summary = node.get("summary").asText();
            String category = node.get("category").asText();

            List<String> chunks = chunkText(content, 500);

            List<List<Float>> embeddings = createEmbeddings(chunks);
            saveVectorDB(docId, chunks, embeddings);

            AiResultDto result = new AiResultDto(
                    docId,
                    content,
                    summary,
                    category,
                    "COMPLETE"
            );

            aiEventProducer.send(
                    RESULT_TOPIC,
                    docId.toString(),
                    result
            );

        } catch (Exception e) {

            e.printStackTrace();

            AiResultDto result = new AiResultDto(
                    docId,
                    null,
                    null,
                    null,
                    "ERROR"
            );

            aiEventProducer.send(
                    RESULT_TOPIC,
                    docId.toString(),
                    result
            );
        }
    }

    private String readFile(String filePath) {
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String summarize(String content) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://localhost:11434/api/generate";

        String prompt = """
        당신은 문서 분석 AI입니다.

        입력된 텍스트를 분석하여 아래 규칙에 따라 응답하세요.

        규칙:
        1. 먼저 문서 유형을 판단하세요.
          - 노래 가사
          - 국가(國歌)
          - 기사
          - 기술 문서
          - 소설
          - 시
          - 계약서
          - 일반 설명문
          등을 구분하세요.

       2. 입력 내용이 유명한 노래, 국가, 시, 소설,
          문학 작품, 역사적으로 알려진 문서라면
          단순 요약하지 말고 아래 정보를 자연스럽게 설명하세요.
          - 작품명 또는 제목
          - 작가 / 작사 / 작곡가
          - 작품 특징 또는 시대적 배경
          - 어떤 작품인지에 대한 설명

       3. 특히 노래나 국가(國歌)라면
          아래 정보를 우선적으로 포함하세요.
          - 제목
          - 가수 또는 국가명
          - 작사/작곡가
          - 어떤 노래인지 설명

       4. 입력 내용이 일반 문서라면
          핵심만 2~3문장으로 자연스럽게 요약하세요.

       5. 입력 내용이 일부만 주어졌더라도
          유명 작품으로 판단 가능하면
          작품 정보를 설명하세요.

       6. 불필요한 목록, 제목, Markdown,
          특수기호 사용 없이 자연스러운 문장 형태로만 작성하세요.

       7. 반드시 한국어로만 응답하세요.
    
       응답 예시:
       {
            "summary": "...",
            "category": "TECH"
       }
                 
        입력 내용:
        %s
        """.formatted(content);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "llama3");
        request.put("prompt", prompt);
        request.put("stream", false);

        Map response = restTemplate.postForObject(url, request, Map.class);

        return (String) response.get("response");
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
