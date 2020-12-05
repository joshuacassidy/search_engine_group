package com.tcd.search_engine_group_project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.StringField;


public class LaTimes implements IDocumentParser {

    private Path documentsDirectory;
    private boolean createIndex;
    private Analyzer analyzer;
    private IndexWriterConfig indexWriterConfig;
    private String indexPath;
    private IndexWriter writer;
    private String stopwordsPath;


    public LaTimes(String dataPath, String indexPath, boolean createIndex, String stopwordsPath) throws IOException {
        this.indexPath= indexPath;
        this.stopwordsPath= stopwordsPath;
        this.createIndex = createIndex;
        this.documentsDirectory = Paths.get(dataPath);
        this.analyzer =  CustomAnalyzer.builder(Paths.get(dataPath))
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                // .addTokenFilter("stop", "ignoreCase", "true", "words", file, "format", "wordset")
                .addTokenFilter("trim")
                .addTokenFilter("patternReplace",
                        "pattern", "^\\s\\.\\s$",
                        "replace", "all",
                        "replacement", " "
                )
                .addTokenFilter("snowballPorter")
                .build();

    }


    public void index() throws IOException {
        try {

            this.indexWriterConfig = new IndexWriterConfig(this.analyzer);
            if(this.createIndex) {
                this.indexWriterConfig.setOpenMode(OpenMode.CREATE);
            } else{
                this.indexWriterConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            Directory indexSaveDirectory = FSDirectory.open(Paths.get(this.indexPath));
            this.writer = new IndexWriter(indexSaveDirectory, indexWriterConfig);
            boolean isDirectory = Files.isDirectory(this.documentsDirectory);
            SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path document, BasicFileAttributes attrs) throws IOException {
                    try {
                        parseDocument(document);
                    } catch (Exception e) {}
                    return FileVisitResult.CONTINUE;
                }
            };
            if(isDirectory) {
                Files.walkFileTree(this.documentsDirectory, fileVisitor);
            } else {
                parseDocument(this.documentsDirectory);
            }

            this.writer.close();
            indexSaveDirectory.close();
            analyzer.close();
        } catch (IOException e) {
            System.out.println("Please specify a vaild collection of documents (can be a file or folder) using the using the -data and a location to store the index using the -index parameter");
            System.exit(1);
        }
    }


    @Override
    public void parseDocument(Path file) throws IOException {
        
        if(!("readfrcg".equals(file.getFileName().toString()) || "readmeft".equals(file.getFileName().toString()))) {
            BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
            File input = file.toFile();
            Document htmldoc = Jsoup.parse(input, "UTF-8");
            Elements links = htmldoc.select("DOC");
            for (Element link : links) {
                org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
                Field textField = new TextField("text", link.select("TEXT").text(), Field.Store.YES);
                document.add(textField);
                Field titleField = new TextField("title", link.select("HEADLINE").text(), Field.Store.YES);
                document.add(titleField);
                Field idField = new StringField("id", link.select("DOCNO").text(), Field.Store.YES);
                document.add(idField);
                
                writer.addDocument(document);
            }
            
            reader.close();
       }
        
//        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
//        StringBuilder doc = new StringBuilder();
//        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
//            doc.append(line);
//        }
//
//        Document htmldoc = Jsoup.parse(doc.toString());
//
//        Elements links = htmldoc.select("DOC > TEXT");
//        for (Element link : links) {
//            String linkText = link.text();
//            //System.out.println(linkText);
//            //System.out.println("\n\n\n\n\n");
//            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
//            Field field = new TextField("TEXT", linkText, Field.Store.YES);
//            document.add(field);
//
//            writer.addDocument(document);
//        }
//
//
//        reader.close();

    }




}

