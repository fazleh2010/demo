/*
 * 
 */
package browser.termallod.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Mohammad Fazleh Elahi
 *
 * This is a interface contains constants of file type
 *
 */
public interface SparqlEndpoint {

    public static final String tbx2rdf_iate_endpoint = "https://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_iate/sparql/";
    public static String tbx2rdf_iate__query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
    public static final String dbpedia_endpoint = "http://dbpedia.org/sparql";
    public static String dbpedia_query = "select distinct ?Concept where {[] a ?Concept} LIMIT 100";
    public static String tbx2rdftest ="https://webtentacle1.techfak.uni-bielefeld.de/tbx2rdftest/sparql";

    public static String iate_query = "PREFIX cc:    <http://creativecommons.org/ns#> \n"
            + "\n"
            + "PREFIX void:  <http://rdfs.org/ns/void#> \n"
            + "\n"
            + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#> \n"
            + "\n"
            + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
            + "\n"
            + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#> \n"
            + "\n"
            + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#> \n"
            + "\n"
            + "PREFIX dct:   <http://purl.org/dc/terms/> \n"
            + "\n"
            + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
            + "\n"
            + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> \n"
            + "\n"
            + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#> \n"
            + "\n"
            + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/> \n"
            + "\n"
            + "PREFIX dcat:  <http://www.w3.org/ns/dcat#> \n"
            + "\n"
            + "PREFIX prov:  <http://www.w3.org/ns/prov#> \n"
            + "\n"
            + "SELECT ?s ?p ?o from <http://tbx2rdf.lider-project.eu/> WHERE { \n"
            + "\n"
            + "    ?s ?p ?o .\n"
            + "\n"
            + "    ?o ontolex:canonicalForm ?canform .\n"
            + "\n"
            + "    ?canform ontolex:writtenRep ?rep .\n"
            + "\n"
            + "    FILTER regex(str(?rep), \"ace\") .\n"
            + "\n"
            + "} LIMIT 5";

}
