'use strict';

module.exports = exports = function(app) {

    app.get('/test', (req, res) => {
        res.send("ok");
    })

    app.get('/demo', (req, res) => {
        var demoHtml = '<form id        =  "uploadForm" ' + 
            ' enctype   =  "multipart/form-data" ' +
            ' action    =  "./initialize" ' + 
            ' method    =  "post"> ' + 
            ' <input type="file" name="upload" placeholder="tbx file" required /> ' +
            ' <input type="file" name="mappings" placeholder="mappings" /> ' +
            ' <input type="text" name="graphname" placeholder="graph name" /> ' +
            ' <input type="submit" value="Upload file" name="submit"> ' +
            ' </form>';
        console.log("/demo: serving")
        res.send(demoHtml)
    });


}
