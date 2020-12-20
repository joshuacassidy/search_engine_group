package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
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
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;

public class DocumentAnalyzer {
    public static Analyzer getCustomAnalyzer() throws IOException {
        String stopwordsFolder = "resources/";
        String stopwordsFile = "stop_words.txt";
        Map<String, String> sargs = new HashMap<>();
        sargs.put("ignoreCase", "true");
        sargs.put("synonyms", "/Users/owner/Desktop/search_engine_group/search_engine_group_project/prolog/wn_s.pl");
        sargs.put("format", "wordnet");

        Set<String> stopWords;
        try (Stream<String> lines = Files.lines(Paths.get(stopwordsFolder + stopwordsFile))) {
            stopWords = new HashSet<>(lines.collect(Collectors.toList()));
        }
        CharArraySet stopWordsSet = CharArraySet.copy(stopWords);

        return CustomAnalyzer.builder(Paths.get(stopwordsFolder))
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
                    .addTokenFilter(SynonymGraphFilterFactory.class, sargs)
                    .addTokenFilter("snowballPorter")
                    .build();
    }

    public static Analyzer getCustomAnalyzerShingle() throws IOException {
        return new ShingleAnalyzerWrapper(getCustomAnalyzer());
    }
}
