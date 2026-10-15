package com.satyam.SearchEngine.codesearch;

import com.satyam.SearchEngine.codesearch.chunker.CodeChunk;
import com.satyam.SearchEngine.codesearch.chunker.JavaChunker;
import com.satyam.SearchEngine.codesearch.indexer.CodeIndexerService;
import com.satyam.SearchEngine.codesearch.scanner.ScannedFile;
import com.satyam.SearchEngine.codesearch.scanner.Scanner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class CodeIndexingPipeline {

    private final Scanner scanner;
    private final JavaChunker chunker;
    private final CodeIndexerService indexer;

    public CodeIndexingPipeline(Scanner scanner, JavaChunker chunker, CodeIndexerService indexer) {
        this.scanner = scanner;
        this.chunker = chunker;
        this.indexer = indexer;
    }

    public void indexRepository(Path repoRoot) throws IOException {
        indexer.clearAll();                                  // full rebuild for now

        List<ScannedFile> files = scanner.scan(repoRoot);
        int chunkCount = 0, failed = 0;

        for (ScannedFile file : files) {                     // one file at a time
            try {
                List<CodeChunk> chunks = chunker.chunk(file);
                indexer.indexChunks(chunks);
                chunkCount += chunks.size();
            } catch (Exception e) {                          // one bad file must not stop the run
                failed++;
                System.err.println("Failed to index " + file.relativePath() + ": " + e.getMessage());
            }
        }
        System.out.printf("Indexed %d files, %d chunks, %d failed%n", files.size(), chunkCount, failed);
    }
}