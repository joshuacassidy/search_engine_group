package com.tcd.search_engine_group_project;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.tartarus.snowball.ext.EnglishStemmer;

import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;


public class CustomDocumentAnalyzer extends StopwordAnalyzerBase {
    List<String> stopWords;
    SynonymMap synonymMap;

    public CustomDocumentAnalyzer(List<String> stopWords, SynonymMap synonymMap) {
        this.stopWords = stopWords;
        this.synonymMap = synonymMap;
    }

    @Override
	protected TokenStreamComponents createComponents(String s) {
            Tokenizer tokenizer = new StandardTokenizer();

            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            tokenStream = new TrimFilter(tokenStream);
            tokenStream = new EnglishPossessiveFilter(tokenStream);
            tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopWords,true));
            tokenStream = new FlattenGraphFilter(
                new WordDelimiterGraphFilter(
                    tokenStream,
                    WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS,
                    null
                )
            );
            tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, synonymMap, true));
            tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());

            return new TokenStreamComponents(tokenizer, tokenStream);
	}

	public static SynonymMap createSynonymMap(String synonymsFileLocation) throws IOException {
        BufferedReader synonyms = new BufferedReader(
                new FileReader(System.getProperty("user.dir") + synonymsFileLocation)
        );

        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        for(String synonym = synonyms.readLine(); synonym != null; synonym = synonyms.readLine()) {
            String[] syns = synonym.split(":");
            for(int i = 1; i < syns.length; i++) {
                if(syns[i].equals("") || syns[0].equals(""))
                    continue;
                builder.add(new CharsRef(syns[0]), new CharsRef(syns[i]), true);
            }
        }

        return builder.build();
    }

    public static Analyzer getPrincetonAnalyzer() throws IOException {
        String stopwordsFolder = "resources/";
        String stopwordsFile = "stop_words.txt";
        Map<String, String> sargs = new HashMap<>();
        sargs.put("ignoreCase", "true");
        sargs.put("synonyms", System.getProperty("user.dir") + "/prolog/wn_s.pl");
        sargs.put("format", "wordnet");

        return org.apache.lucene.analysis.custom.CustomAnalyzer.builder(Paths.get(stopwordsFolder))
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
}
