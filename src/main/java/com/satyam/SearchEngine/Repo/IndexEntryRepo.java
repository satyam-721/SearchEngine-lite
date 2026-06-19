package com.satyam.SearchEngine.Repo;

import com.satyam.SearchEngine.model.IndexEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndexEntryRepo extends JpaRepository<IndexEntry,Integer> {

}
