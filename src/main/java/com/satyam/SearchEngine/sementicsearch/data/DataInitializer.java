package com.satyam.SearchEngine.sementicsearch.data;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer {

    @Autowired
    VectorStore vectorStore;

//    @PostConstruct   //run after bean is constructed
    public void initData(){
        System.out.println("initData");
        //TODO: Guard — don't re-index if already populated

//        reads AND wraps the content into Document objects with metadata automatically other than plain java file IO
        TextReader textReader = new TextReader(new ClassPathResource("product_details.txt"));

        //TODO: fix metadata
        /** using TextReader we get something like this
         * // You get: List<Document> where each Document has:
         * // {
         * //   content: "Title: Bluetooth Wireless Earbuds\n...(entire file content)...",
         * //   metadata: {
         * //     "source":            "products.txt",
         * //     "charset":           "UTF-8",
         * //     "contentType":       "text/plain",
         * //     "resourceFilename":  "products.txt"
         * //   }
         * // }
         */

        TokenTextSplitter splitter = TokenTextSplitter.builder()

                //TODO: change the chunking approach here
                .withChunkSize(200)
                .withMinChunkSizeChars(30)     //chunk should has at least this char, if not merge with another chunk
                .withMinChunkLengthToEmbed(20)   //if chunk has min this , then it wont be embedded and stored just ignored
                .withMaxNumChunks(500)
                .withKeepSeparator(false)   //Determines whether separators (such as newlines or punctuation used during splitting) are kept in the chunks
                .build();

        List<Document> documents = splitter.split(textReader.get());

        //embedding happening here
        vectorStore.add(documents);
        System.out.println("Done initData");
    }


    /**web_details.txt
     │
     ▼
     TextReader
     │
     ▼
     List<Document> (1 document)
     │
     ▼
     TokenTextSplitter
     │
     ▼
     List<Document> (many chunks)
     │
     ▼
     vectorStore.add()
     │
     ├── Chunk 1 ──► Embedding Model ──► Vector
     ├── Chunk 2 ──► Embedding Model ──► Vector
     ├── Chunk 3 ──► Embedding Model ──► Vector
     └── ...
     │
     ▼
     Vector Database
     */
}
