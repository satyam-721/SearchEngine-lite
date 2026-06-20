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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String word;
    int pageId;
    float score;

    public IndexEntry( int pageId, String word,float score) {
        this.score = score;
        this.pageId = pageId;
        this.word = word;
    }
}
