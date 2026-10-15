package com.satyam.SearchEngine.codesearch.indexer;

import jakarta.persistence.*;

@Entity
@Table(name = "code_index_entries", indexes = {
        @Index(name = "idx_entry_term_field", columnList = "term, field"),
        @Index(name = "idx_entry_chunk", columnList = "chunkId")
})
public class IndexEntryCode {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Long chunkId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field")
    private IndexField field;

    @Column(length = 100)
    private String term;

    private int tf;                        // raw term frequency; BM25 is computed at query time

    protected IndexEntryCode() {}

    public IndexEntryCode(Long chunkId, IndexField field, String term, int tf) {
        this.chunkId = chunkId; this.field = field; this.term = term; this.tf = tf;
    }
}