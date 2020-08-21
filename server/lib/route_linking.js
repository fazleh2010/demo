`use strict`;

// TODO pagination of local content, query remote in batches

const fsp = require('fs').promises;
const streamExec = require("./streamexec");
const mstatus = require("./mstatus");

const sparql_utils = require("./sparql_utils")
const inputDir = "/tmp/";
const default_graph = "http://tbx2rdf.lider-project.eu/";
const insert_file = "/tmp/server/uploads/insert.db";
const insert_file_rdf = "/tmp/server/uploads/current.ttl";


module.exports = exports = function(app) {

app.get("/link", async (req, res, next) => {
    return res.status(401).send('invalid method, endpoint requires a POST request');
});

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

        // console.log("DEBUG-term", entity, lang, rep);
        if (!terms_by_language.hasOwnProperty(lang)) {
            terms_by_language[lang] = [];
        }

        terms_by_language[lang].push({"iri": entity, "rep": rep});
    }

    return terms_by_language;
}

async function isqlExecute(filename) {
    if (!filename) {
        throw Error("isql: no filename to execute")
    }

	const cmdExec = "isql-v";
	const cmdArgs = ["1111", "dba", "dba", "VERBOSE=OFF", filename];
	const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};
	mstatus.logstatus("starting isqlExecute");
	var result = null;

	try { 
		result = await streamExec("isql", cmdExec, cmdArgs, execOptions);
		if (result.code != 0) {
			const resulterror = Error("exit code != 0");
            resulterror.result = result;
            throw resulterror;
		}
		mstatus.logstatus("isql exit code " + result.code);
        return result;
	} catch (errconv) {
		mstatus.logstatus("isql error:", errconv.message, errconv);
        throw errconv;
	}
}


async function insert_match(local_term, remote_term) {
    console.log("insert statement");
    const insert_result = await isqlExecute(insert_file);
    console.log("insert result", !!insert_result);
}

async function dolinking(req, res, next) {
	
	try {
        let linking_results = {};
        linking_results.action = 'link';
        linking_results.error = null;
         
        mstatus.update("linking", {
            "status": "starting"
        });

        let local_sparql_endpoint = "http://localhost:8890/sparql";
        let remote_sparql_endpoint = req.body.endpoint || null;
        if (!remote_sparql_endpoint) {
            linking_results.error = 'endpoint argument missing in request';
        } else {
              
		let local_languages = sparql_utils.sparql_result(await sparql_utils.performSPARQL("SELECT ?language, COUNT(?entry) AS ?entrycount WHERE { ?language rdf:type ontolex:Lexicon .  ?language ontolex:entry ?entry } GROUP BY ?language"));
               console.log("local_languages!!!!!!:",local_languages);

            mstatus.update("linking", {
                "status": "matching",
                "languages": linking_results.languages,
                "matches": 0
            });


           var localLangJson = JSON.stringify(local_languages);
           const cmdExec = "java";
           const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar","/tmp/target/tbx2rdf-0.4.jar","link" ,inputDir,localLangJson,remote_sparql_endpoint,insert_file_rdf,local_sparql_endpoint];
           const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};

           try {
            //res.redirect("/status.json");

           mstatus.statusInformation['status'] = 'linking terminology';
           mstatus.statusInformation['state'] = {};

            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
	    await insert_match("localTerm", "remoteTerm");
            mstatus.update("linking", {
                "status": "complete"
            });

	    
              if (result.code != 0) {
                 throw Error("exit code != 0");
                res.status(500).json({error: "Internal server error"});
                   }
                else
                   {
                    console.log("linking works fine:",
                    res.status(200).json({status:"linking is successfully complete!!!!"}));

                   }

            if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
            if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
            } catch (errconv) {
             console.log("java -jar does not work!!"+errconv);
            
            }
           
        }
    } catch (e) {
	    console.log("error in status field!!!");
        mstatus.update("linking", {
            "status": "error",
            "error": e
        });

        return next(e);
    }
}

app.post("/link", dolinking);
app.put("/link", dolinking);


	           // const linkInfoJson  ='{"localTerm":"hole","localUrl":"http://tbx2rdf.lider-project.eu/data/YourNameSpace/hole-EN","remoteTerm":"hole","remoteUrl":"http://webtentacle1.techfak.uni-bielefeld.de/tbx2rdf_intaglio/data/intaglio/hole-EN"}';


            /*linking_results.languages = {
                "local": local_languages,
                "remote": remote_languages
            }*/


/*async function perform_matching(local_terms, remote_terms) {
    console.log("performing matching");
    console.log("\tlocal languages:", Object.keys(local_terms).length);
    console.log("\tremote languages:", Object.keys(remote_terms).length);

    let total_matches = 0;

    for (let [local_lang, local_lang_terms] of Object.entries(local_terms)) {
        for (let [remote_lang, remote_lang_terms] of Object.entries(remote_terms)) {
            if (local_lang !== remote_lang) {
                continue;
            }
            console.log("[matching] language:", local_lang, "local terms:", local_lang_terms.length, "remote terms:", remote_lang_terms.length);

            const local_termcount = local_lang_terms.length;
            const remote_termcount = remote_lang_terms.length;

            for (let local_i = 0; local_i < local_termcount; local_i++) {
                for (let remote_i = 0; remote_i < remote_termcount; remote_i++) {
                    let local_term = local_lang_terms[local_i];
                    let remote_term = remote_lang_terms[remote_i];
                    if (normalize_for_linking(local_term.rep) === normalize_for_linking(remote_term.rep)) {
			  console.log("local_term: ",local_term);
                          console.log("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                          console.log("remote_term: ",remote_term);
                        if (local_term.iri !== remote_term.iri) {
		            console.log("local_term_uri ",local_term.iri);
		            console.log("remoterm_uri ",remote_term.iri);
                            await insert_match(local_term, remote_term);
                            total_matches++;
                            mstatus.update("linking", {
                                "local_terms": !!local_terms,
                                "remote_terms": !!remote_terms,
                                "status": "matching",
                                "local_term_count": local_termcount,
                                "local_term_current": local_i,
                                "matches": total_matches
                            });

                        }
                        
                    }
                }
            }

        }
    }
}*/

            

}


