`use strict`;

// TODO mstatus::update should merge objects instead of replacing

var statusInformation = {"status": "container-alive", "log": []};
const MAX_LOG_LINES = 20;

function log_timestamp() {
    var str = "";

    var currentTime = new Date()
    var hours = currentTime.getHours()
    var minutes = currentTime.getMinutes()
    var seconds = currentTime.getSeconds()

    if (minutes < 10) {
        minutes = "0" + minutes
    }
    if (seconds < 10) {
        seconds = "0" + seconds
    }
    str += hours + ":" + minutes + ":" + seconds + " ";
    return str;
}

module.exports = exports = {
    statusInformation: statusInformation,
    update: function (key, newvalue) {
        statusInformation[key] = newvalue;
    },
    logstatus: function logstatus(...args) {
        if (!args || !args.length) { return; }

        let line = log_timestamp() + "\t" + args.join(" ");
        statusInformation.log.push(line);
        while (statusInformation.log.length > MAX_LOG_LINES) {
            statusInformation.log.shift();
        }
        console && console.log("status", args.join(" "));
    }
}
