/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core;

import static browser.termallod.core.sparql.SparqlEndpoint.endpoint_atc;
import static browser.termallod.core.sparql.SparqlEndpoint.endpoint_intaglio;
import static browser.termallod.core.sparql.SparqlEndpoint.endpoint_solar;
import browser.termallod.core.language.LanguageManager;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.utils.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static browser.termallod.core.sparql.SparqlEndpoint.endpoint_lider_iate;

/**
 *
 * @author elahi
 */
public class Parameter {

    public  String BASE_PATH = "src/java/resources/data/";
    public  String OUTPUT_PATH = BASE_PATH + "/output/";
    public  String INPUT_PATH = BASE_PATH + "/input/";
    public  String CONFIG_PATH = BASE_PATH + "/conf/";
    public  String TEMPLATE_PATH = BASE_PATH + "/template/";
    private LanguageManager languageInfo;
    private String myTermSparqlEndpoint = null, list = null;
    private String htmltype = null;
    private String myTermTableName = "myTerminology";
    private String otherTermTableName = "otherTerminology";
    //private String otherTermSparqlEndpoint = endpoint_intaglio;
    private String otherTermSparqlEndpoint = endpoint_lider_iate; 
    private String matchedTermTable = "link";
    public static String ListOfTerms = "ListOfTerms";
    public static String Browser = "browser";
    public static String TermPage = "TermPage";
    public static String link = "matchTerms";
    private String termJson = "{\"term\":\"hole\","
            + "\"iri\":\"http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_solarenergy/data/solarenergy/hole-EN\","
            + "\"lang\":\"en\"}";
    private String localLangJson = "[{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/NL\"},\"entrycount\":{\"type\":\"typed-literal\",\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"186\"}},{\"language\":{\"type\":\"uri\",\"value\":\"http://tbx2rdf.lider-project.eu/data/YourNameSpace/EN\"},\"entrycount\":{\"type\":\"typed-literal\",\"datatype\":\"http://www.w3.org/2001/XMLSchema#integer\",\"value\":\"19\"}}]";
    private String insertFile=TEMPLATE_PATH+"linkData.txt";

    public Parameter(String args[]) {

        if (args.length > 1&&args[0].contains("link")) {
            this.setParameter(args);
        } else {

            if (args.length > 1) {
                myTermSparqlEndpoint = args[1];
                //System.out.println("SparqlEndpoint: " + myTermSparqlEndpoint);
            } else {
                myTermSparqlEndpoint = endpoint_solar;
                System.err.println("no sparql endpoint in arguments");
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
                //System.out.println("BASE_PATH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + BASE_PATH);
            } else {
            }

            if (args.length > 5) {
                htmltype = args[5];
                //System.out.println("htmltype: " + htmltype);
            } else {
                htmltype = ListOfTerms;
                //htmltype = ListOfTerms; ListOfTerms
                //matchTerms
                //TermPage
            }
            if (args.length > 6) {
                TEMPLATE_PATH = args[6];
                //System.out.println("TEMPLATE_PATH: " + TEMPLATE_PATH);
            } else {
                TEMPLATE_PATH = BASE_PATH + "template/";
            }
            if (args.length > 7) {
                if (htmltype.contains(TermPage)) {
                    termJson = args[7];
                }
            }
        }

        INPUT_PATH = BASE_PATH + "input/";
        try {
            languageInfo = new LanguageManager(new File(BASE_PATH + "/conf/" + "language.conf"));
        } catch (IOException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Parameter(String args[],String htmlType) {

       
        if (htmlType.contains("link")) {
            this.setParameter(args);
        } else {

            if (args.length > 1) {
                myTermSparqlEndpoint = args[1];
                //System.out.println("SparqlEndpoint: " + myTermSparqlEndpoint);
            } else {
                myTermSparqlEndpoint = endpoint_solar;
                //myTermSparqlEndpoint = endpoint_local;
                System.err.println("no sparql endpoint in arguments");
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
                //System.out.println("BASE_PATH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!: " + BASE_PATH);
            } else {
            }

            if (args.length > 5) {
                htmltype = args[5];
                System.out.println("htmltype: " + htmltype);
            } else {
                //htmltype = "browser_en_A_B_2.html";
                htmltype = ListOfTerms;
                //htmltype = ListOfTerms;
                //matchTerms
            }
            if (args.length > 6) {
                TEMPLATE_PATH = args[6];
                //System.out.println("TEMPLATE_PATH: " + TEMPLATE_PATH);
            } else {
                TEMPLATE_PATH = BASE_PATH + "template/";
            }
            if (args.length > 7) {
                if (htmltype.contains(TermPage)) {
                    termJson = args[7];
                }
            }
        }

        INPUT_PATH = BASE_PATH + "input/";
        try {
            languageInfo = new LanguageManager(new File(BASE_PATH + "/conf/" + "language.conf"));
        } catch (IOException ex) {
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setParameter(String[] args) {
        
        if (args.length > 1) {
            BASE_PATH = args[1] + BASE_PATH;
            System.out.println("BASE_PATH: " + BASE_PATH);
        }
        if (args.length > 2) {
            localLangJson = args[2];
            System.out.println("localLangJson: " + localLangJson);
        }
        if (args.length > 3) {
            otherTermSparqlEndpoint = args[3];
            System.out.println("otherTermSparqlEndpoint: " + otherTermSparqlEndpoint);
        }
            
        if (args.length > 4) {
            insertFile = args[4];
            System.out.println("insertFile: " + insertFile);
        }
        if (args.length > 5) {
            myTermSparqlEndpoint = args[5];
            //System.out.println("SparqlEndpoint: " + myTermSparqlEndpoint);
        } else {
            //myTermSparqlEndpoint = endpoint_atc;
            myTermSparqlEndpoint = endpoint_solar;
            System.err.println("no sparql endpoint in arguments");
        }
        
        //String insertFile =  "/tmp/server/uploads/insert.db";
        /*if (args.length > 4) {
            this.myTermSparqlEndpoint = args[4];
            System.out.println("myTermSparqlEndpoint: " + myTermSparqlEndpoint);
        }*/
        htmltype = link;
    }

    public TermDetail getTermDetail() {
        ObjectMapper objectMapper = new ObjectMapper();
        TermDetail termDetail = null;
        try {
            JsonParser jsonParser = objectMapper.readValue(termJson, JsonParser.class);
            termDetail = new TermDetail(jsonParser);

        } catch (IOException ex) {
            System.out.println("json parse fails!!!");
            Logger.getLogger(Parameter.class.getName()).log(Level.SEVERE, null, ex);
        }
        return termDetail;
    }

    public String getBASE_PATH() {
        return BASE_PATH;
    }

    public String getOUTPUT_PATH() {
        return OUTPUT_PATH;
    }

    public String getINPUT_PATH() {
        return INPUT_PATH;
    }

    public String getCONFIG_PATH() {
        return CONFIG_PATH;
    }

    public String getTEMPLATE_PATH() {
        return TEMPLATE_PATH;
    }

    public LanguageManager getLanguageInfo() {
        return languageInfo;
    }

    public String getMyTermSparqlEndpoint() {
        return myTermSparqlEndpoint;
    }

    public String getList() {
        return list;
    }

    public String getHtmltype() {
        return htmltype;
    }

    public String getMyTermTableName() {
        return myTermTableName;
    }

    public String getOtherTermTableName() {
        return otherTermTableName;
    }

    public String getMatchedTermTable() {
        return matchedTermTable;
    }

    public void setHtmltype(String htmltype) {
        this.htmltype = htmltype;
    }

    public String getListOfTerms() {
        return ListOfTerms;
    }

    public String getTermPage() {
        return TermPage;
    }

    public String getMatchTerms() {
        return link;
    }

    public String getOtherTermSparqlEndpoint() {
        return otherTermSparqlEndpoint;
    }

    public String getTermJson() {
        return termJson;
    }

    public String getLocalLangJson() {
        return localLangJson;
    }

    public String getInsertFile() {
        return insertFile;
    }

    @Override
    public String toString() {
        return "Parameter{" + "BASE_PATH=" + BASE_PATH + ", OUTPUT_PATH=" + OUTPUT_PATH + ", INPUT_PATH=" + INPUT_PATH + ", CONFIG_PATH=" + CONFIG_PATH + ", TEMPLATE_PATH=" + TEMPLATE_PATH + ", languageInfo=" + languageInfo + ", myTermSparqlEndpoint=" + myTermSparqlEndpoint + ", list=" + list + ", htmltype=" + htmltype + ", myTermTableName=" + myTermTableName + ", otherTermTableName=" + otherTermTableName + ", otherTermSparqlEndpoint=" + otherTermSparqlEndpoint + ", matchedTermTable=" + matchedTermTable + ", termJson=" + termJson + ", localLangJson=" + localLangJson + ", insertFile=" + insertFile + '}';
    }

}
