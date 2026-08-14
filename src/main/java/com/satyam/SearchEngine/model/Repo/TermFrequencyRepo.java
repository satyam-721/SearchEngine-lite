package com.satyam.SearchEngine.model.Repo;

import com.satyam.SearchEngine.model.TermFrequency;
import com.satyam.SearchEngine.model.TermFrequencyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TermFrequencyRepo extends JpaRepository<TermFrequency,Integer> {

    @Query("""
        SELECT t.term AS term, t.pageId AS pageId, t.tf AS tf
        FROM TermFrequency t
        WHERE t.term IN :terms
    """)
    List<TermFrequencyProjection> findByTerms(
            @Param("terms") List<String> terms
    );
}
