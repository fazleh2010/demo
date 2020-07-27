`use strict`;
const proxy = require('express-http-proxy'); // https://www.npmjs.com/package/express-http-proxy
const jsdom = require("jsdom"); // https://www.npmjs.com/package/jsdom
const fs = require("fs");
const jquery = require("jquery");
const sparql_utils = require("./sparql_utils");

module.exports = exports = function(app) {
    const uri_virtuoso = "http://localhost:8890/";
    const uri_pubby = "http://localhost:9000/";

    function needs_rewrite(uri) {
        if (!uri) { return false; }
        uri = uri.toLowerCase().trim();
        if (uri.startsWith("http://localhost") || uri.startsWith("https://localhost")) { return true; }
        if (uri.startsWith("http:") || uri.startsWith("https:")) { return false; }
        return true;
    }

    function rewrite_uri(base_uri, prefix, uri) {
        if (!uri) { return uri; }

        if (uri.startsWith("http://localhost") || uri.startsWith("https://localhost")) {
            uri = uri.replace("http://localhost", "").replace("https://localhost", "");
            if (uri.indexOf("/") > -1) {
                uri = uri.substring(uri.indexOf("/"));
            }
        }
        if (base_uri && !uri.startsWith("/")) {
            return uri;
        }

        if (!uri.startsWith("/")) {
            return uri;
        }
        if (prefix.endsWith("/")) {
            prefix = prefix.substring(0, prefix.length - 1);
        }

        return prefix + uri;
    }

    function rewriteVirtuoso (proxyRes, proxyResData, userReq, userRes) {

        console.log("preq request headers", userReq.headers);
        console.log("preq response headers", proxyRes.headers);
        let rewritePrefix = null;
        if (userReq && userReq.headers && userReq.headers['x-context']) {
            rewritePrefix = userReq.headers['x-context'];
        }
        if (!rewritePrefix) {
            return proxyResData;
        }

        if (proxyRes && proxyRes.headers && proxyRes.headers['content-type'] && proxyRes.headers['content-type'].indexOf("text/html") > -1) {
            let data = proxyResData.toString('utf8');

            const dom = new jsdom.JSDOM(data);
            const $ = jquery(dom.window);

            let base_uri = null;
            $("base").each(function(idx, elem) {
                if (!elem) return;
                let $elem = $(elem);
                let href = $elem.attr("href");
                if (!href) { return; }
                base_uri = href;
            });

            if (base_uri) {
                // make protocol relative
                if (base_uri.startsWith("http://")) {
                    base_uri = "//" + base_uri.substring("http://".length);
                }
                if (base_uri.startsWith("https://")) {
                    base_uri = "//" + base_uri.substring("https://".length);
                }
                // add in rewritePrefix
                if (rewritePrefix) {
                    let bu_delim = base_uri.indexOf("/", 3);
                    if (bu_delim > -1) {
                        let base_rewrite = rewritePrefix;
                        if (base_rewrite.endsWith("/")) {
                            base_rewrite = base_rewrite.substring(0, base_rewrite.length - 1);
                        }
                        base_uri = base_uri.substring(0, bu_delim) + base_rewrite + base_uri.substring(bu_delim);
                    }
                }

                $("base").each(function(idx, elem) {
                    let $elem = $(elem);
                    $elem.attr('href', base_uri);
                });
            }

            function rewrite_href(idx, elem){
                let $elem = $(elem);
                if ($elem.attr("href")) {
                    let href = $elem.attr("href");

                    if (elem.tagName === "BASE") {
                        if (href.startsWith("http:")) {
                            href = href.substring("http:".length);
                        }
                        if (href.startsWith("https:")) {
                            href = href.substring("https:".length);
                        }
                        $elem.attr("href", href);
                        return;
                    }

                    if (!needs_rewrite(href)) { return; }

                    href = rewrite_uri(base_uri, rewritePrefix, href);
                    $elem.attr("href", href);
                }
            }

            function rewrite_src(idx, elem){
                let $elem = $(elem);
                if ($elem.attr("src")) {
                    let imgsrc = $elem.attr("src");
                    if (!needs_rewrite(imgsrc)) { return; }

                    imgsrc = rewrite_uri(base_uri, rewritePrefix, imgsrc);
                    $elem.attr("src", imgsrc);
                }
            }

            function rewrite_action(idx, elem){
                let $elem = $(elem);
                if ($elem.attr("action")) {
                    let actionuri = $elem.attr("action");
                    if (!needs_rewrite(actionuri)) { return; }

                    actionuri = rewrite_uri(base_uri, rewritePrefix, actionuri);
                    $elem.attr("action", actionuri);
                }
            }

            $("a").each(rewrite_href);
            $("link").each(rewrite_href);
            $("img").each(rewrite_src);
            $("script").each(rewrite_src);
            $("form").each(rewrite_action);

            let result_text = dom.window.document.documentElement.outerHTML;

            var urlRegex =/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig;
            var scriptHrefRegex = /href=[\'\"]?.*[\']/ig;
            result_text = result_text.replace(urlRegex, function(uri) {
                if (uri.indexOf(rewritePrefix) > -1) {
                    return uri;
                }

                uri = rewrite_uri(base_uri, rewritePrefix, uri);
                return uri;
            });
            result_text = result_text.replace(scriptHrefRegex, function(txt) {
                if (!txt) return txt;
                if (txt.indexOf(rewritePrefix) > -1) return txt;
                let uri = txt.substring("href='".length, txt.length - 1).trim();
                uri = rewrite_uri(base_uri, rewritePrefix, uri);
                txt = "href='" + uri + "'"
                return txt;
            });

            return result_text;

        }
        return proxyResData;
    }

    // proxy unknown request paths through the proxy middleware for handling in Virtuoso
    // if this fails (Virtuoso not started, Virtuoso does not handle that URI, ...) fallback to a 404
    var proxyVirtuoso = proxy(uri_virtuoso, {
        proxyReqPathResolver: function (req) {
            var reqpath = req.url;
            console.log("virtuoso request:", reqpath);
            return reqpath;
        },
        userResDecorator: rewriteVirtuoso
    });
    var proxyPubby = proxy(uri_pubby, {
        proxyReqPathResolver: function (req) {
            var parts = req.url.split('?');
            var queryString = parts[1];
            var updatedPath = parts[0].replace("/pubby/", "/");
            let respath = updatedPath + (queryString ? '?' + queryString : '');
            return respath;
        }
    });

    function sparql_resultlist(result) {
        if (!result) return null;
        if (result.result) { result = result.result; };
        if (!result || !result.length) { return {}; };
        let transformed = {};

        for (let i = 0, l = result.length; i < l; i++) {
            let obj = result[i];
            Object.keys(obj).forEach(function(k) {
                if (!(k in transformed)) {
                    transformed[k] = [];
                }
                transformed[k].push( obj[k] );
            });
        }

        return transformed;
    }

    async function getDataPage(req_uri) {
        let parts = req_uri.split("/");
        if (!parts || !parts.length) { return null; }

        let req_entity = parts[parts.length - 1];
        console.log(`getDataPage(${req_uri}) => ${req_entity}`);

        let result = {"entity": req_entity, "full": req_uri};


        try {
            result['demosparql'] = await sparql_utils.performSPARQL("PREFIX : <http://tbx2rdf.lider-project.eu/data/atc/>" + "\n" +
                " SELECT ?p1 ?o1 ?cans ?canp ?cano " +"\n" +
                " WHERE { " +"\n" +
                ":acetyldihydrocodeine-EN ?p1 ?o1 ." +"\n" +
                ":acetyldihydrocodeine-EN ontolex:canonicalForm ?cans ." +"\n" +
                "       ?cans ?canp ?cano" +"\n" +
                "}");
        } catch (err) {
            console.log(err);
        }

        try {
            // result['languages'] = sparql_resultlist(await sparql_utils.performSPARQL("SELECT DISTINCT ?language WHERE { ?language rdf:type ontolex:Lexicon .}"));
            result['languages'] = sparql_resultlist(await sparql_utils.performSPARQL("SELECT DISTINCT ?language, COUNT(?entry) AS ?entrycount WHERE { ?language rdf:type ontolex:Lexicon .  ?language ontolex:entry ?entry }"));
        } catch (err) {
            console.log(err);
        }

        return JSON.stringify(result);
    }


    app.use("/", async function (req, res, next) {
        if (req.originalUrl && req.originalUrl.startsWith("/pubby/")) {
            console.log(`dispatching ${req.originalUrl} to pubby proxy`);
            return proxyPubby(req, res, function proxyNext(proxyError) {
                if (proxyError && proxyError instanceof Error) {
                    console.log(`proxy::error ${proxyError.message} (${proxyError.syscall} for ${proxyError.address}:${proxyError.port})`)
                    return next();
                }
            });
        }

        let context_uri = req.headers['x-context'] || "/";
        let req_uri = req.originalUrl;
        if (req_uri && req_uri.startsWith("/data/")) {
            let redir = context_uri;
            if (redir.endsWith("/")) {
                redir = redir.substring(0, redir.length - 1);
            }

            if (context_uri.endsWith("/")) {
                context_uri = context_uri.substring(0, context_uri.length - 1);
            }
            req_uri = "http://" + (req.headers['x-forwarded-host'] || req.headers.host || "tbx2rdf") + context_uri + req_uri;
            redir += "/describe/?url=" + encodeURIComponent(req_uri);

            // check if req_uri is a browser file
            // /data/atc_en_C_D_1.html
            if (req_uri.indexOf("/data/") > -1 && req_uri.endsWith(".html")) {
                let req_filename = req_uri.substring(req_uri.indexOf("/data/") + "/data/".length);
                req_filename = path.join("/tmp/server/uploads/browser/", req_filename)
                console.log("req_filename", req_filename); 

                if (fs.existsSync(req_filename)) {
                    let content = await fsp.readFile(req_filename);
                    content = content.toString();
                    content = content.split("../css/").join("../static/css/");

                    res.write(content);
                }

                // no file found but ends with .html -> raise 404
                return next();
            }

            let datapage = await getDataPage(req_uri);
            if (datapage) {
                res.write(datapage);
                return res.end();
            }

            // redirect to describe uri if it's an entity we do not have data for
            console.log("data redirect", redir);
            return res.redirect(301, redir);
        }

        console.log(`dispatching ${req.originalUrl} (${req.method}, ${req.headers.accept}) to virtuoso proxy`);
        if (req.method === 'POST' && req.originalUrl === '/sparql' && req.headers.accept === 'application/sparql-results+json') {
            console.log("redirecting SPARQL query")

            try {
                const qry = req.body.query;
                const spres = await sparql_utils.performSPARQL(qry);
                const sparqlResult = {results: {bindings: spres.result}, error: spres.error, query: spres.query};
                return res.json(sparqlResult);
            } catch (err) {
                console.error(err.toString());
                return res.status(500).json({"query": req.body.query, "err": err.toString()});
            }
        } 

        if (req.method.toLowerCase() === 'post') {
            req.data = req.body;
        }

        return proxyVirtuoso(req, res, function proxyNext(proxyError) {
            if (proxyError && proxyError instanceof Error) {
                console.log(`proxy::error ${proxyError.message} (${proxyError.syscall} for ${proxyError.address}:${proxyError.port})`)
                return next();
            }
        });
    });


}
