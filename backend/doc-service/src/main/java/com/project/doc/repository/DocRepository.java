package com.project.doc.repository;

import com.project.doc.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Document, Long> {
}