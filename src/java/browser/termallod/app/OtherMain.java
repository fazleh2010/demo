/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.app;

import browser.termallod.constants.SparqlEndpoint;
import browser.termallod.core.termbase.Termbase;
import browser.termallod.core.mysql.MySQLAccess;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.core.sparql.SparqlGenerator;
import browser.termallod.core.termbase.TermDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 */
public class OtherMain implements SparqlEndpoint {

    private static String path = "src/main/resources/";
    private static Integer limitOfTerms = -1;
    private static MySQLAccess mySQLAccess;

    public static void main(String[] args) throws Exception {

        Integer index = 1;

        String myTermTableName = "myTerminology", otherTermTableName = "otherTerminology", matchedTermTable = "link";
        String myTermSparqlEndpoint = null, otherTermSparqlEndpoint = null;

        System.out.println("called");
        System.out.println("arguments: " + args.length);
        if (args.length > 0) {
            myTermSparqlEndpoint = args[0];
            System.out.println("endpoint 1: " + args[0]);
        } else {
            System.err.println("no first endpoint in arguments");
            if (index == 1) {
                myTermSparqlEndpoint = endpoint_atc;
            }
        }
        if (args.length > 1) {
            otherTermSparqlEndpoint = args[1];
            System.out.println("endpoint 2: " + args[1]);
        } else {
            System.err.println("no second endpoint in arguments");
            otherTermSparqlEndpoint = endpoint_intaglio;
        }

        mySQLAccess = new MySQLAccess();
        
        System.out.println(myTermSparqlEndpoint);
         System.out.println(otherTermSparqlEndpoint);

        //my terminology
        /*System.out.println("Adding my terminology!!" + myTermSparqlEndpoint);
        Termbase myTerminology = new CurlSparqlQuery(myTermSparqlEndpoint, query_writtenRep, myTermTableName).getTermbase();
        addToDataBase(myTermTableName, myTerminology, limitOfTerms);

        //Link terminology
        System.out.println("Adding my other terminology!!" + otherTermTableName);
        Termbase otherTerminology = new CurlSparqlQuery(otherTermSparqlEndpoint, query_writtenRep, otherTermTableName).getTermbase();
        addToDataBase(otherTermTableName, otherTerminology, limitOfTerms);

        System.out.println("creating linking table!!");
        matchWithDataBase(myTermTableName, otherTerminology, matchedTermTable);

        System.out.println("inserting linked terminologies in host terminology!!");
        List<TermInfo> termList = matchWithDataBase(myTermTableName, otherTerminology, matchedTermTable);
        TermDetailNew.display(termList);
        /*SparqlGenerator sparqlGenerator = new SparqlGenerator(termList, ontolex_prefix, ontolex_owl_sameAs);
        CurlSparqlQuery curlSparqlQuery=new CurlSparqlQuery(myTermSparqlEndpoint,sparqlGenerator.getSparqlQuery());*/

        mySQLAccess.close();
        //System.out.println("MY terminology !!" + myTermSparqlEndpoint);
        //System.out.println("Other terminology!!" + otherTermSparqlEndpoint);
    }

    private static Boolean addToDataBase(String myTermTableName, Termbase myTerminology, Integer limitOfTerms) {
        try {
            mySQLAccess.deleteTable(myTermTableName);
            mySQLAccess.createTermTable(myTermTableName);
            mySQLAccess.insertDataTermTable(myTermTableName, myTerminology, limitOfTerms);
            mySQLAccess.readTermTable(myTermTableName);
        } catch (Exception ex) {
            Logger.getLogger(OtherMain.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;

    }

    private static List<TermDetail> matchWithDataBase(String myTermTable, Termbase otherTerminology, String matchedTermTable) {
        Integer index = 0;
        List<TermDetail> termInfos = new ArrayList<TermDetail>();
        try {
            mySQLAccess.deleteTable(matchedTermTable);
            mySQLAccess.createLinkingTable(matchedTermTable);
            index = mySQLAccess.insertDataTermTable(myTermTable, otherTerminology, matchedTermTable);
            termInfos = mySQLAccess.readMatchedTermTable(matchedTermTable);
            //display(matchedTermTable);
            System.out.println(matchedTermTable + "  number of matched found:  " + index);
        } catch (Exception ex) {
            Logger.getLogger(OtherMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        return termInfos;

    }

    /*private static Termbase getTermBaseFromTxtFiles(String termBaseName, String path, String extension) throws Exception {
        //System.out.println(termBaseName+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        File[] myTerminologyfiles = FileUrlUtils.getFiles(path, extension);
        Map<String, TermDetailNew> allkeysValues = new HashMap<String, TermDetailNew>();
        for (File file : myTerminologyfiles) {
            //System.out.println(file.getAbsolutePath());
            Map<String, TermDetailNew> terms = new HashMap<String, TermDetailNew>();
            terms = FileUrlUtils.getHashFromFile(file);
            allkeysValues.putAll(terms);
        }
        Termbase termbase = new Termbase(termBaseName, allkeysValues);
        return termbase;
    }*/
}
