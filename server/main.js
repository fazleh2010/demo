`use strict`;
const express = require('express')
const app = express()
const util = require('util');
const path = require('path'); 
const bodyParser = require('body-parser');
const fs = require("fs");
const fsp = require('fs').promises;
const process = require('process');

const server_utils = require("./lib/server_utils");
const sparql_utils = require("./lib/sparql_utils");
const port = 8080;

const mstatus = require("./lib/mstatus");

app.use(bodyParser.json() );                        
app.use(bodyParser.urlencoded({extended: true})); 

if (!('toJSON' in Error.prototype))
Object.defineProperty(Error.prototype, 'toJSON', {
    value: function () {
        var alt = {};

        Object.getOwnPropertyNames(this).forEach(function (key) {
            alt[key] = this[key];
        }, this);

        return alt;
    },
    configurable: true,
    writable: true
});

app.set('json spaces', 2);

require("./lib/route_demo")(app);
require("./lib/route_webview")(app);
require("./lib/route_linking")(app);
require("./lib/route_termbrowser")(app);

sparql_utils.setup_routes(app);

app.get("/status.json", async (req, res, next) => {
	try {
		res.json(mstatus.statusInformation);
	} catch (e) {
		next(e)
	}
});

mstatus.logstatus("container initialized");

require("./lib/route_conversion")(app);

app.use('/files', express.static('/tmp/server/uploads/'));

app.use('/static', express.static(path.join(__dirname, 'static'), {
	index: false
}));

require("./lib/route_virtuoso")(app);

app.use(function (req, res, next) {
	var fullUrl = req.protocol + '://' + req.get('host') + req.originalUrl;
	console.log("404", fullUrl);
	console.log(req.url);
	console.log(req.originalUrl);
	console.log(req.path);
	res.status(404).send("404 - Page not found")
})

app.listen(port, '0.0.0.0', () => console.log(`Instance listening on port ${port}!`))


