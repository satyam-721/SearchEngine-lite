package com.satyam.SearchEngine.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Page {

    private Long id;
    private String url;
    private String title;
    private String bodyText;

}