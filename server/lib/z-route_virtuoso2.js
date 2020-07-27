`use strict`;
var http = require('http');
const htmlDir = "/tmp/server/uploads/browser/";
const streamExec    = require("./streamexec");
const server_utils  = require("./server_utils");
const sparql_utils  = require("./sparql_utils");
const route_linking = require("./route_linking");
const fs = require("fs");
const fsp = require("fs").promises;
const multer  = require('multer')
const upload = multer({ dest: 'uploads/' /*, limits: { fileSize: 1024*1024*5000 }*/ })


const express = require('express');
const bodyParser = require('body-parser');

const app = express();
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

console.log(req.query.page);
res.sendFile(htmlDir+'browser_en_C_D_1.html');

});


app.get("/describe", async (req, res, next) => {

var result = null;
var indexFile='ListofTerm.html';
var local_sparql_endpoint = "http://localhost:8890/sparql";

//termporay coce for recent demo
//const termlistResult = termlist(sparql_utils.sparql_result(await sparql_utils.performSPARQL(term_query)));
//console.log(termlistResult);

const cmdExec = "java";
const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/server/uploads/tbx2rdf-0.3.4.jar", local_sparql_endpoint, htmlDir,termlistResult];
const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};

        try {
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
            } catch (errconv) {
             console.log("java -jar does not work!!"+errconv);
            }


res.sendFile(htmlDir+'ListofTerm.html');


	
//var name = 'hello';
//  res.render(htmlDir+'test.html', {name:name}); 
	/*var demoHtml = '<form id        =  "uploadForm" ' +
            ' enctype   =  "multipart/form-data" ' +
            ' action    =  "./initialize" ' +
            ' method    =  "post"> ' +
            ' <input type="file" name="upload" placeholder="tbx file" required /> ' +
            ' <input type="file" name="mappings" placeholder="mappings" /> ' +
            ' <input type="text" name="graphname" placeholder="graph name" /> ' +
            ' <input type="submit" value="Upload file" name="submit"> ' +
            ' </form>';
        
        res.send(demoHtml)*/

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




}

