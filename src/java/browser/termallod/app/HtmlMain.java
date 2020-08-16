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
import static browser.termallod.constants.Parameter.Browser;
import browser.termallod.utils.FileRelatedUtils;
import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.constants.SparqlEndpoint;
import browser.termallod.constants.SparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import browser.termallod.core.termbase.Termbase;
import static browser.termallod.utils.StringMatcherUtil2.getPageNumber;
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

    public HtmlMain(String[] args, String type) {
        parameter = new Parameter(args, type);
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, Exception {
        String html = "html";
        String link = "link";
        HtmlMain HtmlMain = new HtmlMain(args, link);
        HtmlMain.html();
    }

    public String html() throws Exception {
        String myTermSparqlEndpoint = parameter.getMyTermSparqlEndpoint();
        CurlSparqlQuery curlSparqlQuery = new CurlSparqlQuery();
        HtmlCreator htmlCreator = new HtmlCreator(parameter.getTEMPLATE_PATH(), parameter.getOUTPUT_PATH());

        if (parameter.getHtmltype().contains(Parameter.ListOfTerms)) {
            FileRelatedUtils.cleanDirectory(parameter.getINPUT_PATH(), parameter.getOUTPUT_PATH());
            Termbase myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint, false);
            if (myTerminology.getTerms().isEmpty()) {
                return Parameter.ListOfTerms;
            }
            CreateAlphabetFiles alphabetFiles = new CreateAlphabetFiles(parameter.getLanguageInfo(), myTerminology);
            //alphabetFiles.display();
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());
            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(), true, "all", 1);
            return Parameter.ListOfTerms;
        } else if (parameter.getHtmltype().contains(Browser)) {
            //System.out.println("Browser................" + Browser);
            Termbase myTerminology = curlSparqlQuery.findListOfTerms(parameter.getMyTermSparqlEndpoint(), query_writtenRep, myTermSparqlEndpoint, false);
            //System.out.println("saving terms");
            if (myTerminology.getTerms().isEmpty()) {
                return Parameter.Browser;
            }

            CreateAlphabetFiles alphabetFiles = new CreateAlphabetFiles(parameter.getLanguageInfo(), myTerminology);

            //cleanDirectory();
            //System.out.println("saving files");
            FileRelatedUtils.writeFile(alphabetFiles.getLangTerms(), parameter.getINPUT_PATH());

            //System.out.println("creating html");
            Integer pageNumber = getPageNumber(parameter.getHtmltype());

            htmlCreator.createListOfTermHtmlPage(parameter.getINPUT_PATH(), alphabetFiles.getLangTerms().keySet(), parameter.getHtmltype(), true, "A_B", pageNumber);

        } else if (parameter.getHtmltype().contains(Parameter.TermPage)) {
            String termDetailSparql = SparqlQuery.getTermDetailSpqlByTerm(parameter.getTermDetail());
            //System.out.println("termDetailSparql :"+termDetailSparql);
            TermDetail termDetail = curlSparqlQuery.findTermDetail(myTermSparqlEndpoint, termDetailSparql);
            //System.out.println("after sparql query :"+termDetail);
            //System.out.println("after sparql query :"+parameter.getHtmltype());
            htmlCreator.createHtmlTermPage(termDetail, parameter.getHtmltype());
            return Parameter.TermPage;
        } else if (parameter.getHtmltype().contains(Parameter.link)) {
            //System.out.println("inside into term matching!!!!!!!!!!!!");
            //System.out.println("parameter:" + parameter);
            Matching mattchTerminologies = new Matching(parameter);

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
