package com.satyam.SearchEngine.controller;

import com.satyam.SearchEngine.model.dto.PageContentDto;
import com.satyam.SearchEngine.query.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class QueryController {

    @Autowired
    QueryService queryService;

    @GetMapping("/{query}")
    public List<PageContentDto> beganSearch(@PathVariable String query) throws Exception {
          return queryService.fetch(query);
    }
}
