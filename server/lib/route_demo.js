'use strict';

module.exports = exports = function(app) {

    app.get('/test', (req, res) => {
        res.send("ok");
    })

    app.get('/demo', (req, res) => {
	    var test = "Hello World";
        var demoHtml = '<!doctype html>'+
'<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">'+
 '<head>'+ 
  '<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">'+ 
  '<meta http-equiv="X-UA-Compatible" content="IE=edge"> '+
  '<meta name="viewport" content="width=device-width, initial-scale=1">'+ 
  '<meta name="_csrf" content="b40c2453-5c92-4fb8-8a48-9329d11c12e3">'+ 
  '<meta name="_csrf_header" content="X-CSRF-TOKEN">'+ 
  '<!--this location is for testing in local machine-->'+ 
  '<link href="../static/css/interface.css" rel="stylesheet" type="text/css">' +
  '<link href="../static/css/oxford.css" rel="stylesheet" type="text/css">' +
  '<link href="../static/css/responsive.css" rel="stylesheet" type="text/css"> '+
  '</head>' + 
  '<body>' +
  '<p>' +test+'</p>'+
  '</body> '+
  '</html>';
        console.log("/demo: serving")
        res.send(demoHtml)
    });


}
