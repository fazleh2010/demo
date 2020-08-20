`use strict`;
var http = require('http');

const streamExec    = require("./streamexec");
const server_utils  = require("./server_utils");
const sparql_utils  = require("./sparql_utils");
const route_linking = require("./route_linking");
const route_termbrowser = require("./route_termbrowser");


const fs = require("fs");
const fsp = require("fs").promises;
const multer  = require('multer')
const upload = multer({ dest: 'uploads/' /*, limits: { fileSize: 1024*1024*5000 }*/ })
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const local_sparql_endpoint = "http://localhost:8890/sparql";
const htmlDir = "/tmp/server/uploads/browser/";
const templateDir = "/tmp/server/static/";
//const templateDir = "/tmp/src/java/resources/data/template/";
const inputDir = "/tmp/";
const listOfTerms="ListOfTerms";
const termDetail="TermPage";


app.use(bodyParser.urlencoded({ extended: true }));



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
}`;


module.exports = exports = function(app) {

app.get("/listOfTerms", async (req, res, next) => {
var page=req.query.page; 
console.log(req.query.page);
res.sendFile(htmlDir+page);

});





app.get("/termPage", async (req, res, next) => {
const linkInfo = JSON.parse(req.query.term);
console.log("term!!!!!!!!!!!!!!!!!!!: "+linkInfo.term);
console.log("url!!!!!!!!!!!!!!!!!!!: " +linkInfo.iri);
console.log("lang!!!!!!!!!!!!!!!!!!!: "+linkInfo.lang);

const termlistResult ="";
const cmdExec = "java";
const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar","/tmp/target/tbx2rdf-0.4.jar","html" ,local_sparql_endpoint, htmlDir,termlistResult,inputDir,termDetail,templateDir,req.query.term];
const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};
        try {
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
              if (result.code != 0) {
                 res.sendFile(templateDir+TermPage+'.html');
                 throw Error("exit code != 0");
                      return;
                   }
                else
                   {
                   res.sendFile(htmlDir+termDetail+'.html');
                   }
            if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
            if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
            } catch (errconv) {
             console.log("java -jar does not work!!"+errconv);
             res.sendFile(templateDir+TermPage+'.html');
            return;
            }



/*const term_details_sparql=`PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>
PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>

PREFIX cc:    <http://creativecommons.org/ns#> 
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
FILTER regex(str(?rep), "hole") .
} LIMIT 100`;


    const lookup_result = {};
    const term_details = await sparql_utils.performSPARQL(term_details_sparql);
    lookup_result.term_details = sparql_utils.sparql_result(term_details);
    console.log("termpage:"+term_details);
    console.log("termpage:"+lookup_result.term_details);
*/
});



app.get("/describe", async (req, res, next) => {

var result = null;
const termlistResult = termlist(sparql_utils.sparql_result(await sparql_utils.performSPARQL(term_query)));
const cmdExec = "java";
const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar","/tmp/target/tbx2rdf-0.4.jar","html" ,local_sparql_endpoint, htmlDir,termlistResult,inputDir,listOfTerms,templateDir];
const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};

        try {
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
            console.log("result:",result);
		console.log("template:",htmlDir+"template/");
	      if (result.code != 0) {
		 res.sendFile(templateDir+'ListOfTermsHome.html');
                 throw Error("exit code != 0");
		      return;
                   }
		else
		   {
	            res.sendFile(htmlDir+'browser_en_A_B_1.html');
		   }	

            if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
            if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
            } catch (errconv) {
             console.log("java -jar does not work!!"+errconv);
            return;
            }

});



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

        // console.log("DEBUG-term", entity, lang, rep);
        if (!terms_by_language.hasOwnProperty(lang)) {
            terms_by_language[lang] = [];
        }

        terms_by_language[lang].push({"iri": entity, "rep": rep});
    }

    return terms_by_language;
}

function normalize_for_linking(term) {
    if (!term) { return term; }

    return term.toLowerCase().replace(/[\W]/gi, ' ').replace(/\s+/g,' ')
}

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



}

