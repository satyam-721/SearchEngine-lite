package xyz.satyam.crawler.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import xyz.satyam.crawler.services.CrawlStatus;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PageRepo extends JpaRepository<Page,Long> {

    Optional<Page> findByUrl(String url);

    @Query("""
    SELECT p
    FROM Page p
    WHERE p.status = :status
""")
    List<Page> findPageByStatus(CrawlStatus status, Pageable pageable);


    org.springframework.data.domain.Page<PageContent> findByStatus(CrawlStatus crawlStatus, Pageable pageable);

    Stream<Page> streamFindByStatus(CrawlStatus crawlStatus);


    List<PageContent> findByStatus(CrawlStatus crawlStatus);

    int countByStatus(CrawlStatus crawlStatus);


    @Query("""
    SELECT id, url, title, snippet
    FROM Page p
    WHERE p.id = :id
""")
    Optional<PageContentDto> findPageDetailsById(long id);
}
