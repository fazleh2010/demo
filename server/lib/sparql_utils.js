'use strict';

const sparql = require('sparql');
    
function sparql_prefixes() {
    return "PREFIX ontolex: <http://www.w3.org/ns/lemon/ontolex#>\n" + 
        "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
        "\n";
}

module.exports = exports = {
    performSPARQL: async function performSPARQL(query,  endpoint="http://localhost:8890/sparql") {
        return new Promise(function (resolve, reject) {
            let result = {query: query || null, result: null, error: null};
            if (!result.query) {
                return next(Error("no query"));
            }

            const is_local = endpoint;

           if (is_local) {
                result.query = sparql_prefixes() + result.query;
           }

            console.log("[SPARQL] endpoint: <" + endpoint + "> query:\n---------\n" + result.query.trim() + "\n---------");
            var client = new sparql.Client(endpoint);

            client.rows(result.query, function (err, qres) {
                if (err) {
                    result.error = err.toString();
                    console.log(err.toString());
                    return reject(err);
                }
                if (qres && qres.length) {
                    result.result = qres;
                    /* if (qres.result && qres.result !== undefined) {
                result.result = qres.result;
                console.log(">>>>>", result.result);
            } else {
                result.result = [];
                for (let i = 0; i < qres.length; i++) {
                    result.result.push(qres[i]);
                }
            }*/
                } else {
                    result.result = [];
                }
                return resolve(result);
            });
        });
    },
    sparql_result: function sparql_result(result) {
        if (!result) return null;
        if (result.result) { result = result.result; };
        if (!result || !result.length) { return {}; };
        let transformed = {};

        let maxlen = -1;

        for (let i = 0, l = result.length; i < l; i++) {
            let obj = result[i];
            Object.keys(obj).forEach(function(k) {
                if (!(k in transformed)) {
                    transformed[k] = [];
                }
                transformed[k].push( obj[k] );
                if (transformed[k].length > maxlen) {
                    maxlen = transformed[k].length;
                }
            });
        }

        let rows = [];

        for (let idx = 0; idx < maxlen; idx++) {
            let row = {}
            Object.keys(transformed).forEach(function(column) {
                row[column] = transformed[column][idx];
            });
            rows.push(row);
        }

        return rows;
    },
    setup_routes: function setup_routes(app) {
        app.post("/sparql.json", async (req, res, next) => {
            try {
                let result = {query: req.body.query || null, result: null, error: null};
                if (!result.query) {
                    return next(Error("no query"));
                }

                console.log("SPARQL request: " + result.query);
                var client = new sparql.Client("http://localhost:8890/sparql");

                client.rows(result.query, function (err, qres) {
                    if (err) {
                        result.error = err.toString();
                    }
                    if (qres && qres.length) {
                        result.result = [];
                        for (let i = 0; i < qres.length; i++) {
                            result.result.push(qres[i]);
                        }
                    }
                    console.log("full result", result);
                    res.json(result);
                });
            } catch (e) {
                next(e)
            }
        });
    }
}
