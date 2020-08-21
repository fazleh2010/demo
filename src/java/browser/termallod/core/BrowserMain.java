/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core;

import browser.termallod.core.language.LangPairFilesGen;
import browser.termallod.core.html.HtmlCreator;
import browser.termallod.core.termbase.TermListMatcher;
import static browser.termallod.core.Parameter.Browser;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.core.sparql.SparqlEndpoint;
import browser.termallod.core.sparql.SparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.TermLists;
import static browser.termallod.utils.StringMatcher.getPageNumber;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import browser.termallod.constants.LanguageDetail;

/**
 *
 * @author Mohammad Fazleh Elahi
 */
public class BrowserMain implements SparqlEndpoint {

    private static LanguageDetail languageInfo;
    private Parameter parameter = null;

    public BrowserMain(String[] args) {
        parameter = new Parameter(args);
    }

    public BrowserMain(String[] args, String type) {
        parameter = new Parameter(args, type);
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, Exception {
        String html = "html";
        String link = "link";
        BrowserMain HtmlMain = new BrowserMain(args, link);
        HtmlMain.html();
    }

    public String html() throws Exception {
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        HtmlCreator htmlCreator = new HtmlCreator(parameter.getTEMPLATE_PATH(), parameter.getOUTPUT_PATH());

        if (parameter.getHtmltype().contains(Parameter.ListOfTerms)) {
            FileRelatedUtils.cleanDirectory(parameter.getINPUT_PATH(), parameter.getOUTPUT_PATH());
            TermLists myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint, false);
            if (myTerminology.getTerms().isEmpty()) {
                return Parameter.ListOfTerms;
            }
            LangPairFilesGen alphabetFiles = new LangPairFilesGen(parameter.getLanguageInfo(), myTerminology);
            //alphabetFiles.display();
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());
            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(), true, "all", 1);
            return Parameter.ListOfTerms;
        } else if (parameter.getHtmltype().contains(Browser)) {
            System.out.println("Browser................" + Browser);
            TermLists myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint, false);
            if (myTerminology.getTerms().isEmpty()) {
                return Parameter.Browser;
            }
            LangPairFilesGen alphabetFiles = new LangPairFilesGen(parameter.getLanguageInfo(), myTerminology);
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());
            Integer pageNumber = getPageNumber(parameter.getHtmltype());
            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(), true, "A_B", pageNumber);

        } else if (parameter.getHtmltype().contains(Parameter.TermPage)) {
           
            //System.out.println("termDetailSparql :"+termDetailSparql);
            TermDetail termDetail = curlSparqlQuery.findTermDetail(myTermSparqlEndpoint, parameter);
            //System.out.println("after sparql query :"+termDetail);
            //System.out.println("after sparql query :"+parameter.getHtmltype());
            htmlCreator.createHtmlTermPage(termDetail, parameter.getHtmltype());
            return Parameter.TermPage;
        } else if (parameter.getHtmltype().contains(Parameter.link)) {
            //System.out.println("inside into term matching!!!!!!!!!!!!");
            //System.out.println("parameter:" + parameter);
            TermListMatcher mattchTerminologies = new TermListMatcher(parameter);

            /*for (String key : mattchTerminologies.getMatchedTermsInto().keySet()) {
                System.out.println("key:" + key);
                Set<TermDetail> matchedTerms = mattchTerminologies.getMatchedTermsInto().get(key);
                for (TermDetail termDetail : matchedTerms) {
                    String term = termDetail.getTermDecrpt();
                    String localTermUrl = termDetail.getTermUrl();
                    String remoteTermUrl = termDetail.getTermLinks().get("otherTerminology");
                    System.out.println("!!!!!!!!!!!localTermUrl:" + localTermUrl);
                    System.out.println("!!!!!!!!!!!remoteTermUrl:" + remoteTermUrl);
                    System.out.println("!!!!!!!!!!!!term:" + term);
                    String sparqlEnd = "SPARQL INSERT DATA {\n"
                            + "GRAPH <http://tbx2rdf.lider-project.eu/> {\n"
                            + "<" + localTermUrl + "> <http://www.w3.org/ns/lemon/ontolex#sameAs> <" + remoteTermUrl + ">\n"
                            + "} };";
                    System.out.println("!!!!!!!!!!!sparqlEnd!!!!!!!!!!!!!" + sparqlEnd);
                    break;
                    //FileRelatedUtils.writeSparqlToFile(parameter.getInsertFile(), sparqlEnd);
                }
                break;
            }*/

            return Parameter.link;
        }

        System.out.println("Processing finished!!!");
        return Parameter.ListOfTerms;
    }

}
