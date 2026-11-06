package xyz.satyam.indexer.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import xyz.satyam.indexer.dto.TermFrequencyProjection;
import xyz.satyam.indexer.model.TermFrequency;

import java.util.List;

@Repository
public interface TermFrequencyRepo extends JpaRepository<TermFrequency,Integer> {

    @Query("""
    SELECT new xyz.satyam.indexer.dto.TermFrequencyProjection(
        t.term, t.pageId, t.tf
    )
    FROM TermFrequency t
    WHERE t.term IN :terms
""")
    List<TermFrequencyProjection> findByTerms(
            @Param("terms") List<String> terms
    );
}
