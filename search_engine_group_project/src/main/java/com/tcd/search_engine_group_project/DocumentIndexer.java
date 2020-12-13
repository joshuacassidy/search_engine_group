package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Map;

public class DocumentIndexer {
    private Path documentsDirectory;
    private IndexWriter indexWriter;
    private Map<String, String> documentFieldsMap;

    public DocumentIndexer(String documentsDirectory,
                           String indexFolder,
                           IndexWriter indexWriter,
                           Map<String, String> documentFieldsMap) {
        this.documentsDirectory = Paths.get(documentsDirectory);

        if(!Files.isDirectory(this.documentsDirectory)) {
            throw new RuntimeException("Path " + indexFolder +" is not a Directory");
        }

        this.indexWriter = indexWriter;
        this.documentFieldsMap = documentFieldsMap;
    }

    private void indexDocument(Path file) throws IOException {
        File input = file.toFile();
        Document htmlDoc = Jsoup.parse(input, "UTF-8");
        Elements links = htmlDoc.select("DOC");

        for (Element link : links) {

            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
            for(String documentField : documentFieldsMap.keySet()) {
                
                // String[] words = link.select(
                //                 documentFieldsMap.get(documentField)
                //                 ).text().split(" ");
                
                // int N=2000; // NUMBER OF WORDS THAT YOU NEED
                // String nWords="";

                // // concatenating number of words that you required
                // for(int i=0; i<Math.min(N, words.length); i++){
                //      nWords = nWords + " " + words[i] ;         
                // }
                
                Field textField = new TextField(documentField, link.select(
                    documentFieldsMap.get(documentField)
                    ).text(), Field.Store.YES);
                document.add(textField);
            }
            
            indexWriter.addDocument(document);
        }
    }

    public void indexAllDocumentsInFolder() throws IOException {
        SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path document, BasicFileAttributes attrs) throws IOException {
                if(document.getFileName().toString().endsWith(".txt") ||
                        document.getFileName().toString().equals("readchg") ||
                        document.getFileName().toString().equals("readmeft") ||
                        document.getFileName().toString().equals("readmefr") ||
                        document.getFileName().toString().equals("readfrcg"))
                    return FileVisitResult.CONTINUE;
                indexDocument(document);
                return FileVisitResult.CONTINUE;
            }
        };

        System.out.println("Indexing doc no: " + this.documentsDirectory);

        Files.walkFileTree(this.documentsDirectory, fileVisitor);
    }
}
