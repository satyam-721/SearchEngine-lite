package com.satyam.SearchEngine.Repo;

import com.satyam.SearchEngine.crawler.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PageRepo extends JpaRepository<Page,Float> {

    Optional<Page> findByUrl(String url);

    List<Page> findByStatus(CrawlStatus crawlStatus, Pageable pageable);

    Stream<Page> streamFindByStatus(CrawlStatus crawlStatus);

    List<Page> findByStatus(CrawlStatus crawlStatus);
}
