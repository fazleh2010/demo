`use strict`;

// TODOs
// list of terms, language filter

const sparql_utils = require("./sparql_utils");

module.exports = exports = function(app) {

async function countLanguageTerms() {
    const languageCounts = {};
    const langSparqlQuery="SELECT DISTINCT ?language, COUNT(?entry) AS ?entrycount WHERE { ?language rdf:type ontolex:Lexicon .  ?language ontolex:entry ?entry }";
    const tmp = sparql_utils.sparql_result(await sparql_utils.performSPARQL(langSparqlQuery));

    for (let i = 0; i < tmp.length; i++) {
        const row = tmp[i]; 
        let lang = row.language.value.toLowerCase();
        if (lang.indexOf("/") > -1) {
            lang = lang.split("/");
            lang = lang[lang.length -1];
        }
        languageCounts[lang] = parseInt(row.entrycount.value);
    }
	

    return languageCounts;
}

function normalize_for_linking(term) {
    if (!term) { return term; }
        return term.toLowerCase().replace(/[\W]/gi, ' ').replace(/\s+/g,' ')
}


function termlist(terms) {
    let terms_by_language = {};

    for (let termidx = 0, termcount = terms.length; termidx < termcount; termidx++) {
        let termobj = terms[termidx];

        let entity = termobj.entity.value;
        let rep = termobj.rep.value;
        if (!rep) {
            continue;
        }
        let lang = termobj.rep["xml:lang"];
        if (!lang) {
            lang = termobj.lang.value.toLowerCase();
            if (lang.indexOf("/") > -1) {
                lang = lang.split("/");
                lang = lang[lang.length - 1];
            }
        }
        rep = normalize_for_linking(rep);
        console.log("rep normalize_for_linking:",rep);
        // console.log("DEBUG-term", entity, lang, rep);
        if (!terms_by_language.hasOwnProperty(lang)) {
            terms_by_language[lang] = [];
        }

        terms_by_language[lang].push({"iri": entity, "rep": rep});

    }

    return terms_by_language;
}

async function autoComplete(term, termlimit) {
    const term_query = `PREFIX cc:    <http://creativecommons.org/ns#> 
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

SELECT ?entity ?rep ?lang from <http://tbx2rdf.lider-project.eu/> WHERE { 
?entity ontolex:canonicalForm ?canform .
?canform ontolex:writtenRep ?rep .
?lang rdf:type ontolex:Lexicon .
?lang ontolex:entry ?entity .
FILTER regex(str(?rep), "${term}") .
} LIMIT ${termlimit}`;

    const terms = termlist(sparql_utils.sparql_result(await sparql_utils.performSPARQL(term_query)));
    return terms
}

async function lookupTerm(obj) {
    if (typeof(obj) === 'string') {
        obj = JSON.parse(obj);
    }

    if (!obj || !obj.iri || !obj.lang) { return; }
    if (obj.iri.indexOf(" ") > -1) { 
        obj.iri = obj.iri.replace(/ /g, '+');
    }

console.log("obj:"+obj);

console.log("obj.iri:"+obj.iri);  

    const lookup_result = {};
    const term_details_sparql=`PREFIX cc:    <http://creativecommons.org/ns#> 
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
    ?entry ontolex:language ?language .
    ?entry rdf:type ?rdftype .
    ?entry ontolex:canonicalForm ?canonical .
    ?entry ontolex:sense ?sense .
    ?canonical ontolex:writtenRep ?writtenRep .
    FILTER (?entry = <${obj.iri}>)
} LIMIT 100`;

    const term_details = await sparql_utils.performSPARQL(term_details_sparql);
    lookup_result.term_details = sparql_utils.sparql_result(term_details);

 
const linked_terms_sparql=`PREFIX cc:    <http://creativecommons.org/ns#> 
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
} LIMIT 100`;

    lookup_result.linked_terms = sparql_utils.sparql_result(await sparql_utils.performSPARQL(linked_terms_sparql));

    console.log("lookup_result.term_details:"+lookup_result.term_details);
    console.log("lookup_result.linked_terms:"+lookup_result.linked_terms);


    return lookup_result;
}

app.get("/terms", async (req, res, next) => {
    resultobj = {}

    resultobj.languages = await countLanguageTerms();

    if (req.query.complete) {
        resultobj.autocomplete = await autoComplete(req.query.complete, 100);
	//console.log("resultobj.autocomplete",resultobj.autocomplete);
    }

    if (req.query.lookup) {
         console.log("result:"+req.query.lookup);  
	 resultobj.lookup = await lookupTerm(req.query.lookup);
        //console.log("resultobj.lookup:",resultobj.lookup);

    }

     
    return res.json(resultobj);
});

}
