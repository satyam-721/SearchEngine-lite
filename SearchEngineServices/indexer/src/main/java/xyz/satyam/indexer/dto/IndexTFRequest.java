package xyz.satyam.indexer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexTFRequest {
    Long pageId;
    String pageContent;


}
