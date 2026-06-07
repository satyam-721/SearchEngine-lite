package com.satyam.SearchEngine.Repo;

import com.satyam.SearchEngine.model.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepo extends JpaRepository<Page,Float> {

    Optional<Page> findByUrl(String url);

    List<Page> findByStatus(CrawlStatus crawlStatus);
}
