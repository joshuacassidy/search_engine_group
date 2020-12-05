package com.tcd.search_engine_group_project;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

//import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;


public class FederalRegister implements IDocumentParser {
    
    private Path documentsDirectory;
    private boolean createIndex;
    private Analyzer analyzer;
    private IndexWriterConfig indexWriterConfig;
    private String indexPath;
    private IndexWriter writer;
    private String stopwordsPath;


    public FederalRegister(String dataPath, String indexPath, boolean createIndex, String stopwordsPath) throws IOException {
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
        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
        String doc = "";
        for(String line = reader.readLine(); line != null; line = reader.readLine()) {
            doc += line;
        }

        Document htmldoc = Jsoup.parse(doc);

        Elements links = htmldoc.select("DOC > TEXT");
        for (Element link : links) {
            String linkText = link.text();
//            System.out.println(link);
//            System.out.println(linkText);
            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
            Field textField = new TextField("TEXT", linkText, Field.Store.YES);
            document.add(textField);
            // Field textField = new TextField("TEXT", linkText, Field.Store.YES);
            // document.add(textField);

            writer.addDocument(document);
        }

        
        reader.close();

    }



   
}

