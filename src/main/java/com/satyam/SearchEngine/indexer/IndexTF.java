package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.model.Page;
import com.satyam.SearchEngine.model.Repo.DocFrequencyRepo;
import com.satyam.SearchEngine.model.Repo.TermFrequencyRepo;
import com.satyam.SearchEngine.model.TermFrequency;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import com.satyam.SearchEngine.indexer.dataEng.TextAnalyser;
import org.springframework.stereotype.Service;

@Service
public class IndexTF {

    @Autowired
    DocFrequencyRepo docFrequencyRepo;

    @Autowired
    private TermFrequencyRepo termFrequencyRepo;





    public void indexPage(Page page) {
         getDocFrequency(page);
        //CalculateTF
    }

    private void getDocFrequency(Page page) {
        HashMap<String, Integer> docFrequency = new HashMap<>();

        List<String> wordsList = TextAnalyser.start(page.getContent()).toList();
//        Set<String> uniqueWords = new HashSet<>(wordsList);

        //Get Words freq
        int totalTerm = wordsList.size();
        HashMap<String,Integer> wordfreq = new HashMap<>();

        for(String word: wordsList){
            wordfreq.merge(word, 1, Integer::sum);
        }

        calculateTF(wordfreq, page.getId(),totalTerm);

        //DF
        updateDF(wordfreq);

    }

    private void updateDF(HashMap<String, Integer> wordfreq) {
        for (String key : wordfreq.keySet()) {
            docFrequencyRepo.incrementDf(key);
        }

    }

    private void calculateTF(HashMap<String, Integer> wordfreq, Long id, int totalTerm) {

        wordfreq.forEach((word,count) -> {
            float tf = (float) Math.log(  1 + (double) count / totalTerm);
            termFrequencyRepo.save( new TermFrequency(word, id, tf) );
        });

    }
}