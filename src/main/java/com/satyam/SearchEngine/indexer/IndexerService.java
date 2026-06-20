package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.Repo.IndexEntryRepo;
import com.satyam.SearchEngine.Repo.PageRepo;
import com.satyam.SearchEngine.crawler.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import com.satyam.SearchEngine.model.IndexEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.satyam.SearchEngine.text.TextAnalyser;

@Service
public class IndexerService {

    @Autowired
    PageRepo pageRepo;

    @Autowired
    IndexEntryRepo indexEntryRepo;

//    Pageable limit = PageRequest.of(0,1000, Sort.by("id"));

    public HashMap<String, Integer> startAnalyzer(){


        List<Page> pagelist = pageRepo.findByStatus(CrawlStatus.CRAWLED/**, Pageable.ofSize(1000)**/);
        int totaldoc = pagelist.size();

        Map<Integer, Map<String,Float>> tfCache = new HashMap<>(totaldoc);
//        List<List<IndexEntry>> indexEntryList = new ArrayList<>(List.of());
        HashMap<String, Integer> docFrequency = new HashMap<>(20000);

        for(Page page:pagelist) {
            List<String> wordsStream = TextAnalyser.start(page.getContent()).toList();
            Set<String> uniqueWords = new HashSet<>(wordsStream);

            Map<String,Float> pageTf = calculateTF(wordsStream);
            tfCache.put(Math.toIntExact(page.getId()),pageTf);

            for (String word : uniqueWords) {
                docFrequency.merge(word, 1, Integer::sum);

            }

        }
        System.out.println("Calculated TF for "+ pagelist.size() + " documents");

        calculateScore(tfCache,docFrequency,totaldoc);

        System.out.println("calculated Score and saved into DB");



        return docFrequency;
    }

    private void calculateScore(Map<Integer, Map<String, Float>> tfCache, HashMap<String, Integer> docFrequency, int totalSize) {

        tfCache.forEach((pid,pageTf) -> {
            List<IndexEntry> indexEntries = new ArrayList<>(pageTf.size());
            pageTf.forEach((word,tf)->{
                float idf = (float) Math.log(  (double) totalSize / docFrequency.get(word));
                indexEntries.add(new IndexEntry(pid,word, tf * idf));
            });
            indexEntryRepo.saveAll(indexEntries);
        });


    }




    private Map<String, Float> calculateTF(List<String> wordsArray ) {

        int totalTerm = wordsArray.size();
        HashMap<String,Integer> wordfreq = new HashMap<>();


        for(String word : wordsArray){
            wordfreq.merge(word, 1, Integer::sum);
        }

        Map<String, Float> pageTf = new HashMap<>(totalTerm);

        wordfreq.forEach((word,count) -> {
                float tf = (float) Math.log(  1 + (double) count / totalTerm);
                pageTf.put(word,tf);
        });

        return pageTf;

    }


}