package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.crawler.CrawlStatus;
import com.satyam.SearchEngine.model.Repo.DocFrequencyRepo;
import com.satyam.SearchEngine.model.Repo.PageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class IndexIDF {

    @Autowired
    PageRepo pageRepo;

    @Autowired
    DocFrequencyRepo docFrequencyRepo;

    public HashMap<String, Float> calculateIDF(List<String> stemmedTerms) {
        int pageSize = pageRepo.countByStatus(CrawlStatus.CRAWLED);
        HashMap<String, Float> termIDF = new HashMap<>();

        for(String term: stemmedTerms){
            float df = docFrequencyRepo.findDfByTerm(term).orElse(0.0f);
            float idf = (float) Math.log( 1 + (pageSize / df) );
            termIDF.put(term,idf);
        }

        return termIDF;


    }
}
