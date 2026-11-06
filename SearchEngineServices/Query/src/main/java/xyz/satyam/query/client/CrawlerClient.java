package xyz.satyam.query.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.satyam.query.dto.PageContentDto;

import java.util.List;

@FeignClient("crawler")
public interface CrawlerClient {

    @GetMapping("/crawler/pages")
    public List<PageContentDto> getPageContent(@RequestParam("pageIds")List<Long> resultPages);
}
