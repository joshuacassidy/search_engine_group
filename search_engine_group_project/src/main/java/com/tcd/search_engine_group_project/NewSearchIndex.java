package com.tcd.search_engine_group_project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class NewSearchIndex {
    private boolean useWordFrequencies;
    private Analyzer analyzer;
    private String indexPath;
    private Similarity similarity;
    private Map<String, Float> documentCategoryScores;

    public NewSearchIndex(String indexPath, Analyzer analyzer, Similarity similarity, boolean useWordFrequencies) {
        this.indexPath = indexPath;
        documentCategoryScores = new HashMap<>();
        documentCategoryScores.put("title", 0.1f);
        documentCategoryScores.put("text", 1.0f);

        this.analyzer = analyzer;
        this.similarity = similarity;
        this.useWordFrequencies = useWordFrequencies;
    }

    public void searchQueryFile(String queriesFile, String output) throws Exception {
        FileWriter resultsFileWriter = new FileWriter(Paths.get(output).toString());
        IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        indexSearcher.setSimilarity(similarity);


        QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "text"}, analyzer, documentCategoryScores);

        int count = 0;
        Document htmlDoc = Jsoup.parse(new File(queriesFile), "UTF-8");
        Elements links = htmlDoc.select("top");

        for (Element link : links) {
            System.out.println("Searching doc no" + count);
            count++;
            String title = link.select("title").text().trim();
            String body = link.select("desc")
                    .text()
                    .trim()
                    .replace("Description: ", "")
                    .replace("Narrative:", "")
                    .replace("irrelevant", "not relevant");
            body = getRelevantNarrative(body);
            String queryNumber = link.select("num").first().text().replace("Number: ", "").split(" ")[0];

            executeQuery(resultsFileWriter, indexSearcher, queryParser, title, body, queryNumber);
        }

        indexReader.close();
        resultsFileWriter.close();
    }

    private void executeQuery(FileWriter resultsFileWriter, IndexSearcher indexSearcher, QueryParser queryParser, String title, String body, String queryNumber) throws Exception {
        int resultsToConsider = 2500;

        Query titleQuery = queryParser.parse(QueryParser.escape(title));
        Query descriptionQuery = queryParser.parse( QueryParser.escape(body));

        BooleanQuery booleanQuery = new BooleanQuery.Builder()
                .add(new BoostQuery(titleQuery, 4f), BooleanClause.Occur.SHOULD)
                .add(new BoostQuery(descriptionQuery, 1.7f), BooleanClause.Occur.SHOULD)
                .build();

        TopDocs results = indexSearcher.search(booleanQuery, resultsToConsider);

        final Map<org.apache.lucene.document.Document, Float> scores = new HashMap<>();

        List<org.apache.lucene.document.Document> documents = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < Math.min(results.totalHits.value, resultsToConsider); i++) {
            org.apache.lucene.document.Document doc = indexSearcher.doc(results.scoreDocs[i].doc);
            documents.add(doc);
            texts.add(doc.get("text"));
            scores.put(doc, results.scoreDocs[i].score);
        }

        PythonAPIManager pythonAPIManager = new PythonAPIManager();

        // Calculating doc2Vec scores
        /*List<Float> doc2VecScores = pythonAPIManager.scoreTextsWithDoc2Vec(queryText, texts);
        for (int i = 0; i < Math.min(results.totalHits.value, 2000); i++) {
            org.apache.lucene.document.Document doc = documents.get(i);
            float doc2VecScore = 20 * doc2VecScores.get(i);
            scores.put(doc, scores.get(doc) * 0.8f + doc2VecScore);
        }*/

        // Calculating Zipf scores
        if(useWordFrequencies) {
            List<Float> zipfScores = pythonAPIManager.scoreTextsWithZipf(body + title, texts);
            for(int i = 0; i < Math.min(results.totalHits.value, resultsToConsider); i++) {
                org.apache.lucene.document.Document doc = documents.get(i);
                float zipfScore = zipfScores.get(i);

                float zipfScore5 = Math.max((float) Math.log(zipfScore), 0);
                zipfScore5 = 0.00000175f * (float) Math.pow(zipfScore5, 5);

                float finalZipfScore = zipfScore5;

                scores.put(doc, scores.get(doc) + finalZipfScore);
            }
        }

        documents.sort(Comparator.comparing(scores::get).reversed());

        for(int i=0; i<1500; i++) {
            org.apache.lucene.document.Document doc = documents.get(i);
            String path = doc.get("id");

            if (path != null) {
                resultsFileWriter.write(queryNumber +" 0 " + path + " " + (i+1) + " " + results.scoreDocs[i].score + " Any\n");
            }
        }
    }


    private String getRelevantNarrative(String narrative) {
        StringBuilder relevantNarrative = new StringBuilder();

        BreakIterator bi = BreakIterator.getSentenceInstance();
        bi.setText(narrative);

        for (int i = 0; bi.next() != BreakIterator.DONE; i = bi.current()) {

            String sentence = narrative.substring(i, bi.current());
            if (!sentence.contains("unrelated") && !sentence.contains("not relevant") && !sentence.contains("irrelevant")) {
                relevantNarrative.append(sentence.toLowerCase().replaceAll(
                    "documents that discuss|the intent|of this query|find documents that discuss|a relevant document may|a relevant document provides|a relevant document identifies|a relevant document will|a relevant document must|a relevant document could|to be relevant|a document must|a document will|documents describing|relevant documents|relevant document|are relevant|are all relevant|relevant|related|will contain|will provide|must cite|will discuss|will report or discuss|contain|any discussion|could identify|provide information|information|specific|instance", ""));
            }
            
        }
        
        return relevantNarrative.toString();
    }
}
