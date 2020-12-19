package com.tcd.search_engine_group_project;

import org.apache.commons.cli.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class App {
    private static void indexDocuments(String indexPath) throws IOException {
        String ftLocation = "datasets/ft";
        String frLocation = "datasets/fr94";
        String fbisLocation = "datasets/fbis";
        String laTimesLocation = "datasets/latimes";

        Analyzer analyzer = DocumentAnalyzer.getCustomAnalyzer();

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        Directory indexSaveDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexWriter indexWriter = new IndexWriter(indexSaveDirectory, indexWriterConfig);

        DocumentIndexer ftParser = new DocumentIndexer(ftLocation, indexWriter,
                DocumentIndexerMaps.FT_MAP);
        ftParser.indexAllDocumentsInFolder();
        DocumentIndexer frParser = new DocumentIndexer(frLocation, indexWriter,
                DocumentIndexerMaps.FR_MAP);
        frParser.indexAllDocumentsInFolder();
        DocumentIndexer fbisParser = new DocumentIndexer(fbisLocation, indexWriter,
                DocumentIndexerMaps.FBIS_MAP);
        fbisParser.indexAllDocumentsInFolder();
        DocumentIndexer laTimesParser = new DocumentIndexer(laTimesLocation, indexWriter,
                DocumentIndexerMaps.LA_TIMES_MAP);
        laTimesParser.indexAllDocumentsInFolder();

        analyzer.close();
        indexWriter.close();
        indexSaveDirectory.close();
    }
    
    private static void indexDocumentsNew(String indexPath, Analyzer analyzer) throws IOException {
        String ftLocation = "datasets/ft";
        String frLocation = "datasets/fr94";
        String fbisLocation = "datasets/fbis";
        String laTimesLocation = "datasets/latimes";

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        Directory indexSaveDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexWriter indexWriter = new IndexWriter(indexSaveDirectory, indexWriterConfig);

        NewDocumentIndexer ftParser = new NewDocumentIndexer(ftLocation, indexWriter,
                DocumentIndexerMaps.FT_MAP);
        ftParser.indexAllDocumentsInFolder();
        NewDocumentIndexer frParser = new NewDocumentIndexer(frLocation, indexWriter,
                DocumentIndexerMaps.FR_MAP);
        frParser.indexAllDocumentsInFolder();
        NewDocumentIndexer fbisParser = new NewDocumentIndexer(fbisLocation, indexWriter,
                DocumentIndexerMaps.FBIS_MAP);
        fbisParser.indexAllDocumentsInFolder();
        NewDocumentIndexer laTimesParser = new NewDocumentIndexer(laTimesLocation, indexWriter,
                DocumentIndexerMaps.LA_TIMES_MAP);
        laTimesParser.indexAllDocumentsInFolder();

        analyzer.close();
        indexWriter.close();
        indexSaveDirectory.close();
    }

    public static void main( String[] args ) throws Exception {
        String indexPath = "index";
        String topicFile = "topics";

        CommandLine cmd = buildCommandLineArguments(args);
        retrieveSimilarity(cmd);
        indexDocumentsNew(indexPath, retrieveAnalyzer(cmd));

        NewSearchIndex searchIndex = new NewSearchIndex(indexPath, retrieveAnalyzer(cmd),
                retrieveSimilarity(cmd));
        searchIndex.searchQueryFile(topicFile, retrieveOutputLocation(cmd));
    }

    private static String retrieveOutputLocation(CommandLine cmd) {
        if(cmd.getOptionValue("output_location") == null) {
            return "output/output.txt";
        }
        return cmd.getOptionValue("output_location");
    }

    private static Analyzer retrieveAnalyzer(CommandLine cmd) throws IOException {
        List<String> stopWords = Files.readAllLines(
                Paths.get(System.getProperty("user.dir") + "/resources/stop_words.txt")
        );

        if(cmd.getOptionValue("analyzer") == null) {
            System.out.println("Using Custom Analyzer");
            return new NewAnalyzer(stopWords);
        }

        switch(cmd.getOptionValue("analyzer").toLowerCase()) {
            case "standard":
                System.out.println("Using Standard Analyzer");
                return new StandardAnalyzer();
            case "simple":
                System.out.println("Using Simple Analyzer");
                return new SimpleAnalyzer();
            case "english":
                System.out.println("Using English Analyzer");
                return new EnglishAnalyzer();
            case "white-space":
                System.out.println("Using White Space Analyzer");
                return new WhitespaceAnalyzer();
            case "stop":
                System.out.println("Using Stop Analyzer");
                StringBuilder buffer = new StringBuilder();
                for(String current : stopWords) {
                    buffer.append(current).append("\n");
                }
                BufferedReader reader = new BufferedReader(new StringReader(buffer.toString()));
                return new StopAnalyzer(reader);
            case "custom":
                System.out.println("Using Custom Analyzer");
                return new NewAnalyzer(stopWords);
            default:
                throw new RuntimeException("Analyzer name is invalid");
        }
    }

    private static Similarity retrieveSimilarity(CommandLine cmd) {
        if(cmd.getOptionValue("similarity") == null) {
            System.out.println("Using BM25 Similarity");
            return new BM25Similarity();
        }

        switch(cmd.getOptionValue("similarity").toLowerCase()) {
            case "classic":
                System.out.println("Using Classic Similarity");
                return new ClassicSimilarity();
            case "boolean":
                System.out.println("Using Boolean Similarity");
                return new BooleanSimilarity();
            case "lmd":
                System.out.println("Using LMD Similarity");
                return new LMDirichletSimilarity();
            case "dfis":
                System.out.println("Using DFIS Similarity");
                return new DFISimilarity(new IndependenceChiSquared());
            case "bm25":
                System.out.println("Using BM25 Similarity");
                return new BM25Similarity();
            default:
                throw new RuntimeException("Similarity name is invalid");
        }
    }

    private static CommandLine buildCommandLineArguments(String[] args) throws ParseException {
        Options options = new Options();
        options.addOption("analyzer", true, "analyzer choice");
        options.addOption("similarity", true, "similarity choice");
        options.addOption("output_location", true, "output location");

        CommandLineParser parser = new DefaultParser();

        return parser.parse(options, args);
    }
}
