package xyz.satyam.indexer.controller;

import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.satyam.indexer.dto.IndexTFRequest;
import xyz.satyam.indexer.dto.TermFrequencyProjection;
import xyz.satyam.indexer.repo.TermFrequencyRepo;
import xyz.satyam.indexer.service.IndexIDF;
import xyz.satyam.indexer.service.IndexTF;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("index")
public class ServiceController {

    @Autowired
    IndexTF indexTF;

    @Autowired
    IndexIDF indexIDF;

    @Autowired
    TermFrequencyRepo termFrequencyRepo;

    @PostMapping("/indextf")
    void indexTF( @RequestBody IndexTFRequest indexTFRequest){

        System.out.println("Index TF starts" + indexTFRequest.getPageId());
        indexTF.indexPage(indexTFRequest);

    }

    @PostMapping("/idf")
    public HashMap<String, Float> calculateIDF( @RequestBody List<String> stemmed_terms){
        System.out.println(stemmed_terms);
        return indexIDF.calculateIDF(stemmed_terms);
    }

    @GetMapping("/terms")
    public List<TermFrequencyProjection> getDetailsByTerms (@RequestParam("terms") List<String> stemmed_terms){
        return termFrequencyRepo.findByTerms(stemmed_terms);
    }

}
