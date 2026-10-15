package com.satyam.SearchEngine.codesearch.query;

import java.util.List;

public record SearchResult(
        Long chunkId, double score, String filePath, String className, String name,
        String signature, int startLine, int endLine,
        List<String> matched          // e.g. ["NAME:user", "BODY:jwt"], which fields matched which terms
) {}