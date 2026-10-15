package com.satyam.SearchEngine.codesearch;

import com.satyam.SearchEngine.codesearch.query.CodeSearchService;
import com.satyam.SearchEngine.codesearch.query.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/code")
@CrossOrigin("*")
public class CodeController {

    @Autowired
    CodeIndexingPipeline pipeline;

    @Autowired
    CodeSearchService searchService;


    @PostMapping("/search")
    public List<SearchResult> search(@RequestParam String q,
                                     @RequestParam(defaultValue = "10") int limit) {
        return searchService.search(q, Math.min(limit, 50));
    }

    @PostMapping("/index")
    public String index(@RequestParam String path) throws IOException {
        System.out.println("PATH: "+ path);
        pipeline.indexRepository(Path.of(path));
        return "success";
    }
}
