package com.tcd.search_engine_group_project;

import java.io.IOException;
import java.nio.file.Path;

public interface IDocumentParser {
    void parseDocument(Path documentFolder) throws Exception;
}
