/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.sparql;

import browser.termallod.app.HtmlMain;
import static browser.termallod.constants.SparqlEndpoint.iate_query1;
import browser.termallod.constants.SparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import browser.termallod.utils.FileUrlUtils;
import browser.termallod.utils.StringMatcherUtil2;
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
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author elahi
 * //https://stackoverflow.com/questions/13356534/how-to-read-xml-child-node-values-by-using-a-loop-in-java
 * //https://howtodoinjava.com/xml/read-xml-dom-parser-example/
 */
public class CurlSparqlQuery {

    public Termbase findListOfTerms(String endpoint, String query, String termBaseName) throws Exception {
        String resultSparql = executeSparqlQuery(endpoint, query);
        Termbase termbase = new Termbase(termBaseName, parseResult(resultSparql));
        return termbase;
    }

    public TermDetail findTermDetail(String endpoint, String sparqlTermDetail) throws Exception {
        String result = executeSparqlQuery(endpoint, sparqlTermDetail);
        TermDetail detail = this.parseResults(result,1);    
        
        String termLinkSparql = SparqlQuery.getTermLinks(detail.getTermUrl());
        result =this.executeSparqlQuery(endpoint, termLinkSparql);
        TermDetail link = this.parseResults(result,2);
        detail.setTermLinks(link.getTermLinks());
        
        return detail;
    }


    private String executeSparqlQuery(String endpoint, String query) {
        String result = null, resultUnicode = null, command = null;
        Process process = null;
        try {
            resultUnicode = FileUrlUtils.stringToUrlUnicode(query);
            command = "curl " + endpoint + "?query=" + resultUnicode;
            process = Runtime.getRuntime().exec(command);
            //System.out.print(command);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in unicode in sparql query!" + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in sending sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }

        /*try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException ex) {
            Logger.getLogger(findListOfTerms.class.getName()).log(Level.SEVERE, null, ex);
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
            System.out.println("error in reading sparql query!" + ex.getMessage());
            ex.printStackTrace();
        }
        return result;
    }

    public Map<String, TermDetail> parseResult(String xmlStr) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Map<String, TermDetail> terms = new HashMap<String, TermDetail>();
        try {
            builder = factory.newDocumentBuilder();
            terms = this.parseResult(builder, xmlStr);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        } catch (DOMException ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
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

    private Map<String, TermDetail> parseResult(DocumentBuilder builder, String xmlStr) throws SAXException, IOException, DOMException, Exception {
        Map<String, TermDetail> allkeysValues = new HashMap<String, TermDetail>();
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
                    TermDetail termInfo = new TermDetail(url, null, term, true);
                    allkeysValues.put(termInfo.getTermOrg(), termInfo);
                }
            }
        }
        return allkeysValues;
    }

    private TermDetail parseResults(String xmlStr,Integer index) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlStr)));
            NodeList results = document.getElementsByTagName("results");

            for (int i = 0; i < results.getLength(); i++) {
                NodeList childList = results.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    Node childNode = childList.item(j);
                    if ("result".equals(childNode.getNodeName())) {
                        String string = childNode.getTextContent().trim();
                        if (index == 1) {
                            return this.forTermDetail(string);
                        } else if (index == 2) {
                            return this.forTermLink(string);
                        }

                    }

                }
            }
        } catch (Exception ex) {
            Logger.getLogger(CurlSparqlQuery.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("error in parsing sparql in XML!" + ex.getMessage());
            ex.printStackTrace();
        }
        return new TermDetail();
    }

    private TermDetail forTermDetail(String string) {
        String[] infos = string.split("\n");
        List<String> wordList = Arrays.asList(infos);
        Integer index = 0, termIndex = 1, urlIndex = 2;
        String term = null, url = null;
        for (String textContent : wordList) {
            if (index == termIndex) {
                term = textContent.trim();
            } else if (index == urlIndex) {
                url = textContent.trim();
            }
            index++;
        }
        return new TermDetail(url, null, term, true);
    }

    private TermDetail forTermLink(String string) {
        String terminologyName = null, url = null;
        TermDetail termDetail = new TermDetail();
        Map<String, String> termLInks = new HashMap<String, String>();
        String[] infos = string.split("\n");
        List<String> wordList = Arrays.asList(infos);
        if (wordList.isEmpty()) {
            return termDetail;
        } else {
            for (String textContent : wordList) {
                url = textContent;
            }
            terminologyName = StringMatcherUtil2.getTerminologyName(url);
            termLInks.put(terminologyName, url);
            System.out.println("!!!!!!!!!!!!termLInks!!!!!!!!!!!!!!!!!" + termLInks);
            termDetail.setTermLinks(termLInks);
        }
        return termDetail;
    }

    public static void main(String[] args)  {
        CurlSparqlQuery curlSparqlQuery=new CurlSparqlQuery();

        String resultSparql = "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd\">\n"
                + "<head>\n"
                + "<variable name=\"exactmatch\"/>\n"
                + "</head>\n"
                + "<results distinct=\"false\" ordered=\"true\">\n"
                + "<result>\n"
                + "<binding name=\"exactmatch\"><uri>http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_intaglio/data/intaglio/hole-EN</uri>\n"
                + "</binding>\n"
                + "</result>\n"
                + "</results>\n"
                + "</sparql>";
        curlSparqlQuery.forTermLink(resultSparql);
    }
}
