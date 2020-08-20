`use strict`;

const spawn = require('child_process').spawn;
const mstatus = require("./mstatus");

module.exports = exports = async function streamExec(tag, cmd, args, options) {
	return new Promise(function (resolve, reject) {
		const proc = spawn(cmd, args, options);
		const stdcache = {stdout: [], stderr: []}

		proc.stdout.on('data', (data) => {
			data = data.toString().trim();
			if (data == '') return;
			data.split("\n").forEach(function(line) {
				if (line === '') return;
				mstatus.logstatus(`[${tag}|stdout] ${line}`);
				stdcache.stdout.push(line);
			});
		});
		proc.stderr.on('data', (data) => {
			data = data.toString().trim();
			if (data == '') return;
			data.split("\n").forEach(function(line) {
				if (line === '') return;
				mstatus.logstatus(`[${tag}|stderr] ${line}`);
				stdcache.stderr.push(line);
			});
		});
		proc.on('exit', function (code) {
			mstatus.logstatus(`[${tag}|exit] code: ${code}`);
			resolve({code: code, stdcache: stdcache});
		});
		proc.on('error', function (err) {
			reject(err);
		});
	});
}

