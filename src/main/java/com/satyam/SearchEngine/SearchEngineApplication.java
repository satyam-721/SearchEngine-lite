package com.satyam.SearchEngine;

import com.satyam.SearchEngine.crawler.CrawlerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SearchEngineApplication {

	public static void main(String[] args) {
		SpringApplication.run(SearchEngineApplication.class, args);
        CrawlerService cs = new CrawlerService();
        cs.startCrawling();
	}

}
