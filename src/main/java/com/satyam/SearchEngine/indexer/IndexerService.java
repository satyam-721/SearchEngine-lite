package com.satyam.SearchEngine.indexer;

import com.satyam.SearchEngine.text.StopWords;
import com.satyam.SearchEngine.text.TextAnalyser;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexerService {

    //  pre-processing: collapse letter.letter → letterletter
    private static final Pattern ACRONYM_DOT =
            Pattern.compile("(?<=[A-Z])\\.(?=[A-Z])");

    // extract sequences of 2+ Unicode letters
    private static final Pattern TOKEN_PATTERN =
            Pattern.compile("[\\p{L}]{2,}");

    public static void main(String args[]){
//        htmlTokenizer();

        StopWords stopWords = new StopWords();
        TextAnalyser analyser = new TextAnalyser();

        List<String> tokens =  tokenize("Hello1234 Schrödinger's this is Ph.D");

        tokens = stopWords.removeStopWords(tokens);


        tokens = tokens.stream()
                .map(analyser::stemmer)
                .toList();





        System.out.println(tokens);

    }



    public static void htmlTokenizer(){
        String text = "Hello1234 Schrödinger's this is U.S.A";

//        Pattern DOTTED_ACRONYM  = Pattern.compile("\\b([A-Z]\\.){2,}[A-Z]?\\.?\\b");
        Pattern DOTTED_ACRONYM = Pattern.compile("[\\p{L}]{2,}");

        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        Matcher matcher = DOTTED_ACRONYM.matcher(normalized);


        while(matcher.find()){
            System.out.println( matcher.group());

        }


    }



    public static List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();

        // Step 1: collapse dotted acronyms BEFORE tokenizing
        String preprocessed = collapseDottedAcronyms(text);


        // Step 2: Unicode normalization (ö → o)
        String normalized = Normalizer.normalize(preprocessed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        // \p{M} = Unicode "Mark" category = combining diacritics

        // Step 3: extract tokens
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




}
