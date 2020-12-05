package com.tcd.search_engine_group_project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class App
{
    public static void main( String[] args ) throws Exception
    {

        String dataPath = "datasets/Assignment Two/fr94/01";
        String indexPath = "index";
        boolean createIndex = true;
        String stopwordsPath = null;

        FederalRegister federalRegister = new FederalRegister(dataPath, indexPath, createIndex, stopwordsPath);
        
        if(dataPath != null && indexPath != null) {
            federalRegister.index();
            System.out.println("Completed indexing documents storing found in " + dataPath + " in " + indexPath);
        }


    }
}
