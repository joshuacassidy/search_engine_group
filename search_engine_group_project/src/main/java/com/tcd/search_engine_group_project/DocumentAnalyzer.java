package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory;

public class DocumentAnalyzer {
    public static Analyzer getCustomAnalyzer() throws IOException {
        String stopwordsFolder = "resources/";
        String stopwordsFile = "stop_words.txt";

        return CustomAnalyzer.builder(Paths.get(stopwordsFolder))
                // .withTokenizer(
                //     NGramTokenizerFactory.class, 
                //     new String[] { 
                //         "minGramSize", "1", 
                //         "maxGramSize", "3" 
                //     }
                // )
                .withTokenizer("standard")
                .addTokenFilter(EnglishPossessiveFilterFactory.class)

                .addTokenFilter("trim")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop", "ignoreCase", "true", "words", stopwordsFile, "format", "wordset")
                .addTokenFilter("snowballPorter")
                .build();
    }
}
