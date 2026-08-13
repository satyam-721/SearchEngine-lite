package com.satyam.SearchEngine.model.Repo;

import com.satyam.SearchEngine.model.IndexEntry;
import com.satyam.SearchEngine.model.dto.IndexResult;
import com.satyam.SearchEngine.query.QueryResult;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexEntryRepo extends JpaRepository<IndexEntry,Integer> {
    @Query("""
        SELECT e.pageId as pageId, SUM(e.score) as totalScore
        FROM IndexEntry e
        WHERE e.word IN :terms
        GROUP BY e.pageId
        ORDER BY SUM(e.score) DESC
    """)
    List<IndexResult> findTopPagesByTerms(@Param("terms") List<String> terms, Pageable pageable);
}
