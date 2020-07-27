/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.testcode;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.sparql.core.DatasetImpl;

/**
 *
 * @author elahi
 */
public class SparqlExample1 {

    public SparqlExample1() {
    }

    public ResultSet executeQuery(String queryString) throws Exception {

        QueryExecution exec = QueryExecutionFactory.create(QueryFactory.create(queryString), new DatasetImpl(ModelFactory.createDefaultModel()));
        return exec.execSelect();

    }

    public static void main(String[] args) throws Exception {

        /**
         * For documentation please read "Federated SPARQL queries" at
         * http://www.bioontology.org/wiki/index.php/SPARQL_BioPortal
         */
        SparqlExample1 test = new SparqlExample1();

        String query = "PREFIX map: <http://protege.stanford.edu/ontologies/mappings/mappings.rdfs#> \n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n"
                + "SELECT DISTINCT ?mappedParent WHERE {\n"
                + "    SERVICE <http://sparql.bioontology.org/mappings/sparql/?apikey=YOUR API KEY HERE> {\n"
                + "        ?mapping map:target <http://purl.bioontology.org/ontology/CSP/0468-5952> .\n"
                + "        ?mapping map:source ?source .\n"
                + "    }\n"
                + "    SERVICE <http://sparql.bioontology.org/ontologies/sparql/?apikey=YOUR API KEY HERE> {\n"
                + "        ?source rdfs:subClassOf ?mappedParent .\n"
                + "    }\n"
                + "}";

        /*ResultSet results = test.executeQuery(query);
        for (; results.hasNext();) {
            QuerySolution soln = results.nextSolution();
            System.out.println(soln);
        }*/
    }
}
