package xyz.satyam.crawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.satyam.crawler.services.ThreadServices;

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
