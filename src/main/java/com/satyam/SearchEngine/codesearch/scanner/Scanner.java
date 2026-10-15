package com.satyam.SearchEngine.codesearch.scanner;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class Scanner {

    // Directories that should be never descend
    private static final Set<String> IGNORED_DIRS =
            Set.of(".git", "target", "build", "node_modules", ".idea", ".mvn", ".gradle");

    //What files should be index
    private static final Map<String, String> SUPPORTED_EXTENSIONS =
            Map.of("java", "java");

    private static final long MAX_FILE_SIZE_BYTES = 1_000_000;

    public List<ScannedFile> scan(Path repoRoot) throws IOException {
        List<ScannedFile> results = new ArrayList<>();

        // recursively walks through directories and files.
        // We give it a "visitor" with callbacks that run at each step.
        Files.walkFileTree(repoRoot, new SimpleFileVisitor<>() {

            // Called BEFORE entering a directory
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                // Never skip the root itself, even if it is named "build" etc.
                if (!dir.equals(repoRoot)) {
                    String dirName = dir.getFileName().toString();
                    if (IGNORED_DIRS.contains(dirName)) {
                        return FileVisitResult.SKIP_SUBTREE; // don't go inside
                    }
                }
                return FileVisitResult.CONTINUE; // go inside
            }

            // Called for every file
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                // Ignore symlinks, directories-as-files, sockets, etc.
                if (!attrs.isRegularFile() || attrs.isSymbolicLink()) {
                    return FileVisitResult.CONTINUE;
                }
                if (attrs.size() > MAX_FILE_SIZE_BYTES) {
                    return FileVisitResult.CONTINUE;
                }

                String fileName = file.getFileName().toString();
                String extension = getExtension(fileName);
                String language = SUPPORTED_EXTENSIONS.get(extension);
                if (language == null) {
                    return FileVisitResult.CONTINUE; // unsupported type
                }

                // Path relative to the repo root, always with "/" separators
                String relativePath = repoRoot.relativize(file)
                        .toString()
                        .replace('\\', '/');

                results.add(new ScannedFile(
                        file,
                        relativePath,
                        fileName,
                        extension,
                        language,
                        attrs.size(),
                        attrs.lastModifiedTime().toInstant()
                ));
                return FileVisitResult.CONTINUE;
            }

            // Called when a file/dir can't be read (permissions, etc.)
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.println("Skipping unreadable path: " + file + " (" + exc.getMessage() + ")");
                return FileVisitResult.CONTINUE; // one bad file must not stop the scan
            }
        });
        for(ScannedFile i: results)
        System.out.println(i.fileName());

        return results;
    }

    private static String getExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1).toLowerCase();
    }
}