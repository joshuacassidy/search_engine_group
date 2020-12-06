package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class App {
    private static void indexDocuments() throws IOException {
        String ftLocation = "datasets/ft";
        String frLocation = "datasets/fr94";
        String fbisLocation = "datasets/fbis";
        String laTimesLocation = "datasets/latimes";

        String indexPath = "index";

        Analyzer analyzer = DocumentAnalyzer.getCustomAnalyzer();

        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        Directory indexSaveDirectory = FSDirectory.open(Paths.get(indexPath));
        IndexWriter indexWriter = new IndexWriter(indexSaveDirectory, indexWriterConfig);

        DocumentIndexer ftParser = new DocumentIndexer(ftLocation, indexPath, indexWriter,
                DocumentIndexerMaps.FT_MAP);
        ftParser.indexAllDocumentsInFolder();
        DocumentIndexer frParser = new DocumentIndexer(frLocation, indexPath, indexWriter,
                DocumentIndexerMaps.FR_MAP);
        frParser.indexAllDocumentsInFolder();
        DocumentIndexer fbisParser = new DocumentIndexer(fbisLocation, indexPath, indexWriter,
                DocumentIndexerMaps.FBIS_MAP);
        fbisParser.indexAllDocumentsInFolder();
        DocumentIndexer laTimesParser = new DocumentIndexer(laTimesLocation, indexPath, indexWriter,
                DocumentIndexerMaps.LA_TIMES_MAP);
        laTimesParser.indexAllDocumentsInFolder();

        analyzer.close();
        indexSaveDirectory.close();
        indexWriter.close();
    }

    public static void main( String[] args ) throws Exception
    {
        indexDocuments();

        String indexPath = "index";
        String stopwordsPath = null;
        
        String outputFile = "";
        String topicFile = "topics";

        SearchIndex searchIndex = new SearchIndex(indexPath, "0", stopwordsPath);
        searchIndex.searchQueryFile(topicFile, outputFile);
    }
}
