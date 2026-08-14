package com.satyam.SearchEngine.query;

import com.satyam.SearchEngine.indexer.IndexIDF;
import com.satyam.SearchEngine.model.Repo.IndexEntryRepo;
import com.satyam.SearchEngine.indexer.dataEng.TextAnalyser;
import com.satyam.SearchEngine.model.Repo.PageRepo;
import com.satyam.SearchEngine.model.Repo.TermFrequencyRepo;
import com.satyam.SearchEngine.model.TermFrequencyProjection;
import com.satyam.SearchEngine.model.dto.PageContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    IndexEntryRepo indexRepo;

    @Autowired
    PageRepo pageRepo;

    @Autowired
    IndexIDF indexIDF;

    @Autowired
    TermFrequencyRepo termFrequencyRepo;

    public List<PageContentDto> fetch(String query) throws Exception {

        List<String> stemmed_terms = TextAnalyser.start(query).toList();
        HashMap<String, Float> termIDF =  indexIDF.calculateIDF(stemmed_terms);

        List<TermFrequencyProjection> termFrequencies = termFrequencyRepo.findByTerms(stemmed_terms);

        HashMap<Long,Float> pageData = getScore(termFrequencies,termIDF);

        List<Long> resultPages = rankPages(pageData);

//        List<IndexResult> indexResults = indexRepo.findTopPagesByTerms(stemmed_terms, Pageable.ofSize(20));

        return fetchContents(resultPages);
    }

    private List<Long> rankPages(HashMap<Long, Float> pageData) {
        return pageData.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Float>comparingByValue().reversed())
                .limit(20)
                .map(Map.Entry::getKey)
                .toList();

    }

    private HashMap<Long, Float> getScore(List<TermFrequencyProjection> termFrequencies, HashMap<String, Float> termIDF) {
        HashMap<Long,Float> pageScores = new HashMap<>();

        for(TermFrequencyProjection termfreq: termFrequencies){
            long pageId = termfreq.getPageId();
            float tf = termfreq.getTf();
            String term = termfreq.getTerm();

            float idf = termIDF.get(term);

            float score = idf * tf;

            pageScores.merge(pageId, score, Float::sum);
        }
        return pageScores;

    }

    public List<PageContentDto> fetchContents(List<Long> resultPages) throws Exception {

        List<PageContentDto> pageContentList= new ArrayList<>();

        for(Long pageId: resultPages){
            pageContentList.add(
                    pageRepo.findPageDetailsById(pageId).orElseThrow(
                            () -> new Exception("Page Id not found in PageRepo")
                    )
            );
        }

        return pageContentList;
    }
}
