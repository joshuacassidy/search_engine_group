package com.tcd.search_engine_group_project;

import java.util.List;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class NewAnalyzer extends StopwordAnalyzerBase {
    List<String> stopWords;
    SynonymMap synonymMap;

    public NewAnalyzer(List<String> stopWords, SynonymMap synonymMap) {
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

	public static SynonymMap createSynonymMap() throws IOException {
        BufferedReader countries = new BufferedReader(
                new FileReader(System.getProperty("user.dir") + "/resources/syns.txt")
        );

        SynonymMap.Builder builder = new SynonymMap.Builder(true);

        for(String country = countries.readLine(); country != null; country = countries.readLine()) {
            String[] syns = country.split(":");
            for(int i = 1; i < syns.length; i++) {
                if(syns[i].equals(""))
                    continue;
                builder.add(new CharsRef(syns[0]), new CharsRef(syns[i]), true);
            }
        }

        return builder.build();
    }
}
