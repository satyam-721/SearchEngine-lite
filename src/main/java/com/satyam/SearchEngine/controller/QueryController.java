package com.satyam.SearchEngine.controller;

import com.satyam.SearchEngine.model.dto.PageContentDto;
import com.satyam.SearchEngine.query.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin("*")
public class QueryController {

    @Autowired
    QueryService queryService;

    @GetMapping
    public List<PageContentDto> beganSearch(@RequestParam String query) throws Exception {
        System.out.println(query);
        return queryService.fetch(query);
    }
}
