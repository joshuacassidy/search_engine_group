package com.tcd.search_engine_group_project;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.jsoup.nodes.Document;

public class SearchIndex {

    private Analyzer analyzer;
    private String indexPath;
    private Similarity similarity;
    private HashMap<String, Float> documentCategoryScores;


    public SearchIndex(String indexPath) {
        this.indexPath = indexPath;
        documentCategoryScores = new HashMap<>();
        documentCategoryScores.put("title", 0.1f);
        documentCategoryScores.put("text", 1f);
        similarity = new BM25Similarity();
    }

    private void writeQuery(String queryText, IndexSearcher indexSearcher, MultiFieldQueryParser parser, String queryNumber, FileWriter resultsFileWriter) throws Exception {
        Query query = parser.parse(QueryParser.escape(queryText.trim()));
        TopDocs results = indexSearcher.search(query, 10000);
        final Map<org.apache.lucene.document.Document, Float> scores = new HashMap<>();

        List<org.apache.lucene.document.Document> documents = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < Math.min(results.totalHits.value, 2000); i++) {
            org.apache.lucene.document.Document doc = indexSearcher.doc(results.scoreDocs[i].doc);
            documents.add(doc);
            texts.add(doc.get("text"));
            scores.put(doc, results.scoreDocs[i].score);
        }

        PythonAPIManager pythonAPIManager = new PythonAPIManager();

        // Calculating doc2Vec scores
        //List<Float> doc2VecScores = pythonAPIManager.scoreTextsWithDoc2Vec(queryText, texts);
        for (int i = 0; i < Math.min(results.totalHits.value, 2000); i++) {
            org.apache.lucene.document.Document doc = documents.get(i);
            float doc2VecScore = 0;//20 * doc2VecScores.get(i);
            scores.put(doc, scores.get(doc) * 0.8f + doc2VecScore);
        }

        documents.sort(Comparator.comparing(scores::get).reversed());

        for(int i=0; i<1000; i++) {
            org.apache.lucene.document.Document doc = documents.get(i);
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
            

            int count = 0;
            Document htmldoc = Jsoup.parse(new File(queriesFile), "UTF-8");
            Elements links = htmldoc.select("top");
            for (Element link : links) {
                System.out.println("Searching doc no" + count);
                String title = link.select("title").text();
                String body = link.select("narr").text() + link.select("desc").text();
                String queryNumber = link.select("num").first().text().replace("Number: ", "").split(" ")[0];
                String query = "text:" + (body + title) + " OR title:" + title;
                writeQuery(query, indexSearcher, parser, queryNumber, resultsFileWriter);
                count++;
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
