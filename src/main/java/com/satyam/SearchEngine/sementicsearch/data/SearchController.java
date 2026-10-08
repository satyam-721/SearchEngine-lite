package com.satyam.SearchEngine.sementicsearch.data;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    @Autowired
    VectorStore vectorStore;

    @GetMapping("/search/{message}")
    public List<Document> search(@PathVariable String message){
        System.out.println("initiate searching");
        return vectorStore.similaritySearch(message);



    }
}
