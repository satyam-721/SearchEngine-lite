package com.satyam.SearchEngine.codesearch.analyzer;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeAnalyzer {

    private static final Pattern RAW_TOKEN = Pattern.compile("[\\p{L}\\p{N}_]+");

    // Zero-width split points inside a token, plus underscores themselves:
    //  1. "_+"                       user_account   -> user | account
    //  2. lower -> Upper             createUser     -> create | User
    //  3. Upper -> Upper+lower       HTTPRequest    -> HTTP | Request
    //  4. letter -> digit            utf8           -> utf | 8
    //  5. digit -> letter            8Decoder       -> 8 | Decoder
    private static final Pattern SUBTOKEN_SPLIT = Pattern.compile(
            "_+"
                    + "|(?<=\\p{Ll})(?=\\p{Lu})"
                    + "|(?<=\\p{Lu})(?=\\p{Lu}\\p{Ll})"
                    + "|(?<=\\p{L})(?=\\p{N})"
                    + "|(?<=\\p{N})(?=\\p{L})");

    private static final Set<String> STOPWORDS = Set.of(
            "a", "an", "the", "is", "are", "was", "were", "be",
            "in", "on", "of", "to", "for", "and", "or", "by", "with",
            "where", "what", "which", "how", "does", "do", "this", "that");

    private static final int MIN_TERM_LENGTH = 2;

    public List<String> analyze(String text) {
        return analyze(text, false);
    }

    /** For javadoc and queries: pass true to drop stopwords. */
    public List<String> analyze(String text, boolean removeStopwords) {
        List<String> terms = new ArrayList<>();
        if (text == null || text.isEmpty()) return terms;

        Matcher m = RAW_TOKEN.matcher(text);
        while (m.find()) {
            String raw = m.group();
            String whole = raw.toLowerCase(Locale.ROOT);

            // 1. the original token, lowercased (keeps exact-name queries precise)
            addTerm(terms, whole, removeStopwords);

            // 2. its sub-tokens, only if splitting actually produced something different
            List<String> parts = splitSubTokens(raw);
            boolean sameAsWhole = parts.size() == 1 && parts.get(0).equals(whole);
            if (!sameAsWhole) {
                for (String part : parts) {
                    addTerm(terms, part, removeStopwords);
                }
            }
        }
        return terms;
    }

    private List<String> splitSubTokens(String raw) {
        List<String> parts = new ArrayList<>();
        for (String p : SUBTOKEN_SPLIT.split(raw)) {
            if (!p.isEmpty()) parts.add(p.toLowerCase(Locale.ROOT));
        }
        return parts;
    }

    private void addTerm(List<String> terms, String term, boolean removeStopwords) {
        if (term.length() < MIN_TERM_LENGTH) return;
        if (removeStopwords && STOPWORDS.contains(term)) return;
        terms.add(term);
    }
}