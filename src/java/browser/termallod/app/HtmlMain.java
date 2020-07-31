/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.app;

import browser.termallod.process.CreateAlphabetFiles;
import browser.termallod.core.html.HtmlCreator;
import browser.termallod.api.LanguageManager;
import browser.termallod.core.LanguageAlphabetPro;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.constants.SparqlEndpoint;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class HtmlMain implements SparqlEndpoint {

    private static String BASE_PATH = "src/java/resources/data/";
    private static String OUTPUT_PATH = BASE_PATH + "/output/";
    private static String INPUT_PATH = BASE_PATH + "/input/";
    private static String CONFIG_PATH = BASE_PATH + "/conf/";
    private static String TEMPLATE_PATH = BASE_PATH + "/template/";
    private String htmlString = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "    <head>\n"
            + "        <title>Example</title>\n"
            + "    </head>\n"
            + "    <body>\n"
            + "        <p>This is an example of a simple HTML page with one paragraph.</p>\n"
            + "    </body>\n"
            + "</html>";

    private static LanguageManager languageInfo;

    public HtmlMain() {
    }

    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, Exception {
        HtmlMain HtmlMain = new HtmlMain();
        HtmlMain.html(args);
    }

    public boolean html(String[] args) throws Exception {

        String myTermTableName = "myTerminology", otherTermTableName = "otherTerminology", matchedTermTable = "link";
        String myTermSparqlEndpoint = null, list = null;
        String ListOfTermPage = "ListOfTerms", TermPage = "TermPage", htmltype = null;

        System.out.println("called");
        System.out.println("arguments: " + args.length);
        if (args.length > 1) {
            myTermSparqlEndpoint = args[1];
            System.out.println("SparqlEndpoint: " + myTermSparqlEndpoint);
        } else {
            myTermSparqlEndpoint = endpoint_solar;
            System.err.println("no sparql endpoint in arguments");
        }
        if (args.length > 2) {
            OUTPUT_PATH = args[2];
            System.out.println("OUTPUT_PATH: " + OUTPUT_PATH);
        } else {
            System.err.println("no output folder in arguments");

        }
        if (args.length > 3) {
            list = args[3];
            System.out.println("list of terms taken from node.js: " + list);
        } else {
        }
        if (args.length > 4) {
            list = args[4];
            BASE_PATH = args[4] + BASE_PATH;
            System.out.println("BASE_PATH: " + BASE_PATH);
        } else {
        }

        if (args.length > 5) {
            htmltype = args[5];
            System.out.println("htmltype: " + htmltype);
        } else {
            htmltype = "TermPage";
            //htmltype = "ListOfTerms";
        }
        if (args.length > 6) {
            TEMPLATE_PATH = args[6];
            System.out.println("TEMPLATE_PATH: " + TEMPLATE_PATH);
        } else {
            TEMPLATE_PATH = BASE_PATH + "template/";
        }

        INPUT_PATH = BASE_PATH + "input/";

        languageInfo = new LanguageAlphabetPro(new File(BASE_PATH + "/conf/" + "language.conf"));
        System.out.println("reading terms");
        Termbase myTerminology = new CurlSparqlQuery(myTermSparqlEndpoint, query_writtenRep, myTermTableName).getTermbase();
        System.out.println("saving terms");
        if (myTerminology.getTerms().isEmpty()) {
            return false;
        }

        CreateAlphabetFiles alphabetFiles = new CreateAlphabetFiles(languageInfo, myTerminology);

        //cleanDirectory();
        System.out.println("saving files");
        //System.out.println("INPUT_PATH:"+INPUT_PATH);
        FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), INPUT_PATH);
        System.out.println("creating html");

        HtmlCreator htmlCreator = new HtmlCreator(TEMPLATE_PATH, OUTPUT_PATH);

        if (htmltype.contains("ListOfTerms")) {
            htmlCreator.createListOfTermHtmlPage(INPUT_PATH, alphabetFiles.getLangTerms().keySet(), htmltype);
        }
        else if (htmltype.contains("TermPage")) {
            System.out.println("Term page.....");
            Map<String, String> termLinks = new HashMap<String, String>();
            termLinks.put("atc", "link1");
            termLinks.put("disease", "link2");
            TermDetail termDetail = new TermDetail("term_term_term = http:// = en", termLinks);
            htmlCreator.createHtmlTermPage(termDetail, htmltype);
        }

        
       
        

        /*else if(htmltype.contains("TermPage"))
            htmlCreator.createHtmlTermPage(termDetail,htmltype);*/
 /*Map<String,String> termLinks = new HashMap<String,String>();
        termLinks.put("atc", "link1");
        termLinks.put("disease", "link2");
        TermDetail termDetail=new TermDetail("term_term_term = url = en",termLinks);
        if(htmltype.contains("TermPage"))
            htmlCreator.createHtmlTermPage(termDetail,htmltype);
        /*if(categoryName.contains("TermPage"))
          this.createTermPage(termDetail);
        HtmlCreator htmlCreator = new HtmlCreator(INPUT_PATH, alphabetFiles.getLangTerms().keySet(), 
                                                  TEMPLATE_PATH, OUTPUT_PATH, "TermPage");*/
        System.out.println("Processing iate finished!!!");
        return true;
    }

    private static void cleanDirectory() throws IOException {
        FileRelatedUtils.deleteDirectory(INPUT_PATH);
        FileRelatedUtils.createDirectory(INPUT_PATH);
        FileRelatedUtils.deleteDirectory(OUTPUT_PATH);
        FileRelatedUtils.createDirectory(OUTPUT_PATH);
    }

}
