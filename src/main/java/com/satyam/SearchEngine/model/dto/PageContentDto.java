package com.satyam.SearchEngine.model.dto;

public record PageContentDto(
        Long id,
        String url,
        String title,
        String snippet
) {
}
