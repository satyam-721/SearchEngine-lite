package com.satyam.SearchEngine.model;

import com.satyam.SearchEngine.crawler.CrawlStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "wikipedia")
public class Page {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 2048)
    private String url;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String snippet;
    private LocalDateTime crawledAt;

    @Enumerated(EnumType.STRING)
    private CrawlStatus status;      //CRAWLED, FAILED, SKIPPED

    private int retryCount;
    private int httpStatusCode;
    private String failureReason;

    private LocalDate lastUpdated;


    public Page(String url, CrawlStatus crawlStatus) {
        this.url = url;
        this.status = crawlStatus;
    }
}
