/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tcd.search_engine_group_project;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;


public class NewDocumentIndexer {
    private Path documentsDirectory;
    private IndexWriter indexWriter;
    private Map<String, List<String>> documentFieldsMap;

    public NewDocumentIndexer(String documentsDirectory,
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

            if(file.toString().contains("fr94")){
                link.select("ADDRESS").remove();
                link.select("SIGNER").remove();
                link.select("SIGNJOB").remove();
                link.select("BILLING").remove();
                link.select("FRFILING").remove();
                link.select("DATE").remove();
                link.select("RINDOCK").remove();
                link.select("usbureau").remove();
                link.select("agency").remove();
                link.select("further").remove();
                link.select("usdept").remove();
                link.select("table").remove();
                link.select("footnote").remove();
                link.select("footcite").remove();

                for(Element e : link.select("TEXT").first().getAllElements()) {
                    for(int i = 0; i < e.childNodes().size(); i++) {
                        Node n = e.childNodes().get(i);
                        if(n instanceof Comment){
                            n.remove();
                        } 
                    }
                }

            }

            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
            for(String documentField : documentFieldsMap.keySet()) {
                
                Field textField = new TextField(documentField, link.select(
                    documentFieldsMap.get(documentField).get(0)
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

