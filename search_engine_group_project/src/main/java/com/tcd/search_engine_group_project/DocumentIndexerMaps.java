package com.tcd.search_engine_group_project;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentIndexerMaps {
    public static final Map<String, List<String>> FT_MAP = new HashMap<String, List<String>>() {{
        put("id", Arrays.asList("DOCNO"));
        put("title", Arrays.asList("headline"));
        put("text", Arrays.asList("text"));
    }};

    public static final Map<String, List<String>> FR_MAP = new HashMap<String, List<String>>() {{
        put("id", Arrays.asList("DOCNO"));
        put("title", Arrays.asList("title"));
        put("text", Arrays.asList("text"));
    }};

    public static final Map<String, List<String>> FBIS_MAP = new HashMap<String, List<String>>() {{
        put("id", Arrays.asList("DOCNO"));
        put("title", Arrays.asList("ti"));
        put("text", Arrays.asList("text", "header"));
    }};

    public static final Map<String, List<String>> LA_TIMES_MAP = new HashMap<String, List<String>>() {{
        put("id", Arrays.asList("DOCNO"));
        put("title", Arrays.asList("headline"));
        put("text", Arrays.asList("text", "graphic"));
    }};
}
