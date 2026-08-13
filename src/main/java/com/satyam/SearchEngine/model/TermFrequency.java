package com.satyam.SearchEngine.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TermFrequency {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "index_seq")
    @SequenceGenerator(
            name = "index_seq",
            sequenceName = "index_seq",
            allocationSize = 50000
    )
    Integer id;

    String term;
    long pageId;
    float tf;

    public TermFrequency(String term, long pageId, float tf) {
        this.term = term;
        this.pageId = pageId;
        this.tf = tf;
    }
}
