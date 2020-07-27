/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.sparql;

import browser.termallod.core.termbase.TermDetailNew;
import browser.termallod.core.termbase.Termbase;
/*
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;*/
import java.util.HashMap;
import java.util.Map;
import static browser.termallod.constants.SparqlEndpoint.query_writtenRep;

/**
 *
 * @author elahi
 */
public class JenaSparqlQuery {

    private Termbase terminology;

    //for unknown reason jena is not working..therefore jena library is deleted...
    public JenaSparqlQuery(String myTermSparqlEndpoint, String myTermTableName) throws Exception {
        //this.terminology = getTermBaseFromSparqlEndpoint(myTermSparqlEndpoint, myTermTableName);
    }

    /* private Termbase getTermBaseFromSparqlEndpoint(String sparqlEndpoint, String termBaseName) throws Exception {
        Map<String, TermDetailNew> allkeysValues = new HashMap<String, TermDetailNew>();
        ResultSet results = getResultSparql(sparqlEndpoint, query_writtenRep);
        while (results != null && results.hasNext()) {
            QuerySolution querySolution = results.next();
            RDFNode subject = querySolution.get("?s");
            RDFNode predicate = querySolution.get("?p");
            RDFNode object = querySolution.get("?o");
            TermDetailNew termInfo = new TermDetailNew(subject, object);
            allkeysValues.put(termInfo.getTermOrg(), termInfo);
        }

        Termbase termbase = new Termbase(termBaseName, allkeysValues);
        return termbase;
    }

    private ResultSet getResultSparql(String sparql_endpoint, String sparql_query) {
        Query query = QueryFactory.create(sparql_query); //s2 = the query above
        QueryExecution qExe = QueryExecutionFactory.sparqlService(sparql_endpoint, query);
        ResultSet results = qExe.execSelect();
        return results;
    }*/
    public Termbase getTerminology() {
        return terminology;
    }

}
