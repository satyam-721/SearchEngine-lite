package xyz.satyam.query.dto;


public record TermFrequencyProjection(
        String term,
        long pageId,
        float tf
) {}