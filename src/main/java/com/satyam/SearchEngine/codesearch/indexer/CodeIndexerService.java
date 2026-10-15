package com.satyam.SearchEngine.codesearch.indexer;

import com.satyam.SearchEngine.codesearch.analyzer.CodeAnalyzer;
import com.satyam.SearchEngine.codesearch.chunker.CodeChunk;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CodeIndexerService {

    private static final int MAX_TERM_LENGTH = 100;   // skips base64 blobs, minified junk

    private final CodeAnalyzer analyzer;
    private final ChunkRepository chunkRepo;
    private final IndexEntryRepository entryRepo;

    public CodeIndexerService(CodeAnalyzer analyzer, ChunkRepository chunkRepo,
                              IndexEntryRepository entryRepo) {
        this.analyzer = analyzer;
        this.chunkRepo = chunkRepo;
        this.entryRepo = entryRepo;
    }

    @Transactional
    public void clearAll() {
        chunkRepo.truncateAll();
    }

    /** Indexes all chunks of ONE file in a single transaction. */
    @Transactional
    public void indexChunks(List<CodeChunk> chunks) {
        for (CodeChunk c : chunks) {

            // *** THE ANALYZER CALLS: one per field ***
            Map<IndexField, List<String>> termsByField = analyzeFields(c);

            ChunkEntity entity = new ChunkEntity(
                    c.id(), c.kind(), c.filePath(), c.language(), c.packageName(),
                    c.className(), c.name(), c.signature(), c.annotations(),
                    c.javadoc(), c.body(), c.startLine(), c.endLine());

            entity = chunkRepo.save(entity);

            for (var e : termsByField.entrySet()) {
                entity.getFieldLengths().put(e.getKey(), e.getValue().size());
            }

            List<IndexEntryCode> entries = new ArrayList<>();

            for (var e : termsByField.entrySet()) {
                Map<String, Long> tf = e.getValue().stream()
                        .collect(Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()));

                for (var t : tf.entrySet()) {
                    entries.add(new IndexEntryCode(
                            entity.getId(),
                            e.getKey(),
                            t.getKey(),
                            t.getValue().intValue()));
                }
            }

            entryRepo.saveAll(entries);
        }
    }

    private Map<IndexField, List<String>> analyzeFields(CodeChunk c) {
        Map<IndexField, List<String>> m = new EnumMap<>(IndexField.class);
        m.put(IndexField.NAME,        clean(analyzer.analyze(c.name())));
        m.put(IndexField.SIGNATURE,   clean(analyzer.analyze(c.signature())));
        m.put(IndexField.ANNOTATIONS, clean(analyzer.analyze(c.annotations())));
        m.put(IndexField.JAVADOC,     clean(analyzer.analyze(c.javadoc(), true)));   // stopwords removed
        m.put(IndexField.BODY,        clean(analyzer.analyze(c.body())));
        m.put(IndexField.PATH,        clean(analyzer.analyze(c.filePath())));
        return m;
    }

    private List<String> clean(List<String> terms) {
        return terms.stream().filter(t -> t.length() <= MAX_TERM_LENGTH).toList();
    }
}