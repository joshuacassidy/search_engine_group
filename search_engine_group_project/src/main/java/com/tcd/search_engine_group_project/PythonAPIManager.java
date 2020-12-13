package com.tcd.search_engine_group_project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.nio.charset.StandardCharsets.*;

public class PythonAPIManager {
    public PythonAPIManager() {
    }

    public static String getHTML(String urlStr, String bodyOriginal) throws Exception {
        byte[] ptext = bodyOriginal.getBytes(ISO_8859_1);
        String body = new String(ptext, UTF_8);

        HttpClient client = HttpClients.createDefault();
        HttpPost request = new HttpPost(urlStr);

        StringEntity entity = new StringEntity(body);
        entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        request.setEntity(entity);
        request.setHeader("Content-Type", "application/json");

        HttpResponse response = client.execute(request);
        BufferedReader stream = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuilder responseText = new StringBuilder();

        String line;
        while((line = stream.readLine()) != null) {
            responseText.append(line);
        }

        return responseText.toString();
    }

    public List<Float> scoreTextsWithDoc2Vec(String queryText, List<String> texts) throws Exception {
        List<Float> result = new ArrayList<>();

        /*for(int i=0; i<texts.size(); i++) {
            String body = createDoc2VecBody(queryText, Collections.singletonList(texts.get(i)));
            String response = getHTML("http://localhost:5000/doc2vec", body);
            result.addAll(extractDoc2VecValues(response));
        }*/

        String body = createDoc2VecBody(queryText, texts);
        String response = getHTML("http://localhost:5000/doc2vec", body);
        result.addAll(extractDoc2VecValues(response));

        return result;
    }

    private List<Float> extractDoc2VecValues(String response) throws JsonProcessingException {
        List<Float> doc2VecResult = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(response);

        ArrayNode jsonArrray = (ArrayNode) actualObj.get("values");

        for (int i=0; i<jsonArrray.size(); i++) {
            float value = Float.parseFloat(jsonArrray.get(i).toString());
            doc2VecResult.add(value);
        }

        return doc2VecResult;
    }

    private String createDoc2VecBody(String queryText, List<String> texts) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("query_text", queryText);

        ArrayNode jsonTexts = mapper.createArrayNode();
        for(int i=0; i<texts.size(); i++) {
            ObjectNode documentText = mapper.createObjectNode();
            documentText.put("doc_text", texts.get(i));
            jsonTexts.add(documentText);
        }

        root.set("document_texts", jsonTexts);

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
    }
}
