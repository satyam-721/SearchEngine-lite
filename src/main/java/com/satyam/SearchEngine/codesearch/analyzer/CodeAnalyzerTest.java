package com.satyam.SearchEngine.codesearch.analyzer;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeAnalyzerTest {

    private final CodeAnalyzer analyzer = new CodeAnalyzer();

    @Test void camelCase() {
        assertEquals(List.of("jwtservice", "jwt", "service"), analyzer.analyze("JwtService"));
    }

    @Test void snakeCase() {
        assertEquals(List.of("create_user_account", "create", "user", "account"),
                analyzer.analyze("create_user_account"));
    }

    @Test void acronymInside() {
        assertEquals(List.of("parsehttprequest", "parse", "http", "request"),
                analyzer.analyze("parseHTTPRequest"));
    }

    @Test void acronymAtStart() {
        assertEquals(List.of("httpserver", "http", "server"), analyzer.analyze("HTTPServer"));
    }

    @Test void acronymsMidName() {
        assertEquals(List.of("geturlfromdb", "get", "url", "from", "db"),
                analyzer.analyze("getURLFromDB"));
    }

    @Test void lettersAndDigits() {
        // the single-char "8" is dropped by MIN_TERM_LENGTH
        assertEquals(List.of("utf8decoder", "utf", "decoder"), analyzer.analyze("utf8Decoder"));
        assertEquals(List.of("abc123def", "abc", "123", "def"), analyzer.analyze("ABC123def"));
    }

    @Test void plainWordEmittedOnce() {
        assertEquals(List.of("user"), analyzer.analyze("user"));
    }

    @Test void edgeCases() {
        assertEquals(List.of(), analyzer.analyze(""));
        assertEquals(List.of(), analyzer.analyze(null));
        assertEquals(List.of(), analyzer.analyze("a"));
        assertEquals(List.of("__init__", "init"), analyzer.analyze("__init__"));
    }

    @Test void stopwordsOnlyWhenAsked() {
        String q = "where is user authentication handled";
        assertEquals(List.of("where", "is", "user", "authentication", "handled"), analyzer.analyze(q));
        assertEquals(List.of("user", "authentication", "handled"), analyzer.analyze(q, true));
    }
}