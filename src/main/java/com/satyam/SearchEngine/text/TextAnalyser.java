package com.satyam.SearchEngine.text;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Component
public class TextAnalyser {

    //  pre-processing: collapse letter.letter → letterletter
    private static final Pattern ACRONYM_DOT = Pattern.compile("(?<=[A-Z])\\.(?=[A-Z])");

    // extract sequences of 2+ Unicode letters
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[\\p{L}]{2,}");



    public static String stemmer(String word) {
        Stemmer stemmer = new Stemmer();
        stemmer.add(word.toCharArray(),word.length());
        stemmer.stem();
        return stemmer.toString();

    }

    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();

        // Step 1: collapse dotted acronyms BEFORE tokenizing
        String preprocessed = collapseDottedAcronyms(text);


        // Unicode normalization (ö → o)
        String normalized = Normalizer.normalize(preprocessed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // \p{M} = Unicode "Mark" category = combining diacritics

        List<String> tokens = new ArrayList<>(512);
        Matcher matcher = TOKEN_PATTERN.matcher(normalized);


        while (matcher.find()) {
            tokens.add(matcher.group().toLowerCase(Locale.ROOT));
        }
        return tokens;
    }

    private static String collapseDottedAcronyms(String text) {
        return ACRONYM_DOT.matcher(text).replaceAll("");
    }

    public static Stream<String> start(String content){
//        htmlTokenizer();

        StopWords stopWords = new StopWords();

        List<String> tokens =  tokenize(content);

        tokens = stopWords.removeStopWords(tokens);


        return tokens.stream()
                .map(TextAnalyser::stemmer);






    }
}
