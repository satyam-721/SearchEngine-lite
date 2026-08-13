package com.satyam.SearchEngine.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "doc_frequency"
)
public class DocFrequency {
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,
//            generator = "index_seq")
//    @SequenceGenerator(
//            name = "index_seq",
//            sequenceName = "index_seq",
//            allocationSize = 50000
//    )
//    Integer id;

    @Id
    String term;
    float df;
}
