package xyz.satyam.query.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.satyam.query.dto.PageContentDto;
import xyz.satyam.query.service.QueryService;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin("*")
public class QueryController {

    @Autowired
    QueryService queryService;

    @GetMapping
    public List<PageContentDto> beganSearch(@RequestParam String query) throws Exception {
        System.out.println(query.length());
        return queryService.fetch(query);
    }
}
