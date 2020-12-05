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
// import org.jsoup.nodes.Document;
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
    private Map<String, Similarity> scoringApproaches;
    private Similarity similarity;
    private HashMap<String, Float> documentCategoryScores;
    private String stopwordsPath;


    public SearchIndex(String indexPath, String scoringApproach, String stopwordsPath) {
        this.stopwordsPath= stopwordsPath;
        this.indexPath = indexPath;
        documentCategoryScores = new HashMap<String, Float>();
        documentCategoryScores.put("title", 5.7f);
        documentCategoryScores.put("text", 3.5f);
        // documentCategoryScores.put("Author", 0.5f);
        // documentCategoryScores.put("Bibliography", 0.3f);

        scoringApproaches = new HashMap<>();
        scoringApproaches.put("0", new BM25Similarity());
        scoringApproaches.put("1", new ClassicSimilarity());
        scoringApproaches.put("2", new LMDirichletSimilarity());
        scoringApproaches.put("3", new BooleanSimilarity());
        scoringApproaches.put("4", new LMJelinekMercerSimilarity(0.7f));
        scoringApproaches.put("5", new AxiomaticF3LOG(0.001f, 50));
        scoringApproaches.put("6", new DFISimilarity(new IndependenceChiSquared()));
        similarity = scoringApproaches.get("0");
        if(scoringApproaches.get(scoringApproach) != null) {
            similarity = scoringApproaches.get(scoringApproach);
        }
    }

    private void writeQuery(String queryText, IndexSearcher indexSearcher, MultiFieldQueryParser parser, int queryNumber, FileWriter resultsFileWriter) throws Exception {
        Query query = parser.parse(QueryParser.escape(queryText.trim()));
        TopDocs results = indexSearcher.search(query, 1000);
        ScoreDoc[] hits = results.scoreDocs;
        for (int j = 0; j < Math.min(results.totalHits.value, 1000); j++) {
            org.apache.lucene.document.Document doc = indexSearcher.doc(hits[j].doc);
            String path = doc.get("id");
             if (path != null) {
                 resultsFileWriter.write(queryNumber +" 0 " + path + " " + (j+1) + " " + hits[j].score + " Any\n");
             }
        }

    }

    public void searchQueryFile(String queriesFile, String output) {

        try {
            this.analyzer =  CustomAnalyzer.builder(
                // Paths.get(dataPath)
                )
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
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);
            
            MultiFieldQueryParser parser = new MultiFieldQueryParser(
                    new String[]{"title", "text"},
                    analyzer, documentCategoryScores);

            boolean isDirectory = Files.isDirectory(Paths.get(output));
            FileWriter resultsFileWriter;
            
            if(isDirectory) {
                resultsFileWriter = new FileWriter(Paths.get(output, "output.txt").toString());
            } else {
                resultsFileWriter = new FileWriter(Paths.get(output).toString());
            }
            
            


        Document htmldoc = Jsoup.parse(new File(queriesFile), "UTF-8");
        Elements links = htmldoc.select("top");
        System.out.println(htmldoc);
        int queryNumber = 1;
        for (Element link : links) {
            org.apache.lucene.document.Document document = new org.apache.lucene.document.Document();
            String title = link.select("title").text();
            String body = link.select("narr").text();
            
            String query = "text:" + body + " OR title:" + title;
            writeQuery(query, indexSearcher, parser, queryNumber, resultsFileWriter);
            queryNumber++;
            // System.out.println(link.select("desc"));

            // Field textField = new TextField("text", link.select("title").text(), Field.Store.YES);
            // document.add(textField);
            // Field titleField = new TextField("title", link.select("narr").text(), Field.Store.YES);
            // document.add(titleField);
            // Field idField = new StringField("id", link.select("desc").text(), Field.Store.YES);
            // document.add(idField);
            
            // writer.addDocument(document);
        }















            
    //         BufferedReader reader = Files.newBufferedReader(Paths.get(queriesFile), StandardCharsets.UTF_8);
    //         boolean firstQuery = true;
    //         String queryText = "";
    //         int queryNumber = 1;

    //         for(String line = reader.readLine(); line != null; line = reader.readLine()) {
    //             // if(line.startsWith(".I")) {
    //             //     if(!firstQuery) {
    //             //         // writeQuery(queryText, indexSearcher, parser, queryNumber, resultsFileWriter);
    //             //         queryNumber++;
    //             //     }
    //             //     queryText = "";
    //             //     firstQuery = false;
    //             //     continue;
    //             // }
    //             // if(line.startsWith(".W")) {
    //             //     continue;
    //             // }
    //             queryText += line;
    //         }
    //         // org.jsoup.nodes.Document doc = Jsoup.parse(queryText);
    //         // System.out.println(queryText);


    //         org.jsoup.nodes.Document htmldoc = Jsoup.parse(queryText);

    //         Elements links = htmldoc.select("top");
    //         for (Element link : links) {
    //             String linkText = link.text();
    // //            System.out.println(link);
    //            System.out.println(link.select("title").text());
    //            System.out.println(link.select("desc").text());
    //            break;
                
    //             // Field textField = new TextField("TEXT", linkText, Field.Store.YES);
    //             // document.add(textField);
    //         }



        //     writeQuery(queryText, indexSearcher, parser, queryNumber, resultsFileWriter);

            resultsFileWriter.close();
//            reader.close();
            indexReader.close();
            analyzer.close();
        } catch (Exception e) {
            System.out.println("Please specify a vaild queries and output using the -queriesFile and -output file parameters");
            System.exit(1);
        }
    
    }

}