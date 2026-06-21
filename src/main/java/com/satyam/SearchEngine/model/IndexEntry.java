package com.satyam.SearchEngine.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class IndexEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "index_seq")
    @SequenceGenerator(
            name = "index_seq",
            sequenceName = "index_seq",
            allocationSize = 50000
    )
    Integer id;

    String word;
    int pageId;
    @Transient
    float tf;
    float score;

    public IndexEntry( int pageId, String word,float score) {
        this.score = score;
        this.pageId = pageId;
        this.word = word;
    }

    public IndexEntry(String word, int id, float tf) {
        this.word = word;
        this.pageId = id;
        this.tf = tf;
    }
}
