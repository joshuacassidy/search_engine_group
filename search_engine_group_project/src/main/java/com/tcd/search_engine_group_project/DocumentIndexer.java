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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentIndexer {
    private Path documentsDirectory;
    private IndexWriter indexWriter;
    private Map<String, List<String>> documentFieldsMap;

    public DocumentIndexer(String documentsDirectory,
                           IndexWriter indexWriter,
                           Map<String, List<String>> documentFieldsMap) {
        this.documentsDirectory = Paths.get(documentsDirectory);

        if(!Files.isDirectory(this.documentsDirectory)) {
            throw new RuntimeException("Path " + documentsDirectory + " is not a Directory");
        }

        this.indexWriter = indexWriter;
        this.documentFieldsMap = documentFieldsMap;
    }

    private void indexDocument(Path file) throws IOException {
        File input = file.toFile();
        Document htmlDoc = Jsoup.parse(input, "UTF-8");
        Elements links = htmlDoc.select("DOC");

        for (Element link : links) {
            examineDocument(link);
        }
    }

    private void examineDocument(Element doc) throws IOException {
        org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
        Map<String, String> luceneDocumentMapping = new HashMap<>();

        for(String luceneDocumentField : documentFieldsMap.keySet()) {
            List<String> textDocumentFields = documentFieldsMap.get(luceneDocumentField);

            for(String textDocField : textDocumentFields) {
                if(!luceneDocumentMapping.containsKey(luceneDocumentField)) {
                    luceneDocumentMapping.put(luceneDocumentField, "");
                }


                String result = luceneDocumentMapping.get(luceneDocumentField);
                result += doc.select(textDocField).text();
                luceneDocumentMapping.put(luceneDocumentField, result);
            }
        }

        for(String luceneDocumentField : documentFieldsMap.keySet()) {
            Field textField = new TextField(luceneDocumentField, luceneDocumentMapping
                    .get(luceneDocumentField), Field.Store.YES);
            document.add(textField);
        }

        indexWriter.addDocument(document);
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
