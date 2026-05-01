package com.project.doc.repository;

import com.project.doc.dto.response.DocumentListDto;
import com.project.doc.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface DocRepository extends JpaRepository<Document, Long> {

    @Query("""
        SELECT new com.project.doc.dto.response.DocumentListDto(
            d.id,
            d.title,
            d.fileName,
            d.fileSize,
            d.status,
            d.summary,
            d.createdAt
        )
        FROM Document d
        ORDER BY d.createdAt DESC
    """)
    Page<DocumentListDto> findList(Pageable pageable);
}