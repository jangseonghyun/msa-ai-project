package com.project.doc.service;

import com.project.doc.dto.response.UploadResponse;
import com.project.doc.entity.Document;
import com.project.doc.kafka.KafkaProducerService;
import com.project.doc.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final KafkaProducerService kafkaProducerService;

    public UploadResponse upload(MultipartFile file, String title, Long userId) {

        // 1. 인증 체크
        if (userId == null) {
            return new UploadResponse(false, "인증 정보가 없습니다.", null);
        }

        // 2. 파일 체크
        if (file == null || file.isEmpty()) {
            return new UploadResponse(false, "파일이 없습니다.", null);
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null) {
            return new UploadResponse(false, "파일명이 없습니다.", null);
        }

        // 3. 확장자 체크
        String ext = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();

        if (!ext.equals("pdf") && !ext.equals("docx") && !ext.equals("txt")) {
            return new UploadResponse(false, "허용되지 않은 파일 형식입니다.", null);
        }

        String savedName = UUID.randomUUID() + "_" + originalName;
        String filePath = uploadDir + "/" + savedName;

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            file.transferTo(new File(filePath));
        } catch (Exception e) {
            return new UploadResponse(false, "파일 저장 실패", null);
        }

        // 5. DB 저장
        Document doc = Document.builder()
                .title(title)
                .fileName(originalName)
                .filePath(filePath)
                .fileSize(file.getSize())
                .status("PARSED")
                .createdBy(userId)
                .createdAt(LocalDateTime.now())
                .build();

        documentRepository.save(doc);

        // 6. Kafka
        kafkaProducerService.send(doc);

        return new UploadResponse(true, "업로드 성공", doc.getId());
    }
}