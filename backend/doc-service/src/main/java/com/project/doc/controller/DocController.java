package com.project.doc.controller;

import com.project.doc.dto.response.DocumentListDto;
import com.project.doc.dto.response.UploadResponse;
import com.project.doc.service.DocService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/doc")
@RequiredArgsConstructor
public class DocController {

    private final DocService docService;

    @GetMapping("/list")
    public Page<DocumentListDto> list(Pageable pageable) {
        return docService.list(pageable);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> upload(
            @RequestHeader(value = "X-User-Id", required = false) String uid,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title
    ) {

        UploadResponse result = docService.upload(file, title, uid);

        return ResponseEntity.ok(result);
    }
}