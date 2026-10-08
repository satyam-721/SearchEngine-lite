package com.satyam.SearchEngine.sementicsearch.Embeddings;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController

//TESTING purpose....
public class MappingController {

    @Qualifier("googleGenAiTextEmbedding")
    @Autowired
    EmbeddingModel embeddingModel;

    @GetMapping("/embedding/{message}")
    public Map<String, Object> generateEmbedding(@PathVariable String message) {
        // Embed a single text input into a dense vector float array
        float[] embedding = embeddingModel.embed(message);

        return Map.of(
                "input", message,
                "vector_dimensions", embedding.length,
                "vector", embedding   //ignore this printing array
        );
    }

    @GetMapping("/cosinesimilarity")
    public Double getCosineSimilarity(@RequestParam String word1, @RequestParam String word2){

        float[] word1embed = embeddingModel.embed(word1);
        float[] word2embed = embeddingModel.embed((word2));
        double upperlimit = 0;
        double norm1 = 0;
        double norm2 = 0;

        for(int i=0;i<word1embed.length;i++){
            upperlimit += word1embed[i] * word2embed[i];
            norm1 +=Math.pow(word1embed[i],2);
            norm2 +=Math.pow(word2embed[i],2);
        }

        return upperlimit / (Math.sqrt(norm1) * Math.sqrt(norm2));

    }
}
