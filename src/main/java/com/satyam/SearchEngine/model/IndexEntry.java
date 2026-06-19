package com.satyam.SearchEngine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class IndexEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String word;
    long pageId;
    @Transient
    double tf;
    double score;

    public IndexEntry(String word, long pageId, double tf) {
        this.word = word;
        this.pageId = pageId;
        this.tf = tf;
    }
}
