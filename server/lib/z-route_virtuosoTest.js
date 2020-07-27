`use strict`;
var http = require('http');
const htmlDir = "/tmp/server/uploads/browser/";
const streamExec = require("./streamexec");
const server_utils = require("./server_utils");
const fs = require("fs");
const fsp = require("fs").promises;
var sparqlEndpoint="sparqlEndpoint";


app.get("/listOfTerms", async (req, res, next) => {
//res.sendFile(htmlDir+'browser_en_C_D_1.html');
});


app.get("/describe", async (req, res, next) => {

var result = null;
var local_sparql_endpoint = "http://localhost:8890/sparql";
var local_sparql_query = "http://localhost:8890/sparql";
const cmdExec = "java";
const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/server/uploads/tbx2rdf-0.3.4.jar", local_sparql_endpoint, htmlDir,local_sparql_query];
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


