package com.satyam.SearchEngine;

import com.satyam.SearchEngine.codesearch.CodeIndexingPipeline;
import com.satyam.SearchEngine.codesearch.chunker.CodeChunk;
import com.satyam.SearchEngine.codesearch.chunker.JavaChunker;
import com.satyam.SearchEngine.codesearch.scanner.ScannedFile;
import com.satyam.SearchEngine.codesearch.scanner.Scanner;
import com.satyam.SearchEngine.crawler.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootApplication
public class SearchEngineApplication {


    @Autowired
    Scanner scanner;

    @Autowired
    CodeIndexingPipeline pipeline;

	public static void main(String[] args) {
        SpringApplication.run(SearchEngineApplication.class, args);

    }


//    @Override
//    public void run(String... args) throws Exception {
//        pipeline.indexRepository(Path.of("D:\\SATYAM\\DEVELOPMENT\\SpringBoot\\LearningPROJECTS\\ecomm\\BAckend"));
//    }

//    @Override
//    public void run(String... args) throws Exception {
//
//
//
//        Path repo = Path.of("D:\\SATYAM\\DEVELOPMENT\\SpringBoot\\LearningPROJECTS\\ecomm\\BAckend");
//        JavaChunker chunker = new JavaChunker();
//
//        for (ScannedFile f : new Scanner().scan(repo)) {
//            for (CodeChunk c : chunker.chunk(f)) {
//                System.out.printf("%-12s %s  [%d-%d]%n  annotations: %s%n",
//                        c.kind(), c.id(), c.startLine(), c.endLine(), c.annotations());
//            }
//        }
//

    }
//    void splitsIdentifiers() {
//        assertEquals(List.of("jwtservice", "jwt", "service"),
//                analyzer.analyzeCode("JwtService"));
//        assertEquals(List.of("create_user_account", "create", "user", "account"),
//                analyzer.analyzeCode("create_user_account"));
//        assertEquals(List.of("parsehttprequest", "parse", "http", "request"),
//                analyzer.analyzeCode("parseHTTPRequest"));
//        assertEquals(List.of("user"), analyzer.analyzeCode("user"));
//    }





