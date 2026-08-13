package com.satyam.SearchEngine.controller;

import com.satyam.SearchEngine.crawler.CrawlerService;
import com.satyam.SearchEngine.crawler.ThreadServices;
import com.satyam.SearchEngine.indexer.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/crawl")
public class CrawlController {

    @Autowired
    ThreadServices threadServices;

    @Async
    @GetMapping("/start")
    public String startCrawling(){
        threadServices.createCrawlThreads();
        return "Started Crawling";
    }
}
