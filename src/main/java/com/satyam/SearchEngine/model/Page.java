package com.satyam.SearchEngine.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Page {

//    private Long id;
    private String url;
    private String title;
    private String content;

}