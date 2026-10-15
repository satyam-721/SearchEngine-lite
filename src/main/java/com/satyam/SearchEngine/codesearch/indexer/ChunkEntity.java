package com.satyam.SearchEngine.codesearch.indexer;

import com.satyam.SearchEngine.codesearch.chunker.ChunkKind;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.Map;

@Entity
@Table(name = "code_chunks", indexes = @Index(name = "idx_chunk_file", columnList = "filePath"))
@Getter
@Setter
public class ChunkEntity {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(unique = true, length = 1000)
    private String chunkKey;               // the CodeChunk id string

    @Enumerated(EnumType.STRING)
    private ChunkKind kind;

    @Column(length = 1000) private String filePath;
    private String language;
    @Column(length = 500)  private String packageName;
    @Column(length = 500)  private String className;
    @Column(length = 500)  private String name;
    @Column(length = 2000) private String signature;

    @Column(columnDefinition = "TEXT") private String annotations;
    @Column(columnDefinition = "TEXT") private String javadoc;
    @Column(columnDefinition = "TEXT") private String body;

    private int startLine;
    private int endLine;

    // number of terms in each field, needed for BM25 length normalization
    @ElementCollection
    @CollectionTable(name = "chunk_field_lengths", joinColumns = @JoinColumn(name = "chunk_id"))
    @MapKeyColumn(name = "field")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "length")
    private Map<IndexField, Integer> fieldLengths = new EnumMap<>(IndexField.class);

    protected ChunkEntity() {}

    public ChunkEntity(String chunkKey, ChunkKind kind, String filePath, String language,
                       String packageName, String className, String name, String signature,
                       String annotations, String javadoc, String body, int startLine, int endLine) {
        this.chunkKey = chunkKey; this.kind = kind; this.filePath = filePath;
        this.language = language; this.packageName = packageName; this.className = className;
        this.name = name; this.signature = signature; this.annotations = annotations;
        this.javadoc = javadoc; this.body = body; this.startLine = startLine; this.endLine = endLine;
    }

    public Long getId() { return id; }
    public String getChunkKey() { return chunkKey; }
    public String getFilePath() { return filePath; }
    public Map<IndexField, Integer> getFieldLengths() { return fieldLengths; }
    // add the remaining getters (or Lombok @Getter) when the query side needs them
}