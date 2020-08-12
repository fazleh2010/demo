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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    private Parameter parameter = null;

    public HtmlMain(String[] args) {
         parameter = new Parameter(args);
    }
    
     public HtmlMain(String[] args,String type) {
         parameter = new Parameter(args,type);
    }


    public static void main(String[] args) throws ParserConfigurationException, SAXException, Exception {
        HtmlMain HtmlMain = new HtmlMain(args,"link");
        HtmlMain.html();
    }

    public String html() throws Exception {
        //parameter = new Parameter(args);

        //String myTermTableName = "myTerminology";
        /*String myTermSparqlEndpoint = null, list = null;
        String htmltype = null,url=null,term=null;*/
 /*
         private static String ListOfTerms = "ListOfTerms";
         private static String TermPage = "TermPage";
         private static String link = "link";
         */
        //test list of terms
        //Parameter parameter = new Parameter(args,Parameter.ListOfTerms);
        //String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();
        //test termpage 
        /*parameter = new Parameter(args,Parameter.TermPage);
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();*/
        //test link term page
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();

        System.out.println("reading terms");
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        HtmlCreator htmlCreator = new HtmlCreator(parameter.getTEMPLATE_PATH(), parameter.getOUTPUT_PATH());

        if (parameter.getHtmltype().contains(Parameter.ListOfTerms)) {
            Termbase myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint);
            System.out.println("saving terms");
            if (myTerminology.getTerms().isEmpty()) {
                return Parameter.ListOfTerms;
            }

            CreateAlphabetFiles alphabetFiles = new CreateAlphabetFiles(parameter.getLanguageInfo(), myTerminology);

            //cleanDirectory();
            System.out.println("saving files");
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());
            
            System.out.println("creating html");

            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(), true);
             return Parameter.ListOfTerms;
        } else if (parameter.getHtmltype().contains(Parameter.TermPage)) {
            String termDetailSparql = SparqlQuery.getTermDetailSpqlByTerm(parameter.getTermDetail());
            TermDetail termDetail = curlSparqlQuery.findTermDetail(myTermSparqlEndpoint, termDetailSparql);
            htmlCreator.createHtmlTermPage(termDetail, parameter.getHtmltype());
             return Parameter.TermPage;
        } 
        
        
        else if (parameter.getHtmltype().contains(Parameter.link)) {
            System.out.println("inside into term matching!!!!!!!!!!!!");                   
            Matching mattchTerminologies = new Matching(parameter.getINPUT_PATH(), parameter.getOtherTermSparqlEndpoint(), parameter.getLocalLangJson(), parameter.getOtherTermTableName());

            for (String key : mattchTerminologies.getMatchedTermsInto().keySet()) {
                System.out.println("key:"+key);
                Set<TermDetail> matchedTerms = mattchTerminologies.getMatchedTermsInto().get(key);
                for (TermDetail termDetail : matchedTerms) {
                    String term = termDetail.getTermDecrpt();
                    String localTermUrl = termDetail.getTermUrl();
                    String remoteTermUrl = termDetail.getTermLinks().get("otherTerminology");
                    System.out.println("!!!!!!!!!!!localTermUrl:"+localTermUrl);
                    System.out.println("!!!!!!!!!!!remoteTermUrl:"+remoteTermUrl);
                    System.out.println("!!!!!!!!!!!!term:"+term);
                    String sparqlEnd = "SPARQL INSERT DATA {\n"
                            + "GRAPH <http://tbx2rdf.lider-project.eu/> {\n"
                            + "<" + localTermUrl + "> <http://www.w3.org/ns/lemon/ontolex#sameAs> <" + remoteTermUrl + ">\n"
                            + "} };";
                    System.out.println("!!!!!!!!!!!sparqlEnd!!!!!!!!!!!!!"+sparqlEnd);
                    writeSparqlToFile(parameter.getInsertFile(),sparqlEnd);
                }
            }
             /* String term="hole";
             String localTermUrl = "http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN";
             String remoteTermUrl ="http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_intaglio/data/intaglio/hole-EN";
             writeSparqlToFile(insertFile,term,localTermUrl,remoteTermUrl);*/
             
             /*String localTermUrl="http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN";
             String remoteTermUrl="http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_intaglio/data/intaglio/hole-EN";
             String sparqlEnd = "SPARQL INSERT DATA {\n"
                            + "GRAPH <http://tbx2rdf.lider-project.eu/> {\n"
                            + "<" + localTermUrl + "> <http://www.w3.org/ns/lemon/ontolex#sameAs> <" + remoteTermUrl + ">\n"
                            + "} };";
              writeSparqlToFile(parameter.getInsertFile(), sparqlEnd);*/
             return Parameter.link;
        }

        System.out.println("Processing iate finished!!!");
        return Parameter.ListOfTerms;
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
    
    private void writeINFile(String insertFile,String term, String localTermUrl,String remoteTermUrl) throws IOException {
        /*String term=termDetail.getTermDecrpt();
        String localTermUrl = termDetail.getTermUrl();
        String remoteTermUrl = termDetail.getTermLinks().get("otherTerminology");*/
       

        Map<String, String> element = new HashMap<String, String>();
                    element.put("localTerm", term);
                    element.put("localUrl", localTermUrl);
                    element.put("remoteTerm", term);
                    element.put("remoteUrl", remoteTermUrl);       
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(element);
            System.out.println("!!!!!!!!!!!!!!!inside Java!!!!!!!!!!!!!!!!!!!!"+json);
            objectMapper.writeValue(new File(insertFile), json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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
    private void writeSparqlToFile(String file, String sparqlEnd) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file, true);
            BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
            bufferFileWriter.append(sparqlEnd);
            bufferFileWriter.newLine();
            bufferFileWriter.close();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!successsfully write the files");
        } catch (IOException ex) {
            Logger.getLogger(HtmlMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
         /*String jsonLangStr = "[{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/NL\"},"
                    + "\"entrycount\":{\"type\":\"typed-literal\","
                    + "\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"186\"}},"
                    + "{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/EN\"},"
                    + "\"entrycount\":{\"type\":\"typed-literal\",\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"19\"}}]";*/
     

}
