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


public class NewAnalyzer extends StopwordAnalyzerBase {

	@Override
	protected TokenStreamComponents createComponents(String s) {
            final Tokenizer tokenizer = new StandardTokenizer();
            TokenStream tokenStream = new LowerCaseFilter(tokenizer);
            tokenStream = new TrimFilter(tokenStream);
            tokenStream = new EnglishPossessiveFilter(tokenStream);
            try {
                List<String> lines = Files.readAllLines(
                        Paths.get(System.getProperty("user.dir") + "/resources/stop_words.txt")
                );
                tokenStream = new StopFilter(tokenStream, StopFilter.makeStopSet(lines,true));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            

//            tokenStream = new NGramTokenFilter(tokenStream, 1, 2, false);
            tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());

            return new TokenStreamComponents(tokenizer, tokenStream);
	}

}
