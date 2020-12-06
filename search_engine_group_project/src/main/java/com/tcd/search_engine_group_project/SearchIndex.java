package com.tcd.search_engine_group_project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.AxiomaticF3LOG;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BooleanSimilarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.DFISimilarity;
import org.apache.lucene.search.similarities.IndependenceChiSquared;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.jsoup.nodes.Document;

public class SearchIndex {

    private Analyzer analyzer;
    private String indexPath;
    private Similarity similarity;
    private HashMap<String, Float> documentCategoryScores;


    public SearchIndex(String indexPath) {
        this.indexPath = indexPath;
        documentCategoryScores = new HashMap<String, Float>();
        documentCategoryScores.put("title", 2f);
        documentCategoryScores.put("text", 1f);
        similarity = new BM25Similarity();
    }

    private void writeQuery(String queryText, IndexSearcher indexSearcher, MultiFieldQueryParser parser, String queryNumber, FileWriter resultsFileWriter) throws Exception {
        Query query = parser.parse(QueryParser.escape(queryText.trim()));
        TopDocs results = indexSearcher.search(query, 1000);
        for (int i = 0; i < Math.min(results.totalHits.value, 1000); i++) {
            org.apache.lucene.document.Document doc = indexSearcher.doc(results.scoreDocs[i].doc);
            String path = doc.get("id");

            if (path != null) {
                resultsFileWriter.write(queryNumber +" 0 " + path + " " + (i+1) + " " + results.scoreDocs[i].score + " Any\n");
            }
        }

    }

    public void searchQueryFile(String queriesFile, String output) {

        try {
            this.analyzer = DocumentAnalyzer.getCustomAnalyzer();
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);
            
            MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    new String[]{"title", "text"},
                    analyzer, documentCategoryScores);

            FileWriter resultsFileWriter = new FileWriter(Paths.get(output).toString());
            


            Document htmldoc = Jsoup.parse(new File(queriesFile), "UTF-8");
            Elements links = htmldoc.select("top");
            for (Element link : links) {
                org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
                String title = link.select("title").text();
                String body = link.select("narr").text();
                String queryNumber = link.select("num").first().text().replace("Number: ", "").split(" ")[0];
                String query = "text:" + body + " OR title:" + title;
                writeQuery(query, indexSearcher, parser, queryNumber, resultsFileWriter);
            }

            resultsFileWriter.close();
            indexReader.close();
            analyzer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    
    }

}