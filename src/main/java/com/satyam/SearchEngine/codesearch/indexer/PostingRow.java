package com.satyam.SearchEngine.codesearch.indexer;

public interface PostingRow {
    Long getChunkId();
    String getField();
    String getTerm();
    Integer getTf();
    Integer getFieldLength();
}