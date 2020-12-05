package com.tcd.search_engine_group_project;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;

public class DocumentAnalyzer {
    public static Analyzer getCustomAnalyzer() throws IOException {
        // CustomAnalyzer.builder(Paths.get(ftLocation))
        return CustomAnalyzer.builder()
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
    }
}
