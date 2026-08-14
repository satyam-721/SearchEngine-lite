package com.satyam.SearchEngine.model;

public interface TermFrequencyProjection {
    String getTerm();
    long getPageId();
    float getTf();
}