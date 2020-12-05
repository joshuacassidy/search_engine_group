package com.tcd.search_engine_group_project;

import com.alibaba.fastjson.JSON;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FbisParser implements DocumentParser<DocumentFbis> {

	public ArrayList<DocumentFbis> readFile(String filePath) throws Exception {

		ArrayList<DocumentFbis> ParsedArr = new ArrayList<>();


		// read file
		File file = new File(filePath);

		Document Doc = Jsoup.parse(file, "UTF-8");
		Elements elements = Doc.getElementsByTag("DOC");

		for (Element element : elements) {
			DocumentFbis DocFbis = new DocumentFbis();

			try {
				// take element one bv one, after extracting the content: replace them with empty strings!

				// basic doc fields

				Elements DOCNO = element.getElementsByTag("DOCNO");
                DocFbis.docNo = Entities.unescape(DOCNO.get(0).text().trim());
				DOCNO.get(0).text("");

				Elements TEXT = element.getElementsByTag("TEXT");
                DocFbis.text = parse(Entities.unescape(TEXT.get(0).text().trim()));
				TEXT.get(0).text("");

				// fbis custom fields

				Elements HT = element.getElementsByTag("HT");
				if (HT.size() > 0) {
                    DocFbis.ht = parse(Entities.unescape(HT.get(0).text().trim()));
					HT.get(0).text("");
				}

				Elements Date1 = element.getElementsByTag("DATE1");
				if (Date1.size() > 0) {
                    DocFbis.date1 = parse(Entities.unescape(Date1.get(0).text().trim()));
					Date1.get(0).text("");
				}

				Elements TI = element.getElementsByTag("TI");
				if (TI.size() > 0) {
                    DocFbis.title = parse(Entities.unescape(TI.get(0).text().trim()));
					TI.get(0).text("");
				}

				Elements HEADER = element.getElementsByTag("HEADER");
				if (HEADER.size() > 0) {
                    DocFbis.header =parse(Entities.unescape(HEADER.get(0).text().trim()));
					HEADER.get(0).text("");
				}

				// finally the rest things goes into meta
                DocFbis.meta = parse(Entities.unescape(element.text().trim()));

//              System.out.println(JSON.toJSONString(docFbis));

                ParsedArr.add(DocFbis);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(JSON.toJSONString(DocFbis));
				throw e;
			}
		}

		return ParsedArr;
	}

	public ArrayList<org.apache.lucene.document.Document> toLucDoc(ArrayList<DocumentFbis> Array) {
		ArrayList<org.apache.lucene.document.Document> ParsedArr = new ArrayList<>();

		for (DocumentFbis DocFbis : Array) {
			org.apache.lucene.document.Document ParsedDoc = new org.apache.lucene.document.Document();

            ParsedDoc.add(new StringField("DOCNO", DocFbis.docNo, Field.Store.YES));
			if (!Objects.isNull(DocFbis.text)) {
                ParsedDoc.add(new TextField("TEXT", DocFbis.text, Field.Store.YES));
			}
            ParsedDoc.add(new TextField("META", DocFbis.meta, Field.Store.YES));

			// fbis custom fields

			if (!Objects.isNull(DocFbis.date1)) {
                ParsedDoc.add(new StringField("DATE", DocFbis.date1, Field.Store.YES)); // date1
			}
			if (!Objects.isNull(DocFbis.title)) {
                ParsedDoc.add(new TextField("HEADLINE", DocFbis.title, Field.Store.YES));  // ti
			}
			if (!Objects.isNull(DocFbis.ht)) {
                ParsedDoc.add(new StringField("HT", DocFbis.ht, Field.Store.YES));
			}
			if (!Objects.isNull(DocFbis.header)) {
                ParsedDoc.add(new StringField("HEADER", DocFbis.header, Field.Store.YES));
			}

			ParsedArr.add(ParsedDoc);
		}

		return ParsedArr;
	}

    public static String parse(String input) {
        return input.replaceAll("&hyph;", "-");
    }
}

class DocumentFbis extends DocumentBasic {

    public String ht;

    public String date1;

    public String title;

    public String header;

}

class DocumentBasic {

    public String docNo;

    public String text;

    public String meta;

}

