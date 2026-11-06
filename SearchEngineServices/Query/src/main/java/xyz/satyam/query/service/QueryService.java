package xyz.satyam.query.service;

//import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pack.satyam.stremming.TextAnalyser;
import xyz.satyam.query.client.CrawlerClient;
import xyz.satyam.query.client.IndexerClient;
import xyz.satyam.query.dto.PageContentDto;
import xyz.satyam.query.dto.TermFrequencyProjection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @Autowired
    IndexerClient indexerClient;

    @Autowired
    CrawlerClient crawlerClient;

    public List<PageContentDto> fetch(String query) throws Exception {

        //measuring BuinessLogic using micrometer
//        meterRegistry.counter("search.request").increment();

//        if(!query.startsWith("sat721 ")) {
//            return FallBackApi.generate(query);
//        }
//        query = query.substring(6);

        List<String> stemmed_terms = TextAnalyser.start(query).toList();

        // TODO: send List<String>
        HashMap<String, Float> termIDF =  indexerClient.calculateIDF(stemmed_terms);


        List<TermFrequencyProjection> termFrequencies = indexerClient.getDetailsByTerms(stemmed_terms);

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
            long pageId = termfreq.pageId();
            float tf = termfreq.tf();
            String term = termfreq.term();

            float idf = termIDF.get(term);

            float score = idf * tf;

            pageScores.merge(pageId, score, Float::sum);
        }
        return pageScores;

    }

    public List<PageContentDto> fetchContents(List<Long> resultPages) throws Exception {

        List<PageContentDto> pageContentList= crawlerClient.getPageContent(resultPages);

//        for(Long pageId: resultPages){
//            pageContentList.add(
//
//                    pageRepo.findPageDetailsById(pageId).orElseThrow(
//                            () -> new Exception("Page Id not found in PageRepo")
//                    )
//
//            );
//        }

        return pageContentList;
    }
}
