`use strict`;

const multer  = require('multer')
const upload = multer({ dest: 'uploads/' /*, limits: { fileSize: 1024*1024*5000 }*/ })
const mstatus = require("./mstatus");
const streamExec = require("./streamexec");
const server_utils = require("./server_utils");
const curFilepath = "/tmp/server/uploads/current.tbx";
const conversionTarget = "/tmp/server/uploads/current.ttl";
const turtleTarget = "/tmp/server/uploads/current.ttl.gz";
const fs = require("fs");
const fsp = require("fs").promises;

var setTitle = "dataset";

module.exports = exports = function(app) {

    async function tbx2rdf(data) {
        mstatus.logstatus("starting tbx2rdf", curFilepath);

        data['status'] = 'active';
        data['filename'] = curFilepath;

        try {
            await fsp.unlink(conversionTarget);
        } catch (ignored) {}

        /*var data = await fsp.readFile(curFilepath);
        data = data.toString();
        console.log(data); */

        const cmdExec = "java";
        // const cmdArgs = ["-Xms512M", "-Xmx90G", "-jar", "/tmp/target/tbx2rdf-0.1.jar", curFilepath, "--lenient=true", "--output=" + conversionTarget];
        //const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/target/tbx2rdf-0.3.jar", curFilepath, "--lenient=true", "--big=true", "--output=" + conversionTarget];
        const cmdArgs = ["-Xms512M", "-Xmx20G", "-jar", "/tmp/target/tbx2rdf-0.4.jar","conversion" ,curFilepath, "--lenient=true", "--big=false", "--output=" + conversionTarget];
        if ('datanamespace' in data && data['datanamespace']) {
            cmdArgs.push("--datanamespace=" + data['datanamespace']);
        }
        console.log(data);
        console.log(cmdArgs);
        const execOptions = {cwd: "/tmp"}; //, stdout: process.stderr, stderr: process.stderr};
        data['status'] = 'active';
        mstatus.logstatus("starting execution of tbx2rdf");
        var result = null;

        try { 
            result = await streamExec("tbx2rdf", cmdExec, cmdArgs, execOptions);
            data.exit_code = result.code;
            if (data.exit_code != 0) {
                throw Error("exit code != 0");
            }
            mstatus.logstatus("tbx2rdf exit code " + result.code);
            if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
            if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
        } catch (errconv) {
            mstatus.logstatus("tbx2rdf error:", errconv.message, errconv);
            data.error = errconv;
            data.status = 'failed';
            return;
        }
        data.status = 'success';

        data.output = { "path": conversionTarget, "stat": !!(await fsp.stat(conversionTarget)) };
        data.links.push({'path': './files/current.ttl', 'title': 'RDF file', "type": "download"});

        data.output = { "path": turtleTarget, "stat": !!(await fsp.stat(turtleTarget)) };
	data.links.push({'path': './files/current.ttl.gz', 'title': 'zipped turtle file', "type": "download"});
	
        /*var data = await fsp.readFile(conversionTarget);
        data = data.toString();
        console.log(data);*/
        return true;
    }

async function rdf2gzip(data) {
	data.status = 'active';
	//const cmd = "rapper -o ntriples " + conversionTarget + " | gzip >> " + turtleTarget;
	//const execOptions = {cwd: "/tmp", shell: true};
	//const cmd = "gzip -f " + conversionTarget;
	
       /* const cmd = "gzip  -f + conversionTarget;
	const cmdArgs = []
	const execOptions = {cwd: "/tmp/server", "shell": true}; //, stdout: process.stderr, stderr: process.stderr};
	var result = null;
	mstatus.logstatus("starting execution of rdf2gzip");

	try { 
		result = await streamExec("rdf2gzip", cmd, cmdArgs, execOptions);
		data.exit_code = result.code;
		if (data.exit_code != 0) {
			throw Error("exit code != 0");
		}
		mstatus.logstatus("rdf2gzip exit code " + result.code);
		// data.message = "123\n4541\nimi1ko"
		// throw Error("test");
		
		if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
		if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
	} catch (errconv) {
		mstatus.logstatus("rdf2gzip error:", errconv);
		data.error = errconv;
		data.status = 'failed';
		return;
	}

	data.status = 'success';
	data.output = { "path": turtleTarget, "stat": !!(await fsp.stat(turtleTarget)) };
	data.links.push({'path': './files/current.ttl.gz', 'title': 'zipped turtle file', "type": "download"});
	*/
         return true;
}

async function rdf2nt(data) {
	data.status = 'active';
	const cmdExec = "php";
	const cmdArgs = ["convert.php"];
	const execOptions = {cwd: "/tmp/server"}; //, stdout: process.stderr, stderr: process.stderr};
	//const cmd = "rapper -o ntriples " + conversionTarget + " | gzip >> " + turtleTarget;
	//const execArgs = []
	//const execOptions = {cwd: "/tmp", shell: true};
	var result = null;
	mstatus.logstatus("starting execution of rdf2nt");

	try { 
		result = await streamExec("rdf2nt", cmdExec, cmdArgs, execOptions);
		data.exit_code = result.code;
		if (data.exit_code != 0) {
			throw Error("exit code != 0");
		}
		mstatus.logstatus("rdf2nt exit code " + result.code);
		// data.message = "123\n4541\nimi1ko"
		// throw Error("test");
		
		if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
		if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
	} catch (errconv) {
		mstatus.logstatus("rdf2nt error:", errconv);
		data.error = errconv;
		data.status = 'failed';
		return;
	}
	data.status = 'success';
	data.output = { "path": turtleTarget, "stat": !!(await fsp.stat(turtleTarget)) };
	data.links.push({'path': './files/current.nt.gz', 'title': 'n-triples file', "type": "download"});
	return true;
}

async function writeVirtuosoBackend(data) {
	data.status = 'success';
	return true;
}

async function startVirtuosoServer(data) {
	mstatus.logstatus("starting data import", turtleTarget);
	
	data['status'] = 'active';
	data['filename'] = turtleTarget;
    // isql-v 1111 dba dba VERBOSE=OFF import.db
	const cmdExec = "isql-v";
	const cmdArgs = ["1111", "dba", "dba", "VERBOSE=OFF", "import.db"];
	const execOptions = {cwd: "/tmp/server"}; //, stdout: process.stderr, stderr: process.stderr};
	data['status'] = 'active';
	mstatus.logstatus("starting execution of import isql");
	var result = null;

	try { 
		result = await streamExec("isql", cmdExec, cmdArgs, execOptions);
		data.exit_code = result.code;
		if (data.exit_code != 0) {
			throw Error("exit code != 0");
		}
		mstatus.logstatus("isql exit code " + result.code);
		if (result.stdcache.stdout) { data.stdout = result.stdcache.stdout; }
		if (result.stdcache.stderr) { data.stderr = result.stdcache.stderr; }
	} catch (errconv) {
		mstatus.logstatus("isql error:", errconv.message, errconv);
		data.error = errconv;
		data.status = 'failed';
		return;
	}
	data.status = 'success';

	data.output = { "path": turtleTarget, "stat": !!(await fsp.stat(turtleTarget)) };
	data.links.push({'path': './list', 'title': 'entity list', "type": "exthref"});
	data.links.push({'path': './sparql', 'title': 'SPARQL endpoint', "type": "exthref"});
	return true;
}

const pipeline = [
	{"name": "tbx to rdf",
	"function": tbx2rdf,
    "indicator": "/tmp/server/uploads/tbx2rdf.done"},
    /*{"name": "rdf to ttl.gz",
    "function": rdf2gzip,
    "indicator": "/tmp/server/uploads/rdf2gzip.done"},*/
	{"name": "virtuoso backend configuration",
	"function": writeVirtuosoBackend,
    "indicator": "/tmp/server/uploads/virt_backend.done"},
	{"name": "data import",
	"function": startVirtuosoServer,
    "indicator": "/tmp/server/uploads/virt_import.done"}
];
//	{"name": "rdf to ntriples",
//	"function": rdf2nt},

function checkIndicatorFile(dataElement) {
    if (!dataElement || !dataElement.indicator) {
        return false;
    }
    
    try {
        dataElement.indicatorFound = fs.existsSync(dataElement.indicator);
    } catch (ignore) {
        dataElement.indicatorError = ignore;
    }

    if (dataElement.indicatorFound) {
        dataElement.status = 'success';
        return true;
    }
    return false;
}

async function touchIndicator(dataElement) {
    if (!dataElement || !dataElement.indicator) {
        return false;
    }

    return await server_utils.touchFile(dataElement.indicator);
}

function initialize_pipeline() {
	mstatus.statusInformation.pipeline = {'active': false, 'current': 0, 'length': 0, 'elements': []};
	for (let processStep = 0, processSteps = pipeline.length; processStep < processSteps; processStep++) {
		var pipelineElement = pipeline[processStep];
		mstatus.statusInformation.pipeline.length = processSteps;
		mstatus.statusInformation.pipeline.elements.push(pipelineElement.name);
			
        if (!mstatus.statusInformation.hasOwnProperty(pipelineElement.name)) {
		    mstatus.statusInformation[pipelineElement.name] = {'id': processStep, 'status': 'initializing', 'links': []};
		}
		var dataElement = mstatus.statusInformation[pipelineElement.name];
        dataElement['indicator'] = pipelineElement.indicator || null;
        dataElement.indicatorFound = false;
        checkIndicatorFile(dataElement);
	}

}

initialize_pipeline();
mstatus.logstatus("processing pipeline loaded");

async function startConversion() {
	try {
		initialize_pipeline();
		mstatus.logstatus("processing pipeline started");
		mstatus.statusInformation.pipeline.active = true;
		var docontinue = true;
		for (let processStep = 0, processSteps = pipeline.length; docontinue && processStep < processSteps; processStep++) {
			var pipelineElement = pipeline[processStep];
			if (!mstatus.statusInformation.hasOwnProperty(pipelineElement.name)) {
				mstatus.statusInformation[pipelineElement.name] = {'id': processStep, 'status': 'initializing', 'links': []};
			}
			var dataElement = mstatus.statusInformation[pipelineElement.name];

            if (!dataElement) {
                dataElement = {}
            }
            if (mstatus.statusInformation.state) {
                let state_keys = Object.keys(mstatus.statusInformation.state);

                for (let i = 0; i < state_keys.length; i++) {
                    let key = state_keys[i];
                    dataElement[key] = mstatus.statusInformation.state[key];
                }
            }

			mstatus.statusInformation.pipeline.current = processStep;
			if (!pipelineElement['function']) {
				throw Error("pipeline misconfigured: no function for step " + pipelineElement.name);
			}

			dataElement.time_start = +(new Date());
			try {
				mstatus.logstatus("starting pipeline step", pipelineElement.name);
				docontinue = await pipelineElement['function'](dataElement);
				mstatus.logstatus("pipeline step", pipelineElement.name, "done");

                // persist this pipeline step through indicator file
                await touchIndicator(dataElement);
			} catch (plErr) {
				docontinue = false;
				mstatus.logstatus("pipeline step " + processStep + " failed", plErr);
				dataElement.status = 'failed';
			}

			dataElement.time_end = +(new Date());
			dataElement.duration = (dataElement.time_end - dataElement.time_start) / 1000.0;

			if (dataElement && dataElement.status === 'active') {
				dataElement.status = 'done';
			}
		}
/*		var docontinue = false;
		docontinue = await tbx2rdf();
		if (docontinue) {
			docontinue = await rdf2nt();
		}
		if (docontinue) {
			docontinue = await writeVirtuosoBackend();
		}
*/
		mstatus.statusInformation['status'] = 'pipeline-complete';
		mstatus.statusInformation.pipeline.active = false;
	} catch (err) {
		mstatus.statusInformation['status'] = 'error';
		mstatus.statusInformation['error'] = err;
		mstatus.statusInformation.pipeline.active = false;
		mstatus.logstatus("error", err);
	}
}

function getFile(req, fieldname) {
    if (!req || !req.files || !req.files.length) {
        return null;
    }
    for (let idx = 0; idx < req.files.length; idx++) {
        let f = req.files[idx];
        if (f && f.fieldname && f.fieldname === fieldname) {
            return f;
        }
    }
    return null;
}

app.get("/sparql", async (req, res, next) => {
     res.redirect('http://localhost:8080/status?view=sparql');
});

app.get("/list", async (req, res, next) => {
     res.redirect('http://localhost:8080/describe');
});

app.get("/initialize", async (req, res, next) => {
    res.status(401).send('invalid method, endpoint requires a POST request');
});



//var AdmZip = require('adm-zip');
/* app.get("/files/current.ttl.gz", async (req, res, next) => {
    	console.log(" inside into...../files/current.ttl.gz");

//const conversionTarget = "/tmp/server/uploads/current.ttl";
//const turtleTarget = "/tmp/server/uploads/current.ttl.gz";


     const zip = new AdmZip();
     zip.addLocalFile(conversionTarget);
    
 
    // Define zip file name
    const downloadName = `current.ttl.zip`;
 
    const data = zip.toBuffer();
 
    // save file zip in root directory
     zip.writeZip("/tmp/server/uploads/"+downloadName);
    
    // code to download zip file
 
    res.set('Content-Type','application/octet-stream');
    res.set('Content-Disposition',`attachment; filename=${downloadName}`);
    res.set('Content-Length',data.length);
    res.send(data);

        const cmd = "gzip -f + conversionTarget;
        const cmdArgs = []
        const execOptions = {cwd: "/tmp/server", "shell": true}; //, stdout: process.stderr, stderr: process.stderr};
        var result = null;
        mstatus.logstatus("starting execution of rdf2gzip");

        try {
                result = await streamExec("rdf2gzip", cmd, cmdArgs, execOptions);
                if (result.code != 0) {
                        throw Error("exit code != 0");
                }
                mstatus.logstatus("rdf2gzip exit code " + result.code);
                // data.message = "123\n4541\nimi1ko"
                // throw Error("test");

        } catch (errconv) {
                mstatus.logstatus("rdf2gzip error:", errconv);
               
                return;
        }

});
*/


app.post('/initialize', upload.any(), async (req, res, next) => {
	// req.files hold all uploaded files (upload, optionally mapping)
	// req.body will hold the text fields, if there were any
	try {
		mstatus.statusInformation['status'] = 'initializing';
        mstatus.statusInformation['state'] = {};

        let upload = getFile(req, "upload");
        if (!upload) {
            throw Exception("no upload data in call");
        }
		mstatus.statusInformation['tmp_file'] = upload.path.toString();
		console.log("/initialize processing", upload.path);
		var moveResult = await fsp.rename(upload.path, curFilepath);

        let mapping = getFile(req, "mapping");
        if (mapping) {
            mstatus.statusInformation['tmp_map_file'] = mapping.path.toString();
            console.log("got mapping information", mapping.path, curFilepath + ".mapping");
            var moveResult = await fsp.rename(mapping.path, curFilepath + ".mapping");
        } else {
            mstatus.statusInformation['tmp_map_file'] = null;
        }

		if (req.body && req.body.title) {
			setTitle = req.body.title.substring(0, 100);
		}
        if (req.body && req.body.datanamespace) {
            mstatus.statusInformation.state.datanamespace = req.body.datanamespace;
        }

		res.send("/initialize converting now. check /status");
		mstatus.statusInformation['status'] = 'conversion-init';
		setTimeout(startConversion, 0);
	} catch (e) {
		next(e);
	}
})

}



