package com.satyam.SearchEngine.codesearch.chunker;

import com.satyam.SearchEngine.codesearch.chunker.ChunkKind;

public record CodeChunk(
        String id,            // "src/.../UserService.java#UserService.createUser(String,int)"
        ChunkKind kind,
        String filePath,      // relative path
        String language,
        String packageName,
        String className,     // qualified within the file, e.g. "Outer.Inner"
        String name,          // method/class simple name
        String signature,     // "public User createUser(String name, int age)"
        String annotations,   // "@PostMapping(\"/users\") @Transactional"
        String javadoc,       // natural-language description, may be empty
        String body,          // source text of the unit
        int startLine,
        int endLine
) {}