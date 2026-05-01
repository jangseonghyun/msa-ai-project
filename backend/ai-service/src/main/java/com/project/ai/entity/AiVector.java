package com.project.ai.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_vector")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiVector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "chunk_index")
    private Long chunkIndex;

    @Column(columnDefinition = "TEXT")
    private String chunk;

    @Column(columnDefinition = "vector(768)")
    private String embedding;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
