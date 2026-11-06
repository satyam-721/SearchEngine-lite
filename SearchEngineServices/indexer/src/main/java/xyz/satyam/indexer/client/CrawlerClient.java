package xyz.satyam.indexer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "crawler")
public interface CrawlerClient {

    @GetMapping("crawler/totaldoc")
    int getTotalDoc(@RequestParam String status);

}
