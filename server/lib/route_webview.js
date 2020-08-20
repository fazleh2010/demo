'use strict';
const fs = require("fs");
const fsp = require('fs').promises;

module.exports = exports = function(app) {
    app.get("/status", async function (req, res, next) {
        res.status(200);
        res.set('Content-Type', 'text/html');

        let header = await fsp.readFile("/tmp/server/static/header.html");
        let footer = await fsp.readFile("/tmp/server/static/footer.html");

        let content = null;

        const valid_content = ['status', 'sparql', 'browser', 'search'];
        let requested = req.query.view || "status";
        if (!requested || valid_content.indexOf(requested) === -1) {
            requested = "status";
        }
        content = await fsp.readFile("/tmp/server/static/" + requested + ".html");

        res.write(header);
        if (content) {
            res.write(content);
        }
        res.write(footer);
        res.end();
    });

    app.get("/health_check", (req, res, next) => {
        res.send("OK");
    });

    app.get("/descriptor", (req, res, next) => {
        res.type("application/x-yaml");
        res.sendFile("/tmp/server/static/descriptor.yaml");
    });
}
