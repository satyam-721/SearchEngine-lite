package com.satyam.SearchEngine.codesearch.query;

import com.satyam.SearchEngine.codesearch.analyzer.CodeAnalyzer;
import com.satyam.SearchEngine.codesearch.indexer.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CodeSearchService {

    private static final double K1 = 1.2;
    private static final double B = 0.75;

    private static final Map<IndexField, Double> BOOST = new EnumMap<>(Map.of(
            IndexField.NAME, 5.0,
            IndexField.SIGNATURE, 2.0,
            IndexField.ANNOTATIONS, 2.0,
            IndexField.JAVADOC, 2.0,
            IndexField.PATH, 1.5,
            IndexField.BODY, 1.0));

    private final CodeAnalyzer analyzer;
    private final ChunkRepository chunkRepo;
    private final IndexEntryRepository entryRepo;

    public CodeSearchService(CodeAnalyzer analyzer, ChunkRepository chunkRepo,
                             IndexEntryRepository entryRepo) {
        this.analyzer = analyzer;
        this.chunkRepo = chunkRepo;
        this.entryRepo = entryRepo;
    }

    @Transactional(readOnly = true)
    public List<SearchResult> search(String query, int limit) {
        // same analyzer as indexing; stopwords removed for natural-language queries
        List<String> terms = analyzer.analyze(query, true).stream().distinct().toList();
        if (terms.isEmpty()) return List.of();

        long n = chunkRepo.count();
        if (n == 0) return List.of();

        Map<IndexField, Double> avgLen = new EnumMap<>(IndexField.class);
        for (FieldLengthStat s : chunkRepo.averageFieldLengths()) {
            avgLen.put(IndexField.valueOf(s.getField()), s.getAvgLength());
        }

        List<PostingRow> postings = entryRepo.findPostings(terms);

        // df per (field, term) = number of posting rows for that pair
        Map<String, Long> df = postings.stream().collect(
                Collectors.groupingBy(p -> p.getField() + "|" + p.getTerm(), Collectors.counting()));

        Map<Long, Double> scores = new HashMap<>();
        Map<Long, Set<String>> matched = new HashMap<>();

        for (PostingRow p : postings) {
            IndexField field = IndexField.valueOf(p.getField());
            double avgdl = Math.max(avgLen.getOrDefault(field, 1.0), 1.0);
            double docFreq = df.get(p.getField() + "|" + p.getTerm());

            double idf = Math.log(1 + (n - docFreq + 0.5) / (docFreq + 0.5));
            double tf = p.getTf();
            double dl = p.getFieldLength();
            double bm25 = idf * tf * (K1 + 1) / (tf + K1 * (1 - B + B * dl / avgdl));

            scores.merge(p.getChunkId(), BOOST.get(field) * bm25, Double::sum);
            matched.computeIfAbsent(p.getChunkId(), k -> new TreeSet<>())
                    .add(p.getField() + ":" + p.getTerm());
        }

        List<Map.Entry<Long, Double>> top = scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .toList();

        Map<Long, ChunkEntity> chunks = chunkRepo
                .findAllById(top.stream().map(Map.Entry::getKey).toList())
                .stream().collect(Collectors.toMap(ChunkEntity::getId, c -> c));

        List<SearchResult> results = new ArrayList<>();
        for (var e : top) {
            ChunkEntity c = chunks.get(e.getKey());
            results.add(new SearchResult(
                    c.getId(), e.getValue(), c.getFilePath(), c.getClassName(), c.getName(),
                    c.getSignature(), c.getStartLine(), c.getEndLine(),
                    List.copyOf(matched.get(e.getKey()))));
        }
        return results;
    }
}