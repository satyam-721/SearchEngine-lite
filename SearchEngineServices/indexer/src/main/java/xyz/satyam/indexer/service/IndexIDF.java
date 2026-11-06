package xyz.satyam.indexer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.satyam.indexer.client.CrawlerClient;
import xyz.satyam.indexer.repo.DocFrequencyRepo;

import java.util.HashMap;
import java.util.List;

@Service
public class IndexIDF {


    @Autowired
    DocFrequencyRepo docFrequencyRepo;

    @Autowired
    CrawlerClient crawlerClient;

    public HashMap<String, Float> calculateIDF(List<String> stemmedTerms) {

        int pageSize = crawlerClient.getTotalDoc("CRAWLED");

        HashMap<String, Float> termIDF = new HashMap<>();

        for(String term: stemmedTerms){
            float df = docFrequencyRepo.findDfByTerm(term).orElse(0.0f);
            float idf = (float) Math.log( 1 + (pageSize / df) );
            termIDF.put(term,idf);
        }

        return termIDF;


    }
}
