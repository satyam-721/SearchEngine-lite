package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.Repo.IndexEntryRepo;
import com.satyam.SearchEngine.Repo.PageRepo;
import com.satyam.SearchEngine.crawler.CrawlStatus;
import com.satyam.SearchEngine.model.Page;
import com.satyam.SearchEngine.model.IndexEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
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
        List<List<IndexEntry>> indexEntryList = new ArrayList<>(List.of());

        HashMap<String, Integer> docFrequency = new HashMap<>(20000);

        for(Page page:pagelist) {
            List<String> wordsStream = TextAnalyser.start(page.getContent()).toList();
            Set<String> uniqueWords = new HashSet<>(wordsStream);

            List<IndexEntry> indexEntries = calculateTF(wordsStream,page.getId());
            indexEntryList.add(indexEntries);
            wordsStream = null;

            for (String word : uniqueWords) {
                docFrequency.merge(word, 1, Integer::sum);

            }
            uniqueWords = null;

        }
        System.out.println("Calculated TF for "+ pagelist.size() + " documents");

        calculateScore(indexEntryList,docFrequency,pagelist.size());

        System.out.println("calculated Score and saved into DB");



        return docFrequency;
    }

    private void calculateScore(List<List<IndexEntry>> indexEntryList, HashMap<String, Integer> docFrequency,int totalSize) {
        indexEntryList.forEach(indexEntries -> {
            for(IndexEntry indexEntry : indexEntries){
                double idf = Math.log(  (double) totalSize / docFrequency.get(indexEntry.getWord()));
                indexEntry.setScore( indexEntry.getTf() * idf );

            }
            indexEntryRepo.saveAll(indexEntries);
        });


    }




    private List<IndexEntry> calculateTF(List<String> wordsArray , Long id) {

        List<IndexEntry> indexEntryList = new ArrayList<>();
        HashMap<String,Integer> wordfreq = new HashMap<>();
        int totalTerm = wordsArray.size();

        for(String word : wordsArray){
            wordfreq.merge(word, 1, Integer::sum);
        }


        wordfreq.forEach((word,count) -> {
                double tf = Math.log(  1 + (double) count / totalTerm);
                indexEntryList.add(
                        new IndexEntry(word,id,tf)
                );
        });

        return indexEntryList;

    }


}