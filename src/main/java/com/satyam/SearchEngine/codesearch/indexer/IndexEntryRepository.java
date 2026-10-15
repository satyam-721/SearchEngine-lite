package com.satyam.SearchEngine.codesearch.indexer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IndexEntryRepository extends JpaRepository<IndexEntryCode, Long> {

    @Query(value = """
        SELECT e.chunk_id AS "chunkId", e.field AS "field", e.term AS "term",
               e.tf AS "tf", l.length AS "fieldLength"
        FROM code_index_entries e
        JOIN chunk_field_lengths l ON l.chunk_id = e.chunk_id AND l.field = e.field
        WHERE e.term IN (:terms)
        """, nativeQuery = true)
    List<PostingRow> findPostings(@Param("terms") Collection<String> terms);
}