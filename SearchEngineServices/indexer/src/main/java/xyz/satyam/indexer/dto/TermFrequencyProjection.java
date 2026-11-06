package xyz.satyam.indexer.dto;


public record TermFrequencyProjection(
        String term,
        long pageId,
        float tf
) {}