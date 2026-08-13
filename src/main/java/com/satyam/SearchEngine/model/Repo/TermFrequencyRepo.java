package com.satyam.SearchEngine.model.Repo;

import com.satyam.SearchEngine.model.TermFrequency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TermFrequencyRepo extends JpaRepository<TermFrequency,Integer> {
}
