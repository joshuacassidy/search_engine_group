package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.lucene.analysis.shingle.ShingleAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

public class DocumentAnalyzer {
    public static Analyzer getCustomAnalyzer() throws IOException {
        String stopwordsFolder = "resources/";
        String stopwordsFile = "stop_words.txt";
        Map<String, String> sargs = new HashMap<>();
        
        // sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/google_syns.txt");
        // sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/syns.txt");
        sargs.put("ignoreCase", "true");
        
        sargs.put("synonyms", System.getProperty("user.dir") + "/prolog/wn_s.pl");
        sargs.put("format", "wordnet");

        Set<String> stopWords;
        try (Stream<String> lines = Files.lines(Paths.get(stopwordsFolder + stopwordsFile))) {
            stopWords = new HashSet<>(lines.collect(Collectors.toList()));
        }
        CharArraySet stopWordsSet = CharArraySet.copy(stopWords);

        return new StopwordAnalyzerBase() {
            @Override
            protected TokenStreamComponents createComponents(String field) {
                Tokenizer tokenizer = new StandardTokenizer();

                TokenStream filter = new TrimFilter(tokenizer);
                filter = new LowerCaseFilter(filter);
                filter = new StopFilter(filter, stopWordsSet);
                filter = new SnowballFilter(filter, new EnglishStemmer());
                filter = new LengthFilter(filter, 1, 15);
                filter = new RemoveDuplicatesTokenFilter(filter);

                return new TokenStreamComponents(tokenizer, filter);
            }
        };

        /*return CustomAnalyzer.builder(Paths.get(stopwordsFolder))
                .withTokenizer()
                // .withTokenizer(
                //     NGramTokenizerFactory.class,
                //     new String[] {
                //         "minGramSize", "1",
                //         "maxGramSize", "3"
                //     }
                //     )
                //.withTokenizer("standard")
                    .addTokenFilter(EnglishPossessiveFilterFactory.class)
                    .addTokenFilter("trim")
                    .addTokenFilter("lowercase")
                    .addTokenFilter("stop", "ignoreCase", "true", "words", stopwordsFile, "format", "wordset")
                    .addTokenFilter("patternReplace",
                            "pattern", "[^A-Za-z0-9\\s]+",
                            "replace", "all",
                            "replacement", ""
                    )
                    //.addTokenFilter(SynonymFilterFactory.class, sargs)
                    //.addTokenFilter(SynonymGraphFilterFactory.class, sargs)
                    .addTokenFilter("snowballPorter")
                    .build();*/
    }

    public static Analyzer getCustomAnalyzerShingle() throws IOException {
        return new ShingleAnalyzerWrapper(getCustomAnalyzer());
    }
}
