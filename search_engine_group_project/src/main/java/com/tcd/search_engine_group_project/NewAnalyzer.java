package com.tcd.search_engine_group_project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;


public class NewAnalyzer extends StopwordAnalyzerBase {
    List<String> stopWords;

    public NewAnalyzer(List<String> stopWords) {
        this.stopWords = stopWords;
    }

    @Override
	protected TokenStreamComponents createComponents(String s) {
            Tokenizer tokenizer = new StandardTokenizer();
            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            tokenStream = new TrimFilter(tokenStream);
            tokenStream = new EnglishPossessiveFilter(tokenStream);
            tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(stopWords,true));

            try {
                BufferedReader countries = new BufferedReader(
                        new FileReader("/Users/owner/Desktop/search_engine_group-master/search_engine_group/search_engine_group_project/wordnet/custom_syns_py_sample.txt")
                );

                SynonymMap.Builder builder = new SynonymMap.Builder(true);

                for(String country = countries.readLine(); country != null; country = countries.readLine()) {
                    String[] syns = country.split(":");
                    for(int i = 1; i < syns.length; i++) {
                        builder.add(new CharsRef(syns[0]), new CharsRef(syns[i]), true);
                    }
                }

                SynonymMap synMap = builder.build();
                tokenStream = new FlattenGraphFilter(
                new WordDelimiterGraphFilter(
                        tokenStream,
                        WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS,
                        null
                    )
                );
                tokenStream = new FlattenGraphFilter(new SynonymGraphFilter(tokenStream, synMap, true));
            } catch (Exception e) {
                System.out.println("when generating syn map");
            }

//            tokenStream = new NGramTokenFilter(tokenStream, 1, 2, false);
            tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());

            return new TokenStreamComponents(tokenizer, tokenStream);
	}

}
