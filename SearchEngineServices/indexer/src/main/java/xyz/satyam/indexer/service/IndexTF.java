package xyz.satyam.indexer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.satyam.indexer.dto.IndexTFRequest;
import xyz.satyam.indexer.model.TermFrequency;
import xyz.satyam.indexer.repo.DocFrequencyRepo;
import xyz.satyam.indexer.repo.TermFrequencyRepo;
import pack.satyam.stremming.TextAnalyser;

import java.util.HashMap;
import java.util.List;

@Service
public class IndexTF {

    @Autowired
    DocFrequencyRepo docFrequencyRepo;

    @Autowired
    private TermFrequencyRepo termFrequencyRepo;





    public void indexPage(IndexTFRequest indexTFRequest) {
         getDocFrequency(indexTFRequest.getPageId(), indexTFRequest.getPageContent());
        //CalculateTF
    }

    private void getDocFrequency(Long pageId, String content) {
        HashMap<String, Integer> docFrequency = new HashMap<>();

        List<String> wordsList = TextAnalyser.start(content).toList();
//        Set<String> uniqueWords = new HashSet<>(wordsList);

        //Get Words freq
        int totalTerm = wordsList.size();
        HashMap<String,Integer> wordfreq = new HashMap<>();

        for(String word: wordsList){
            wordfreq.merge(word, 1, Integer::sum);
        }

        calculateTF(wordfreq, pageId,totalTerm);

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
            float tf =  (float) count / totalTerm;
            termFrequencyRepo.save( new TermFrequency(word, id, tf) );
        });

    }
}