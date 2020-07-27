/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.sparql;

import static browser.termallod.constants.SparqlEndpoint.iate_query1;
import browser.termallod.core.termbase.TermDetailNew;
import browser.termallod.core.termbase.Termbase;
import browser.termallod.utils.FileUrlUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author elahi
 * //https://stackoverflow.com/questions/13356534/how-to-read-xml-child-node-values-by-using-a-loop-in-java
 * //https://howtodoinjava.com/xml/read-xml-dom-parser-example/
 */
public class CurlSparqlQuery {

    private Termbase termbase = null;

    public CurlSparqlQuery(String endpoint, String query, String termBaseName) throws Exception {
        String resultSparql = executeSparqlQuery(endpoint, query);
        this.termbase = new Termbase(termBaseName, parseResult(resultSparql));
    }

    public CurlSparqlQuery(String endpoint, String query) throws Exception {
        String resultSparql = executeSparqlQuery(endpoint, query);
        //System.out.println(resultSparql);
    }

    private String executeSparqlQuery(String endpoint, String query)   {
        String result = null,resultUnicode=null, command =null;
        Process process=null;
        try {
            resultUnicode = FileUrlUtils.stringToUrlUnicode(query);
            command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
            //System.out.print(command);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in unicode in sparql query!"+ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
             System.out.println("error in sending sparql query!"+ex.getMessage());
             ex.printStackTrace();
        }
       
        /*try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
             System.out.println("error in sending sparql query!"+ex.getMessage());
        }*/
        
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            result = builder.toString();
            // System.out.println("result String:");
            //System.out.print(result);
            //convertToXML(result);

        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in reading sparql query!"+ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public Map<String, TermDetailNew> parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=null;
        Map<String, TermDetailNew> terms=new HashMap<String, TermDetailNew>();
        try {
            builder = factory.newDocumentBuilder();
            terms=this.parseResult(builder, xmlStr);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!"+ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!"+ex.getMessage());
            ex.printStackTrace();
        } catch (DOMException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!"+ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!"+ex.getMessage());
            ex.printStackTrace();
        }
        return terms;
    }

    private Document convertStringToXMLDocument(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, TermDetailNew> parseResult(DocumentBuilder builder, String xmlStr) throws SAXException, IOException, DOMException, Exception {
        Map<String, TermDetailNew> allkeysValues = new HashMap<String, TermDetailNew>();
        Document document = builder.parse(new InputSource(new StringReader(
                xmlStr)));
        NodeList results = document.getElementsByTagName("results");

       
        for (int i = 0; i < results.getLength(); i++) {
            NodeList childList = results.item(i).getChildNodes();

            for (int j = 0; j < childList.getLength(); j++) {
                Node childNode = childList.item(j);
                if ("result".equals(childNode.getNodeName())) {
                    String string = childList.item(j).getTextContent().trim();
                    //st = st.replaceAll("\\s+","");
                    //System.out.println(string);
                    String[] infos = string.split("\n");
                    List<String> wordList = Arrays.asList(infos);
                    String url = null, predicate = null, term = null;
                    Integer index = 0;
                    for (String http : wordList) {
                        if (http.contains("http") && http.contains("CanonicalForm")) {
                            url = http;
                        } else {
                            term = http.trim();
                        }

                    }
                    TermDetailNew termInfo = new TermDetailNew(url, null, term, true);
                    allkeysValues.put(termInfo.getTermOrg(), termInfo);
                }
            }
        }
        return allkeysValues;
    }

    public Termbase getTermbase() {
        return termbase;
    }

}
