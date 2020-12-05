package com.tcd.search_engine_group_project;

public class App
{
    public static void main( String[] args ) throws Exception
    {
        String indexPath = "index";
        boolean createIndex = true;
        String stopwordsPath = null;

        String financialTimesLocation = "datasets/ft";
        String federalRegisterLocation = "datasets/fr94";
        String fbisLocation = "datasets/fbis";
        String laTimesLocation = "datasets/latimes";

        FinancialTimes financialTimes = new FinancialTimes(financialTimesLocation, indexPath, createIndex, stopwordsPath);
        FederalRegister federalRegister = new FederalRegister(federalRegisterLocation, indexPath, createIndex, stopwordsPath);
        ForeignBroadcastInformationService foreignBroadcastInformationService = new ForeignBroadcastInformationService(
                fbisLocation, indexPath, createIndex, stopwordsPath);
        LaTimes laTimes = new LaTimes(laTimesLocation, indexPath, createIndex, stopwordsPath);

        financialTimes.index();
        System.out.println("Completed indexing documents storing found in " + financialTimesLocation + " in " + indexPath);
        federalRegister.index();
        System.out.println("Completed indexing documents storing found in " + federalRegisterLocation + " in " + indexPath);
        foreignBroadcastInformationService.index();
        System.out.println("Completed indexing documents storing found in " + fbisLocation + " in " + indexPath);
        laTimes.index();
        System.out.println("Completed indexing documents storing found in " + laTimesLocation + " in " + indexPath);
    }
}
