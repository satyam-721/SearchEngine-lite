package com.satyam.SearchEngine.codesearch.indexer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChunkRepository extends JpaRepository<ChunkEntity, Long> {

    // Full rebuild: wipes all three tables (FK-safe because they're truncated together)
    @Modifying
    @Query(value = "TRUNCATE code_index_entries, chunk_field_lengths, code_chunks", nativeQuery = true)
    void truncateAll();

    @Query(value = """
        SELECT field AS "field", CAST(AVG(length) AS double precision) AS "avgLength"
        FROM chunk_field_lengths GROUP BY field
        """, nativeQuery = true)
    List<FieldLengthStat> averageFieldLengths();
}