package com.satyam.SearchEngine.query;

import com.satyam.SearchEngine.model.Repo.IndexEntryRepo;
import com.satyam.SearchEngine.indexer.dataEng.TextAnalyser;
import com.satyam.SearchEngine.model.Repo.PageRepo;
import com.satyam.SearchEngine.model.dto.IndexResult;
import com.satyam.SearchEngine.model.dto.PageContentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QueryService {

    @Autowired
    IndexEntryRepo indexRepo;

    @Autowired
    PageRepo pageRepo;

    public List<PageContentDto> fetch(String query) throws Exception {
        List<String> stemmed_terms = TextAnalyser.start(query).toList();
        List<IndexResult> indexResults = indexRepo.findTopPagesByTerms(stemmed_terms, Pageable.ofSize(20));
//        for(QueryResult queryResult: queryResults){
//            System.out.println(queryResult.getPageId()+":            ");
//            System.out.println();
//        }
        return fetchContents(indexResults);
    }
    public List<PageContentDto> fetchContents(List<IndexResult> pageInfo) throws Exception {

        List<PageContentDto> pageContentList= new ArrayList<>();

        for(IndexResult indexResult: pageInfo){
            pageContentList.add(
                    pageRepo.findPageDetailsById((long) indexResult.pageId()).orElseThrow(
                            () -> new Exception("Page Id not found in PageRepo")
                    )
            );
        }
        return pageContentList;
    }
}
