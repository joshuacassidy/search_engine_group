package com.tcd.search_engine_group_project;

public class App
{
    public static void main( String[] args ) throws Exception
    {

        // // FederalRegister federalRegister = new FederalRegister(dataPath, indexPath, createIndex, stopwordsPath);
        
        // // if(dataPath != null && indexPath != null) {
        // //     federalRegister.index();
        // //     System.out.println("Completed indexing documents storing found in " + dataPath + " in " + indexPath);
        // // }

        
        // FinancialTimes financialTimes = new FinancialTimes("datasets/ft", indexPath, createIndex, stopwordsPath);
        // financialTimes.index();



        // // String queriesFile = "topics";
        // // String output = "output";
        // // String scoringApproach = null;
        
        // // SearchIndex searchIndex = new SearchIndex(indexPath, scoringApproach, stopwordsPath);
        // // searchIndex.searchQueryFile(queriesFile, output);
        // // System.out.println("Completed querying the index stored in " + indexPath + " using the  " + queriesFile + " queries file.\nThe results of these queries are stored in " + output);



        String indexPath = "index";
        boolean createIndex = true;
        String stopwordsPath = null;

        String financialTimesLocation = "datasets/ft";
        String federalRegisterLocation = "datasets/fr94";
        String fbisLocation = "datasets/fbis";
        String laTimesLocation = "datasets/latimes";
        
        String outputFile = "";
        String topicFile = "topics";
    //     FinancialTimes financialTimes = new FinancialTimes(financialTimesLocation, indexPath, createIndex, stopwordsPath);
    //     FederalRegister federalRegister = new FederalRegister(federalRegisterLocation, indexPath, false, stopwordsPath);
    //     ForeignBroadcastInformationService foreignBroadcastInformationService = new ForeignBroadcastInformationService(
    //             fbisLocation, indexPath, false, stopwordsPath);
    //     LaTimes laTimes = new LaTimes(laTimesLocation, indexPath, false, stopwordsPath);
    //    financialTimes.index();
    //    System.out.println("Completed indexing documents storing found in " + financialTimesLocation + " in " + indexPath);
    //    federalRegister.index();
    //    System.out.println("Completed indexing documents storing found in " + federalRegisterLocation + " in " + indexPath);
    //     foreignBroadcastInformationService.index();
    //    System.out.println("Completed indexing documents storing found in " + fbisLocation + " in " + indexPath);
       
    //    laTimes.index();
    //    System.out.println("Completed indexing documents storing found in " + laTimesLocation + " in " + indexPath);

            SearchIndex searchIndex = new SearchIndex(indexPath, "0", stopwordsPath);
            searchIndex.searchQueryFile(topicFile, outputFile);


    }
}
