package com.satyam.SearchEngine;

import com.satyam.SearchEngine.crawler.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


@SpringBootApplication
public class SearchEngineApplication {


	public static void main(String[] args) {
        SpringApplication.run(SearchEngineApplication.class, args);


    }



}
