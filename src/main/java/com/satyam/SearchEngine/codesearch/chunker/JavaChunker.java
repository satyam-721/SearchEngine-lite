package com.satyam.SearchEngine.codesearch.chunker;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.satyam.SearchEngine.codesearch.scanner.ScannedFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JavaChunker {

    private static final int MAX_BODY_CHARS = 8_000;

    public JavaChunker() {
        StaticJavaParser.getParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_21);
    }

    public List<CodeChunk> chunk(ScannedFile file) {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(file.absolutePath());
        } catch (IOException | ParseProblemException e) {
            System.err.println("Skipping unparsable file: " + file.relativePath() + " (" + e.getMessage() + ")");
            return List.of();
        }

        String pkg = cu.getPackageDeclaration().map(p -> p.getNameAsString()).orElse("");
        List<CodeChunk> chunks = new ArrayList<>();

        // 1. Class-level chunks (classes, interfaces, enums, records)
        for (TypeDeclaration<?> type : cu.findAll(TypeDeclaration.class)) {
            if (isInsideAnonymousClass(type)) continue;
            chunks.add(buildTypeChunk(type, file, pkg));
        }

        // 2. Methods
        for (MethodDeclaration m : cu.findAll(MethodDeclaration.class)) {
            if (isInsideAnonymousClass(m)) continue;
            chunks.add(buildCallableChunk(m, ChunkKind.METHOD, file, pkg));
        }

        // 3. Constructors
        for (ConstructorDeclaration c : cu.findAll(ConstructorDeclaration.class)) {
            if (isInsideAnonymousClass(c)) continue;
            chunks.add(buildCallableChunk(c, ChunkKind.CONSTRUCTOR, file, pkg));
        }

        return chunks;
    }

    // ---------- builders ----------

    private CodeChunk buildCallableChunk(CallableDeclaration<?> node, ChunkKind kind,
                                         ScannedFile file, String pkg) {
        String className = qualifiedTypeName(node);
        String name = node.getNameAsString();

        String paramTypes = node.getParameters().stream()
                .map(p -> p.getType().asString())
                .collect(Collectors.joining(","));
        String id = file.relativePath() + "#" + className + "." + name + "(" + paramTypes + ")";

        return new CodeChunk(
                id, kind, file.relativePath(), file.language(), pkg, className, name,
                node.getDeclarationAsString(true, true, true),
                annotationsOf(node),
                javadocOf(node),
                truncate(node.toString()),
                lineOf(node, true), lineOf(node, false));
    }

    private CodeChunk buildTypeChunk(TypeDeclaration<?> type, ScannedFile file, String pkg) {
        String className = qualifiedTypeName(type);

        // Class chunk body = fields + method signatures (not full method bodies)
        StringBuilder sb = new StringBuilder();
        for (FieldDeclaration f : type.getFields()) {
            sb.append(f.toString().strip()).append('\n');
        }
        for (MethodDeclaration m : type.getMethods()) {
            sb.append(m.getDeclarationAsString(true, true, true)).append('\n');
        }

        return new CodeChunk(
                file.relativePath() + "#" + className,
                ChunkKind.CLASS, file.relativePath(), file.language(), pkg, className,
                type.getNameAsString(),
                className,
                annotationsOf(type),
                javadocOf(type),
                truncate(sb.toString()),
                lineOf(type, true), lineOf(type, false));
    }

    // ---------- helpers ----------

    // Builds "Outer.Inner" by walking up the tree
    private static String qualifiedTypeName(Node node) {
        Deque<String> names = new ArrayDeque<>();
        Optional<Node> current = Optional.of(node);
        while (current.isPresent()) {
            Node n = current.get();
            if (n instanceof TypeDeclaration<?> t) names.addFirst(t.getNameAsString());
            current = n.getParentNode();
        }
        return String.join(".", names);
    }

    private static boolean isInsideAnonymousClass(Node node) {
        return node.findAncestor(ObjectCreationExpr.class).isPresent();
    }

    private static String annotationsOf(BodyDeclaration<?> node) {
        return node.getAnnotations().stream()
                .map(AnnotationExpr::toString)
                .collect(Collectors.joining(" "));
    }

    private static String javadocOf(Node node) {
        return node.getComment()
                .filter(c -> c instanceof JavadocComment)
                .map(c -> c.getContent().strip())
                .orElse("");
    }

    private static int lineOf(Node node, boolean begin) {
        return (begin ? node.getBegin() : node.getEnd())
                .map(p -> p.line).orElse(-1);
    }

    private static String truncate(String s) {
        return s.length() <= MAX_BODY_CHARS ? s : s.substring(0, MAX_BODY_CHARS);
    }
}