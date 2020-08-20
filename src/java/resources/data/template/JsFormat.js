`use strict`;
var http = require('http');

const streamExec = require("./streamexec");
const server_utils = require("./server_utils");
const sparql_utils = require("./sparql_utils");
const route_linking = require("./route_linking");
const route_termbrowser = require("./route_termbrowser");
const fs = require("fs");
const fsp = require("fs").promises;
const multer = require('multer')
const upload = multer({dest: 'uploads/' /*, limits: { fileSize: 1024*1024*5000 }*/})
const express = require('express');
const bodyParser = require('body-parser');
const app = express();
const local_sparql_endpoint = "http://localhost:8890/sparql";
const htmlDir = "/tmp/server/uploads/browser/";
const templateDir = "/tmp/server/static/";
//const templateDir = "/tmp/src/java/resources/data/template/";
const inputDir = "/tmp/";
const listOfTerms = "ListOfTerms";
const termDetail = "TermPage";
app.use(bodyParser.urlencoded({extended: true}));

module.exports = exports = function (app) {

    app.get("/listOfTerms", async (req, res, next) => {
        var page = req.query.page;
        console.log(req.query.page);
        res.sendFile(htmlDir + page);

    });


    app.get("/pageNumber", async (req, res, next) => {
        var page = req.query.page;
        console.log("page number!!!!!!!!!!!!!!!!!!!: " + req.query.page);

    });

    app.get("/termPage", async (req, res, next) => {
        const linkInfo = JSON.parse(req.query.term);
        console.log("term!!!!!!!!!!!!!!!!!!!: " + linkInfo.term);
        console.log("url!!!!!!!!!!!!!!!!!!!: " + linkInfo.iri);
        console.log("lang!!!!!!!!!!!!!!!!!!!: " + linkInfo.lang);

        const termlistResult = "";
        const cmdExec = "java";
        const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/target/tbx2rdf-0.4.jar", "html", local_sparql_endpoint, htmlDir, termlistResult, inputDir, termDetail, templateDir, req.query.term];
        const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};
        try {
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
            if (result.code != 0) {
                res.sendFile(templateDir + TermPage + '.html');
                throw Error("exit code != 0");
                return;
            } else
            {
                res.sendFile(htmlDir + termDetail + '.html');
            }
            if (result.stdcache.stdout) {
                data.stdout = result.stdcache.stdout;
            }
            if (result.stdcache.stderr) {
                data.stderr = result.stdcache.stderr;
            }
        } catch (errconv) {
            console.log("java -jar does not work!!" + errconv);
            res.sendFile(templateDir + TermPage + '.html');
            return;
        }

    });



    app.get("/describe", async (req, res, next) => {
        var result = null;
        const termlistResult = "";
        const cmdExec = "java";
        const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/target/tbx2rdf-0.4.jar", "html", local_sparql_endpoint, htmlDir, termlistResult, inputDir, listOfTerms, templateDir];
        const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};


        try {
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
            console.log("result:", result);
            console.log("template:", htmlDir + "template/");
            if (result.code != 0) {
                res.sendFile(templateDir + 'ListOfTermsHome.html');
                throw Error("exit code != 0");
                return;
            } else
            {
                try {
                    if (fs.existsSync(htmlDir + 'browser_en_A_B_1.html')) {
                        console.log("The file exists.");
                        res.sendFile(htmlDir + 'browser_en_A_B_1.html');
                    } else {
                        console.log('The file does not exist.');
                        res.sendFile(templateDir + 'ListOfTermsHome.html');
                    }
                } catch (err) {
                    console.error(err);
                }

            }

            if (result.stdcache.stdout) {
                data.stdout = result.stdcache.stdout;
            }
            if (result.stdcache.stderr) {
                data.stderr = result.stdcache.stderr;
            }
        } catch (errconv) {
            console.log("java -jar does not work!!" + errconv);
            return;
        }

    });


}
