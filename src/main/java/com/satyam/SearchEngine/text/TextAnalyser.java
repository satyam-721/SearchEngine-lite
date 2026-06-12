package com.satyam.SearchEngine.text;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TextAnalyser {



    public String stemmer(String word) {
        Stemmer stemmer = new Stemmer();

        stemmer.add(word.toCharArray(),word.length());
        stemmer.stem();
        return stemmer.toString();

    }
}
