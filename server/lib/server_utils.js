`use strict`;

const fsp = require('fs').promises;

async function touchFile(filename) {
    try {
        // update timestamp, do not overwrite existing data
        let time = new Date();
        await fsp.utimesSync(filename, time, time);
        return true;
    } catch (ignored) {
        // if the above failed we create an empty file
        try {
            await fsp.writeFile(filename, "");
            return true;
        } catch (write_failed) {
            console.log("could not touch file <" + filename + ">: " + write_failed);
            return false;
        }
    }
}

/*
(async () => {
    console.log("init");
    console.log("a", await touchFile("./tmp1/a"));
    console.log("b", await touchFile("./tmp1/b"));
    console.log("c", await touchFile("./tmp1/c"));
    console.log("done");
})();
*/

module.exports = exports = {
    touchFile: touchFile
}
