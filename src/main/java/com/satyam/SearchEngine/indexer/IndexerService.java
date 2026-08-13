package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.model.PageContent;
import com.satyam.SearchEngine.model.Repo.IndexEntryRepo;
import com.satyam.SearchEngine.model.Repo.PageRepo;
import com.satyam.SearchEngine.crawler.CrawlStatus;
import com.satyam.SearchEngine.model.IndexEntry;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

import com.satyam.SearchEngine.indexer.dataEng.TextAnalyser;

@Service
@Transactional
public class IndexerService {

    @Autowired
    PageRepo pageRepo;

    @Autowired
    IndexEntryRepo indexEntryRepo;

    @PersistenceContext
    private EntityManager entityManager;

//    Pageable limit = PageRequest.of(0,1000, Sort.by("id"));

    public HashMap<String, Integer> startAnalyzer(){

        System.out.println("Started db feching");

        Pageable pageable = PageRequest.of(0,1000, Sort.by("id"));
        Page<PageContent> page;

        //1st Pass
        HashMap<String, Integer> docFrequency = getDocFrequency();


        //2nd Pass

        int pageSize = pageRepo.countByStatus(CrawlStatus.CRAWLED);

        System.out.println("Scanning "+pageSize+" Pages" );

        do {
            printMemory("Starting...");
            List<IndexEntry> indexEntryList = new ArrayList<>(300);
            page = pageRepo.findByStatus(CrawlStatus.CRAWLED, pageable);


            System.out.println(page.getContent().getFirst().getId());

            for (PageContent webpage : page.getContent()) {

                //can be optimised  without creating wordslist
                List<String> wordsList = TextAnalyser.start(webpage.getContent()).toList();
                indexEntryList = calculateTF(wordsList, webpage.getId(), indexEntryList);
            }

            System.out.println("Calculated TF for "+ page.getSize() + " documents");

            calculateScore(indexEntryList,docFrequency,pageSize);

            pageable = pageable.next();
            printMemory("Ending...");
        }while(page.hasNext());
        System.out.println("DONE");



        return docFrequency;
    }

    private HashMap<String, Integer> getDocFrequency() {
        HashMap<String, Integer> docFrequency = new HashMap<>(20000);
        List<PageContent> pageContentList = pageRepo.findByStatus(CrawlStatus.CRAWLED);

        for(PageContent pageContent: pageContentList){


            Set<String> uniqueWords = new HashSet<>(TextAnalyser.start(pageContent.getContent()).toList());
            for (String word : uniqueWords) {
                docFrequency.merge(word, 1, Integer::sum);

            }
        }
        System.out.println("1st Pass Completed: DocFrequency Created");
        return docFrequency;


    }

    //calculating log(tf/idf)
    private void calculateScore(List<IndexEntry> indexEntryList, HashMap<String, Integer> docFrequency,int totalSize) {
        for(IndexEntry indexEntry : indexEntryList){
            float idf = (float) Math.log(  (double) totalSize / docFrequency.get(indexEntry.getWord()));
            indexEntry.setScore( indexEntry.getTf() * idf );



        }
        indexEntryRepo.saveAll(indexEntryList);
        entityManager.flush();
        entityManager.clear();

        System.out.println("Saved "+ indexEntryList.size()+ " records");


    }




    private List<IndexEntry> calculateTF(List<String> wordsArray , long id, List<IndexEntry> indexEntryList) {

        int totalTerm = wordsArray.size();
        HashMap<String,Integer> wordfreq = new HashMap<>();


        for(String word : wordsArray){
            wordfreq.merge(word, 1, Integer::sum);
        }


        wordfreq.forEach((word,count) -> {
                float tf = (float) Math.log(  1 + (double) count / totalTerm);
                indexEntryList.add(
                        new IndexEntry(word,(int) id,tf)
                );
        });

        return indexEntryList;

    }

    private void printMemory(String stage) {
        Runtime rt = Runtime.getRuntime();

        long used =
                (rt.totalMemory() - rt.freeMemory())
                        / 1024 / 1024;

        System.out.println(stage + ": " + used + " MB");
    }


}