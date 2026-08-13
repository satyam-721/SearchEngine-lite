package com.satyam.SearchEngine.controller;

import com.satyam.SearchEngine.indexer.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/indexer")
public class Indexer {

    @Autowired
    IndexerService indexerService;


    @GetMapping("/start")
    public HashMap<String, Integer> startIndexer(){
        return indexerService.startAnalyzer();

    }
}
