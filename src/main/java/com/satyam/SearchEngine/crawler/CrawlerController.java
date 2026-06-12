package com.satyam.SearchEngine.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crawl")
public class CrawlerController {

    @Autowired
    CrawlerService service;

    @Async
    @GetMapping("start")
    public String startCrawling(){
        service.startCrawling();
        return "Started Crawling";
    }

}
