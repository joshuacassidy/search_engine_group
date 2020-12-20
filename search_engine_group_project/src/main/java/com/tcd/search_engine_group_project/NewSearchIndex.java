package com.tcd.search_engine_group_project;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class NewSearchIndex {

    private Analyzer analyzer;
    private String indexPath;
    private Similarity similarity;
    private Map<String, Float> documentCategoryScores;


    public NewSearchIndex(String indexPath, Analyzer analyzer, Similarity similarity) {
        this.indexPath = indexPath;
        documentCategoryScores = new HashMap<>();
        documentCategoryScores.put("title", 0.1f);
        documentCategoryScores.put("text", 1.0f);

        this.analyzer = analyzer;
        this.similarity = similarity;
    }

    public void searchQueryFile(String queriesFile, String output) throws ParseException {
        try {
            FileWriter resultsFileWriter = new FileWriter(Paths.get(output).toString());
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarity);

            
            QueryParser queryParser = new MultiFieldQueryParser(new String[]{"title", "text"}, analyzer, documentCategoryScores);

            int count = 0;
            Document htmldoc = Jsoup.parse(new File(queriesFile), "UTF-8");
            Elements links = htmldoc.select("top");
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
                
                
                Query titleQuery = queryParser.parse(QueryParser.escape(title));
                Query descriptionQuery = queryParser.parse( QueryParser.escape(body));

                BooleanQuery booleanQuery = new BooleanQuery.Builder()
                        .add(new BoostQuery(titleQuery, 4.1f), BooleanClause.Occur.SHOULD)
                        .add(new BoostQuery(descriptionQuery, 1.75f), BooleanClause.Occur.SHOULD)
                        .build();


                ScoreDoc[] results = indexSearcher.search(booleanQuery, 1500).scoreDocs;

                for (int i = 0; i < results.length; i++) {
                    String documentId = indexSearcher.doc(results[i].doc).get("id");

                     resultsFileWriter.write(
                             queryNumber + 
                             " 0 " + 
                             documentId + 
                             " " +
                             i + 
                             " " + 
                             results[i].score + 
                             " Any\n"
                     );
                }
            }
            
            indexReader.close();

            resultsFileWriter.close();


        } catch (IOException e) {
            System.out.println("cant write to file");
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
