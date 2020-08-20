/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser.termallod.core.sparql;

import browser.termallod.core.termbase.TermDetail;

/**
 *
 * @author elahi
 */
public class SparqlQuery implements SparqlEndpoint {

    /*public static String getTermDetailSpqlByTerm(TermDetail termDetail) {
        String term=termDetail.getTermDecrpt();
        return ontoLexPrefix
                + "\n"
                + "SELECT ?lang ?rep ?entity from <http://tbx2rdf.lider-project.eu/> WHERE { \n"
                + "?entity ontolex:canonicalForm ?canform .\n"
                + "?canform ontolex:writtenRep ?rep .\n"
                + "?lang rdf:type ontolex:Lexicon .\n"
                + "?lang ontolex:entry ?entity .\n"
                + "FILTER regex(str(?rep), \"" + term + "\") ."
                + "\n"
                + "} LIMIT 100";
    }*/
    
    
    public static String getTermDetailSpqlByTerm(TermDetail termDetail) {
        String term = termDetail.getTermDecrpt();
        return ontoLexPrefix
                + "SELECT ?lang ?rep ?entity ?ref ?code ?status ?subject WHERE { \n"
                + "       ?lang ontolex:entry ?entity .\n"
                + "       ?entity ontolex:canonicalForm ?canform .\n"
                + "       ?canform ontolex:writtenRep ?rep .\n"
                + "       ?entity ontolex:sense ?sense .\n"
                + "    OPTIONAL {?sense ontolex:reference ?ref  .}.\n"
                + "    OPTIONAL {?ref tbx:subjectField ?subject  . }.\n"
                + "    OPTIONAL {?entity tbx:reliabilityCode ?code . }.\n"
                + "    OPTIONAL {?entity tbx:administrativeStatus ?status. }.\n"
                + "    FILTER regex(str(?rep), \"" + term + "\") .} LIMIT 100";

    }
    
     public static String getTermLinks(String url) {

        return "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "\n"
                + "PREFIX cc:    <http://creativecommons.org/ns#> \n"
                + "PREFIX void:  <http://rdfs.org/ns/void#> \n"
                + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#> \n"
                + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#> \n"
                + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#> \n"
                + "PREFIX dct:   <http://purl.org/dc/terms/> \n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> \n"
                + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#> \n"
                + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/> \n"
                + "PREFIX dcat:  <http://www.w3.org/ns/dcat#> \n"
                + "PREFIX prov:  <http://www.w3.org/ns/prov#> \n"
                + "\n"
                + "SELECT ?exactmatch from <http://tbx2rdf.lider-project.eu/> WHERE { \n"
                + "    ?s ontolex:entry ?entry .\n"
                + "    ?entry ontolex:sameAs ?exactmatch .\n"
                + "    FILTER (?entry = <"+url+">)\n"
                + "} LIMIT 100";
    }

    public static String getTermDetailSpqlByUrl(String url) {

        return ontoLexPrefix
                + "\n"
                + "SELECT ?lang ?rep ?entity ?linkedTermUrl from <http://tbx2rdf.lider-project.eu/> WHERE { \n"
                + "?entity ontolex:canonicalForm ?canform .\n"
                + "?canform ontolex:writtenRep ?rep .\n"
                + "?lang rdf:type ontolex:Lexicon .\n"
                + "?lang ontolex:entry ?entity .\n"
                + "OPTIONAL {?entity ontolex:sameAs ?linkedTermUrl .}"
                + "FILTER regex(str(?entity), \"" + url + "\") ."
                + "\n"
                + "} LIMIT 100";
    }
    
   

  

    public static String getTermDetailbyUrlSpqlVer1(String url) {
        url="<http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN>";
        
        return "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "\n"
                + "PREFIX cc:    <http://creativecommons.org/ns#> \n"
                + "PREFIX void:  <http://rdfs.org/ns/void#> \n"
                + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#> \n"
                + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n"
                + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#> \n"
                + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#> \n"
                + "PREFIX dct:   <http://purl.org/dc/terms/> \n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
                + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> \n"
                + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#> \n"
                + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/> \n"
                + "PREFIX dcat:  <http://www.w3.org/ns/dcat#> \n"
                + "PREFIX prov:  <http://www.w3.org/ns/prov#> \n"
                + "\n"
                + "SELECT ?lang  ?writtenRep  ?entry from <http://tbx2rdf.lider-project.eu/> WHERE { \n"
                + "    ?s ontolex:entry ?entry .\n"
                + "    ?entry ontolex:language ?language .\n"
                + "    ?entry rdf:type ?rdftype .\n"
                + "    ?entry ontolex:canonicalForm ?canonical .\n"
                + "    ?entry ontolex:sense ?sense .\n"
                + "    ?lang ontolex:entry ?entity .\n"
                + "    ?canonical ontolex:writtenRep ?writtenRep .\n"
                + "    FILTER regex(str(?entry), \"" + url + "\") ."
                //+ "    FILTER (?entry = "+getWithinQuote("<http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN>")+")"
                + "\n"
                + "} LIMIT 100";
        
      

    }
    
     private static String getWithinQuote(String term) {
        return "\"" + term + "\"";
    }

    //suppose to be work quick
    public static String getTermDetailbyUrlSpqlVer2(String url) {
        return "PREFIX cc:    <http://creativecommons.org/ns#> "
                + "\n"
                + "PREFIX void:  <http://rdfs.org/ns/void#> "
                + "\n"
                + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>"
                + "\n"
                + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"
                + "\n"
                + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#>"
                + "\n"
                + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#>"
                + "PREFIX dct:   <http://purl.org/dc/terms/>"
                + "\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "\n"
                + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>"
                + "\n"
                + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#>"
                + "\n"
                + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/>"
                + "\n"
                + "PREFIX dcat:  <http://www.w3.org/ns/dcat#>"
                + "\n"
                + "PREFIX prov:  <http://www.w3.org/ns/prov#>"
                + "\n"
                + "SELECT ?localTermUrl ?rep ?linkedTermUrl WHERE { "
                + "?s ?p ?localTermUrl ."
                + "?localTermUrl ontolex:canonicalForm ?canform ."
                + "?canform ontolex:writtenRep ?rep ."
                + "?lang rdf:type ontolex:Lexicon .\n"
                + "?lang ontolex:entry ?localTermUrl .\n"
                + "OPTIONAL {?localTermUrl ontolex:sameAs ?linkedTermUrl .}"
                + "FILTER regex(str(?localTermUrl), \"" + url + "\") ."
                + "} LIMIT 100";

    }

    public static String getTermLinksTest(String url) {
        return "PREFIX cc:    <http://creativecommons.org/ns#> "
                + "\n"
                + "PREFIX void:  <http://rdfs.org/ns/void#> "
                + "\n"
                + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>"
                + "\n"
                + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"
                + "\n"
                + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#>"
                + "\n"
                + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#>"
                + "PREFIX dct:   <http://purl.org/dc/terms/>"
                + "\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "\n"
                + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>"
                + "\n"
                + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#>"
                + "\n"
                + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/>"
                + "\n"
                + "PREFIX dcat:  <http://www.w3.org/ns/dcat#>"
                + "\n"
                + "PREFIX prov:  <http://www.w3.org/ns/prov#>"
                + "\n"
                + "SELECT * from <http://tbx2rdf.lider-project.eu/> WHERE {"
                + "?s ontolex:entry ?entry ."
                + "?entry ontolex:sameAs ?exactmatch ."
                + "FILTER (?entry = <" + url + ">)"
                + "} LIMIT 100";

    }

    /*const linked_terms_sparql=`PREFIX cc:    <http://creativecommons.org/ns#> 
PREFIX void:  <http://rdfs.org/ns/void#> 
PREFIX skos:  <http://www.w3.org/2004/02/skos/core#> 
PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> 
PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#> 
PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#> 
PREFIX dct:   <http://purl.org/dc/terms/> 
PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> 
PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> 
PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#> 
PREFIX odrl:  <http://www.w3.org/ns/odrl/2/> 
PREFIX dcat:  <http://www.w3.org/ns/dcat#> 
PREFIX prov:  <http://www.w3.org/ns/prov#> 

SELECT * from <http://tbx2rdf.lider-project.eu/> WHERE { 
    ?s ontolex:entry ?entry .
    ?entry ontolex:sameAs ?exactmatch .
    FILTER (?entry = <${obj.iri}>)
} LIMIT 100`;*/

 /*"PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>\n" +
"PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
"\n" +
"PREFIX cc:    <http://creativecommons.org/ns#> \n" +
"PREFIX void:  <http://rdfs.org/ns/void#> \n" +
"PREFIX skos:  <http://www.w3.org/2004/02/skos/core#> \n" +
"PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#> \n" +
"PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#> \n" +
"PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#> \n" +
"PREFIX dct:   <http://purl.org/dc/terms/> \n" +
"PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" +
"PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#> \n" +
"PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#> \n" +
"PREFIX odrl:  <http://www.w3.org/ns/odrl/2/> \n" +
"PREFIX dcat:  <http://www.w3.org/ns/dcat#> \n" +
"PREFIX prov:  <http://www.w3.org/ns/prov#> \n" +
"\n" +
"SELECT * from <http://tbx2rdf.lider-project.eu/> WHERE { \n" +
"    ?s ontolex:entry ?entry .\n" +
"    ?entry ontolex:language ?language .\n" +
"    ?entry rdf:type ?rdftype .\n" +
"    ?entry ontolex:canonicalForm ?canonical .\n" +
"    ?entry ontolex:sense ?sense .\n" +
"    ?canonical ontolex:writtenRep ?writtenRep .\n" +
"    FILTER (?entry = <http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN>)\n" +
"} LIMIT 100";*/
    
      /*return "PREFIX cc:    <http://creativecommons.org/ns#> "
                + "\n"
                + "PREFIX void:  <http://rdfs.org/ns/void#> "
                + "\n"
                + "PREFIX skos:  <http://www.w3.org/2004/02/skos/core#>"
                + "\n"
                + "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"
                + "\n"
                + "PREFIX tbx:   <http://tbx2rdf.lider-project.eu/tbx#>"
                + "\n"
                + "PREFIX decomp: <http://www.w3.org/ns/lemon/decomp#>"
                + "PREFIX dct:   <http://purl.org/dc/terms/>"
                + "\n"
                + "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
                + "\n"
                + "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>"
                + "\n"
                + "PREFIX ldr:   <http://purl.oclc.org/NET/ldr/ns#>"
                + "\n"
                + "PREFIX odrl:  <http://www.w3.org/ns/odrl/2/>"
                + "\n"
                + "PREFIX dcat:  <http://www.w3.org/ns/dcat#>"
                + "\n"
                + "PREFIX prov:  <http://www.w3.org/ns/prov#>"
                + "\n"
                + "SELECT ?lang ?rep ?localTermUrl ?linkedTermUrl WHERE { "
                + "?s ?p ?localTermUrl ."
                + "?localTermUrl ontolex:canonicalForm ?canform ."
                + "?canform ontolex:writtenRep ?rep ."
                + "OPTIONAL {?localTermUrl ontolex:sameAs ?linkedTermUrl .}"
                + "FILTER regex(str(?localTermUrl), \"" + url + "\") ."
                + "} LIMIT 100";*/
        

}
