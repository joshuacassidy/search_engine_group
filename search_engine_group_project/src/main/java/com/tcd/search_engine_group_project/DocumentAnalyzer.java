package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory;
import java.util.*;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.analysis.synonym.SynonymFilterFactory;

public class DocumentAnalyzer {
    public static Analyzer getCustomAnalyzer() throws IOException {
        String stopwordsFolder = "resources/";
        String stopwordsFile = "stop_words.txt";
        Map<String, String> sargs = new HashMap<>();
        
        // sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/google_syns.txt");
        // sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/syns.txt");
        sargs.put("ignoreCase", "true");
        
        sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/search_engine_group_project/prolog/wn_s.pl");
        sargs.put("format", "wordnet");

        return CustomAnalyzer.builder(Paths.get(stopwordsFolder))
                // .withTokenizer(
                //     NGramTokenizerFactory.class, 
                //     new String[] { 
                //         "minGramSize", "1", 
                //         "maxGramSize", "3" 
                //     }
                //     )
                .withTokenizer("standard")
                    .addTokenFilter(EnglishPossessiveFilterFactory.class)
                    .addTokenFilter("trim")
                    .addTokenFilter("lowercase")
                    .addTokenFilter("stop", "ignoreCase", "true", "words", stopwordsFile, "format", "wordset")
                    .addTokenFilter("patternReplace",
                            "pattern", "[^A-Za-z0-9\\s]+",
                            "replace", "all",
                            "replacement", ""
                    )
                    .addTokenFilter(SynonymFilterFactory.class, sargs)
                    // .addTokenFilter(SynonymGraphFilterFactory.class, sargs)
                    .addTokenFilter("snowballPorter") 
                    .build();
    }
}
