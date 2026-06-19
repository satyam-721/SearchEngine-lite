package com.satyam.SearchEngine.crawler;

import com.satyam.SearchEngine.indexer.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api")
public class Controller {

    @Autowired
    CrawlerService service;
    
    @Autowired
    IndexerService indexerService;

    @Async
    @GetMapping("crawl/start")
    public String startCrawling(){
        service.startCrawling();
        return "Started Crawling";
    }
    
    @GetMapping("indexer/start")
    public HashMap<String, Integer> startIndexer(){
        return indexerService.startAnalyzer();

    }

}
