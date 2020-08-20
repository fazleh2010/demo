/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.utils;

import browser.termallod.core.sparql.CurlSparqlQuery;
import browser.termallod.core.termbase.TermDetail;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 *
 * @author elahi
 */
public class Test {
     public static void main(String[] args) throws ParserConfigurationException, SAXException, Exception {
       CurlSparqlQuery CurlSparqlQuery=new CurlSparqlQuery();
       Integer language=0;
       Integer index = 0, languageIndex = 1, termIndex = 2,urlIndex = 3,codeIndex=4,statusIndex=5,refIndex=6,subjectIndex=7;
       
        String result1 = "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd\">\n"
                + "<head>\n"
                + "<variable name=\"lang\"/>\n"
                + "<variable name=\"rep\"/>\n"
                + "<variable name=\"entity\"/>\n"
                + "<variable name=\"ref\"/>\n"
                + " <variable name=\"code\"/>\n"
                + "<variable name=\"status\"/>\n"
                + "<variable name=\"subject\"/>\n"
                + "</head>\n"
                + "<results distinct=\"false\" ordered=\"true\">\n"
                + "<result>\n"
                + "<binding name=\"lang\"><uri>http://tbx2rdf.lider-project.eu/data/YourNameSpace/en</uri></binding>\n"
                + "<binding name=\"rep\"><literal xml:lang=\"en\">Academic Project Team</literal></binding>\n"
                + "<binding name=\"entity\"><uri>http://tbx2rdf.lider-project.eu/data/YourNameSpace/Academic+Project+Team-en</uri></binding>\n"
                + "<binding name=\"ref\"><uri>http://tbx2rdf.lider-project.eu/data/YourNameSpace/IATE-3576516</uri></binding>\n"
                + "<binding name=\"code\"><literal datatype=\"http://www.w3.org/2001/XMLSchema#integer\">3</literal></binding>\n"
                + "<binding name=\"status\"><uri>http://tbx2rdf.lider-project.eu/tbx#supersededTerm-admn-sts</uri></binding>\n"
                + "<binding name=\"subject\"><uri> http://tbx2rdf.lider-project.eu/data/iate/subjectField/10</uri></binding>\n"
                + "</result>\n"
                + "</results>\n"
                + "</sparql>";
        
         String result2 = "<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2001/sw/DataAccess/rf1/result2.xsd\">\n"
                + "<head>\n"
                + "<variable name=\"lang\"/>\n"
                + "<variable name=\"rep\"/>\n"
                + "<variable name=\"entity\"/>\n"
                + "<variable name=\"ref\"/>\n"
                + " <variable name=\"code\"/>\n"
                + "<variable name=\"status\"/>\n"
                + "<variable name=\"subject\"/>\n"
                + "</head>\n"
                + "<results distinct=\"false\" ordered=\"true\">\n"
                + "<result>\n"
                + "<binding name=\"lang\"><uri>http://tbx2rdf.lider-project.eu/data/YourNameSpace/en</uri></binding>\n"
                + "<binding name=\"rep\"><literal xml:lang=\"en\">Academic Project Team</literal></binding>\n"
                + "<binding name=\"entity\"><uri>http://tbx2rdf.lider-project.eu/data/YourNameSpace/Academic+Project+Team-en</uri></binding>\n"
                + "</result>\n"
                + "</results>\n"
                + "</sparql>";
           
         TermDetail detail = CurlSparqlQuery.parseResults(result2, 1,"nl");
         System.out.println(detail);

    }
    
}
