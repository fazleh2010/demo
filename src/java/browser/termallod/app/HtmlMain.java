/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.app;

import browser.termallod.process.CreateAlphabetFiles;
import browser.termallod.core.html.HtmlCreator;
import browser.termallod.api.LanguageManager;
import browser.termallod.process.Matching;
import browser.termallod.constants.Parameter;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.constants.SparqlEndpoint;
import browser.termallod.constants.SparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class HtmlMain implements SparqlEndpoint {

    private static LanguageManager languageInfo;
   

    public HtmlMain() {
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, Exception {
        HtmlMain HtmlMain = new HtmlMain();
        HtmlMain.html(args);
    }

    public boolean html(String[] args) throws Exception {
        Parameter parameter = null;

        //String myTermTableName = "myTerminology";
        /*String myTermSparqlEndpoint = null, list = null;
        String htmltype = null,url=null,term=null;*/
        /*
         private static String ListOfTerms = "ListOfTerms";
         private static String TermPage = "TermPage";
         private static String MatchTerms = "MatchTerms";
        */
        
        //test list of terms
        //Parameter parameter = new Parameter(args,Parameter.ListOfTerms);
        //String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();
        
        //test termpage 
        /*parameter = new Parameter(args,Parameter.TermPage);
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();*/
        
         //test link term page
        parameter = new Parameter(args,Parameter.MatchTerms);
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();

        //use it when internal  test

        System.out.println("called");
        System.out.println("arguments: " + args.length);
        /*if (args.length > 1) {
            myTermSparqlEndpoint = args[1];
            //System.out.println("SparqlEndpoint: " + myTermSparqlEndpoint);
        } else {
            myTermSparqlEndpoint = endpoint_solar;
            System.err.println("no termDetailSparql endpoint in arguments");
        }
        if (args.length > 2) {
            OUTPUT_PATH = args[2];
            //System.out.println("OUTPUT_PATH: " + OUTPUT_PATH);
        } else {
            System.err.println("no output folder in arguments");

        }
        if (args.length > 3) {
            list = args[3];
            //System.out.println("list of terms taken from node.js: " + list);
        } else {
        }
        if (args.length > 4) {
            list = args[4];
            BASE_PATH = args[4] + BASE_PATH;
            //System.out.println("BASE_PATH: " + BASE_PATH);
        } else {
        }

        if (args.length > 5) {
            htmltype = args[5];
            //System.out.println("htmltype: " + htmltype);
        } else {
            htmltype = TermPage;
            //htmltype = ListOfTermPage;
        }
        if (args.length > 6) {
            TEMPLATE_PATH = args[6];
            //System.out.println("TEMPLATE_PATH: " + TEMPLATE_PATH);
        } else {
            TEMPLATE_PATH = BASE_PATH + "template/";
        }
         if (args.length > 7) {
            term = args[7];
            System.out.println("parameter url: " + url);
        } else {
            term ="hole";
        }

        INPUT_PATH = BASE_PATH + "input/";

        languageInfo = new LanguageAlphabetPro(new File(BASE_PATH + "/conf/" + "language.conf"));
         */

        System.out.println("reading terms");
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        HtmlCreator htmlCreator = new HtmlCreator(parameter.getTEMPLATE_PATH(), parameter.getOUTPUT_PATH());

        if (parameter.getHtmltype().contains(Parameter.ListOfTerms)) {
            Termbase myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint);
            System.out.println("saving terms");
            if (myTerminology.getTerms().isEmpty()) {
                return false;
            }

            CreateAlphabetFiles alphabetFiles = new CreateAlphabetFiles(parameter.getLanguageInfo(), myTerminology);

            //cleanDirectory();
            System.out.println("saving files");
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());
            System.out.println("creating html");

            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(),true);
        } else if (parameter.getHtmltype().contains(Parameter.TermPage)) {
            String termDetailSparql = SparqlQuery.getTermDetailSpqlByTerm(parameter.getTermDetail());
            TermDetail termDetail = curlSparqlQuery.findTermDetail(myTermSparqlEndpoint, termDetailSparql);
            String termLinkSparql = SparqlQuery.getTermLinks(termDetail.getTermUrl());
            System.out.println(termLinkSparql);
            curlSparqlQuery.findTermDetail(myTermSparqlEndpoint, termLinkSparql);
            /* made up links..
            Map<String, String> termLinks=new HashMap<String, String>();
            termLinks.put("solar", "http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_solarenergy/data/solarenergy/hole-EN");
            termDetail.setTermLinks(termLinks);*/
            htmlCreator.createHtmlTermPage(termDetail, parameter.getHtmltype());
        }
        else if (parameter.getHtmltype().contains(Parameter.MatchTerms)) {
            String jsonLangStr = "[{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/NL\"},"
                                + "\"entrycount\":{\"type\":\"typed-literal\","
                                + "\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"186\"}},"
                                + "{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/EN\"},"
                                + "\"entrycount\":{\"type\":\"typed-literal\",\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"19\"}}]";
            Matching mattchTerminologies=new Matching(parameter.getINPUT_PATH(), parameter.getOtherTermSparqlEndpoint(), jsonLangStr,parameter.getOtherTermTableName());
            /*for (String key : mattchTerminologies.getMatchedTermsInto().keySet()) {
                System.out.println(key);
                Set<TermDetail> matchedTerms = mattchTerminologies.getMatchedTermsInto().get(key);
                for (TermDetail termDetail : matchedTerms) {
                     System.out.println(termDetail);
                }
            }*/
            
            System.out.println(mattchTerminologies);
        }

        System.out.println("Processing iate finished!!!");
        return true;
    }

    private static void cleanDirectory(String INPUT_PATH, String OUTPUT_PATH) {
        try {
            FileRelatedUtils.deleteDirectory(INPUT_PATH);
            FileRelatedUtils.createDirectory(INPUT_PATH);
            FileRelatedUtils.deleteDirectory(OUTPUT_PATH);
            FileRelatedUtils.createDirectory(OUTPUT_PATH);
        } catch (IOException ex) {
            Logger.getLogger(HtmlMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        


    }
    
     
            /*Set<String> languages=new HashSet<String>();
            languages.add("en");
            languages.add("nl");
            TreeMap<String, TreeMap<String, List<String>>> langSortedTerms = new TreeMap<String, TreeMap<String, List<String>>>();
            for (String langCode : languages) {
                RetrieveAlphabetInfo retrieveAlphabetInfo = new RetrieveAlphabetInfo(parameter.getINPUT_PATH(), langCode);
                langSortedTerms.put(langCode,  retrieveAlphabetInfo.getLangSortedTerms());
            }
            for(String langCode:langSortedTerms.keySet()){
                TreeMap<String, List<String>> pairTerms=langSortedTerms.get(langCode);
                for(String pair:pairTerms.keySet()){
                    List<String> terms=pairTerms.get(pair);
                    System.out.println(pair);
                     System.out.println(terms);
                }
            }
              //Link terminology
            System.out.println("Adding my other terminology!!" + parameter.getOtherTermTableName());
            Termbase otherTerminology = curlSparqlQuery.findListOfTerms(parameter.getOtherTermSparqlEndpoint(), query_writtenRep,  parameter.getOtherTermTableName());
            for (String key:otherTerminology.getTerms().keySet()){
                 TermDetail termDetail=otherTerminology.getTerms().get(key);
                 System.out.println(termDetail.toString());
            }*/
    
         
            //String url="http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_solarenergy/data/solarenergy/hole-EN";
            //String term="hole";
      
    
    
}
