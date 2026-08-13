package com.satyam.SearchEngine.model.Repo;

import com.satyam.SearchEngine.model.DocFrequency;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocFrequencyRepo extends JpaRepository<DocFrequency, String> {

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO doc_frequency (term, df)
        VALUES (:term, 1)
        ON CONFLICT (term)
        DO UPDATE SET df = doc_frequency.df + 1
        """, nativeQuery = true)
    void incrementDf(@Param("term") String term);
}