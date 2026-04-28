package com.project.ai.repository;

import com.project.ai.entity.AiVector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AiVectorRepository extends JpaRepository<AiVector, Long> {

    @Modifying
    @Query(value = """
    INSERT INTO ai_vector (source_type, source_id, chunk, embedding)
    VALUES (:sourceType, :sourceId, :chunk, CAST(:embedding AS vector))
""", nativeQuery = true)
    void insertVector(
            @Param("sourceType") String sourceType,
            @Param("sourceId") Long sourceId,
            @Param("chunk") String chunk,
            @Param("embedding") String embedding
    );

}
