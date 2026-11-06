package xyz.satyam.query.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.satyam.query.dto.TermFrequencyProjection;

import java.util.HashMap;
import java.util.List;

@FeignClient(name = "indexer")
public interface IndexerClient {

    @PostMapping("index/idf")
    public HashMap<String, Float> calculateIDF ( @RequestBody List<String> stemmed_terms);

    @GetMapping("index/terms")
    public List<TermFrequencyProjection> getDetailsByTerms (@RequestParam("terms") List<String> stemmed_terms);

}
