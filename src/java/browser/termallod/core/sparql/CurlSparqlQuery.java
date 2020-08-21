/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.sparql;

import browser.termallod.core.Parameter;
import browser.termallod.core.termbase.TermSubjectInfo;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.TermLists;
import browser.termallod.utils.StringMatcher;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.w3c.dom.NamedNodeMap;

/**
 *
 * @author elahi
 * //https://stackoverflow.com/questions/13356534/how-to-read-xml-child-node-values-by-using-a-loop-in-java
 * //https://howtodoinjava.com/xml/read-xml-dom-parser-example/
 */
public class CurlSparqlQuery {

    public TermLists findListOfTerms(String endpoint, String query, String termBaseName, Boolean flag) throws Exception {
        String resultSparql = executeSparqlQuery(endpoint, query);
        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!endpoint:"+endpoint);
        //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!resultSparql:"+resultSparql);
        TermLists termbase = new TermLists(termBaseName, parseResult(resultSparql, flag));
        return termbase;
    }

    public TermDetail findTermDetail(String endpoint, Parameter parameter) throws Exception {
        String sparqlTermDetail = SparqlQuery.getTermDetailSpqlByTerm(parameter.getTermDetail());
        String givenLang=parameter.getTermDetail().getLanguage();
        String result = executeSparqlQuery(endpoint, sparqlTermDetail);
        //System.out.println("!!!!!!!!!!result!!!!!!!!!!!!!!!");
        //System.out.println(result);
        TermDetail termDetail = this.parseResults(result, 1,givenLang);
        System.out.println("term detail:"+termDetail);
        String termLinkSparql = SparqlQuery.getTermLinks(termDetail.getTermUrl());
        result = this.executeSparqlQuery(endpoint, termLinkSparql);
        TermDetail link = this.parseResults(result, 2,givenLang);
        termDetail.setTermLinks(link.getTermLinks());

        return termDetail;
    }

    private String executeSparqlQuery(String endpoint, String query) {
        String result = null, resultUnicode = null, command = null;
        Process process = null;
        try {
            resultUnicode = StringMatcher.stringToUrlUnicode(query);
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

    public Map<String, TermDetail> parseResult(String xmlStr, Boolean flag) {
        Document doc = convertStringToXMLDocument(xmlStr);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        Map<String, TermDetail> terms = new HashMap<String, TermDetail>();
        try {
            builder = factory.newDocumentBuilder();
            terms = this.parseResult(builder, xmlStr, flag);
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

    private Map<String, TermDetail> parseResult(DocumentBuilder builder, String xmlStr, Boolean flag) throws SAXException, IOException, DOMException, Exception {
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
                            term = http;
                        }

                    }
                    //System.out.println("term.."+"("+term+")"+".."+url);
                    TermDetail termInfo = new TermDetail(url, null, term, true);
                    //System.out.println("in side java termDetail........"+"("+termInfo.getTermOrg()+")");
                    if (flag) {
                        term = termInfo.getTermOrg().replaceAll("\\s+", "");
                        term = term.toLowerCase().trim();
                    } else {
                        term = termInfo.getTermOrg();
                    }

                    allkeysValues.put(term, termInfo);
                }
            }
        }
        return allkeysValues;
    }

    public TermDetail parseResults(String xmlStr, Integer index,String givenLang) {
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
                            return this.forTermDetail(string,givenLang);
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

    /*private TermDetail forTermDetail(String string) {
        String[] infos = string.split("\n");
        List<String> wordList = Arrays.asList(infos);
        Integer index = 0, termIndex = 1, urlIndex = 2;
        String term = null, url = null;
        for (String textContent : wordList) {
            System.out.println("!!!!!!!!!!!!textContent!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println(textContent);
            if (index == termIndex) {
                term = textContent.trim();
            } else if (index == urlIndex) {
                url = textContent.trim();
            }
            index++;
        }
        return new TermDetail(url, null, term, true);
    }*/
    
    private TermDetail forTermDetail(String string,String givenLang) {
        String[] infos = string.split("\n");
        List<String> wordList = Arrays.asList(infos);
        Integer index = 0, languageIndex = 1, termIndex = 2, urlIndex = 3;
        TermDetail termDetail = new TermDetail();
        TermSubjectInfo subjectInfo = new TermSubjectInfo();
        for (String textContent : wordList) {
            index++;
            //System.out.println("!!!!!!!!!!!!textContent!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //System.out.println(textContent);
            textContent = textContent.trim();
            if (index == languageIndex) {
                String lang=StringMatcher.getLastString(textContent,'/', Boolean.TRUE);
                if(!lang.contains(givenLang)){
                    return new TermDetail();
                }
                termDetail.setLang(textContent);
            } else if (index == termIndex) {
                termDetail.setTerm(textContent);
            } else if (index == urlIndex) {
                termDetail.setUrl(textContent);
            } 
            else if (index>3&&textContent.contains("http")) {
                if(textContent.contains("subjectField"))
                    subjectInfo.setSubjectId(textContent); 
                else if(textContent.contains("admn-sts"))
                  termDetail.setAdministrativeStatus(textContent);
                else
                     subjectInfo.setReferenceID(textContent);
            } 
            else if(index>3&&StringMatcher.isNumeric(textContent)){
               termDetail.setReliabilityCode(textContent);
            }
            else if(index>3&&textContent.contains("unknown"))
             subjectInfo.setSubjectId(textContent); 

        }
        termDetail.setSubject(subjectInfo);
        return termDetail;
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
            terminologyName = StringMatcher.getTerminologyName(url);
            termLInks.put(terminologyName, url);
            termDetail.setTermLinks(termLInks);
        }
        return termDetail;
    }

   

}
