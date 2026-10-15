package com.satyam.SearchEngine.codesearch.scanner;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.Path;
import java.time.Instant;

public record ScannedFile(
        Path absolutePath,      // where to read it from
        String relativePath,    // identity inside the repo, e.g. "src/main/java/auth/JwtService.java"
        String fileName,        // "JwtService.java"
        String extension,       // "java"
        String language,        // "java"
        long sizeBytes,
        Instant lastModified
) {}