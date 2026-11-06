package xyz.satyam.crawler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.satyam.crawler.model.PageContentDto;
import xyz.satyam.crawler.model.PageRepo;
import xyz.satyam.crawler.services.CrawlStatus;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/crawler")
public class ServiceController {

    @Autowired
    PageRepo pageRepo;

    @GetMapping("/totaldoc")
    public int getTotalDoc(@RequestParam String status){
        CrawlStatus crawlStatus = CrawlStatus.valueOf(status);

        return pageRepo.countByStatus(crawlStatus);
    }

    @GetMapping("/pages")
    public List<PageContentDto> getPageContent(@RequestParam("pageIds") List<Long> pageIds) throws Exception {
        List<PageContentDto> pageContentList = new ArrayList<>();
        for(Long pageId: pageIds){
            pageContentList.add(

                    pageRepo.findPageDetailsById(pageId).orElseThrow(
                            () -> new Exception("Page Id not found in PageRepo")
                    )

            );
        }
        return pageContentList;
    }

}
