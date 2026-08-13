package com.satyam.SearchEngine.indexer.dataEng;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
public class StopWords {

    private static final HashSet<String> STOP_WORDS;

    static {
        try {
            System.out.println("Loading stopwords.txt");

            ClassPathResource resource = new ClassPathResource("stopwords.txt");

            String[] content = resource.getContentAsString(StandardCharsets.UTF_8).split("\\r?\\n");

            STOP_WORDS = new HashSet<>(Arrays.asList(content));
        }
        catch(Exception e){
            System.out.println(e);
            throw new RuntimeException(e);
        }

    }

    public List<String> removeStopWords(List<String> tokens){

        return tokens
                .stream()
                .filter(st -> !STOP_WORDS.contains(st))
                .toList();
    }
}
