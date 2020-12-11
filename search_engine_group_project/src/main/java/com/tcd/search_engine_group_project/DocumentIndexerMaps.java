package com.tcd.search_engine_group_project;

import java.util.HashMap;
import java.util.Map;

public class DocumentIndexerMaps {
    public static final Map<String, String> FT_MAP = new HashMap<String, String>() {{
        put("id", "DOCNO");
        put("title", "headline");
        put("text", "text");
    }};

    public static final Map<String, String> FR_MAP = new HashMap<String, String>() {{
        put("id", "DOCNO");
        put("title", "title");
        put("text", "text");
    }};

    public static final Map<String, String> FBIS_MAP = new HashMap<String, String>() {{
        put("id", "DOCNO");
        put("title", "ti");
        put("text", "text");
    }};

    public static final Map<String, String> LA_TIMES_MAP = new HashMap<String, String>() {{
        put("id", "DOCNO");
        put("title", "headline");
        put("text", "text");
    }};
}
