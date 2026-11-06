package xyz.satyam.crawler.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.satyam.crawler.dto.IndexTFRequest;

@FeignClient(name = "indexer")
public interface IndexerClient {

    @PostMapping("index/indextf")
    void indexTF(
            @RequestBody IndexTFRequest indexTFRequest
            );

}
