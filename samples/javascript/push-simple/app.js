const http = require('http');
const fs = require('fs');

var server = http.createServer(
	(req, res) => {

		let url = req.url;
		if ('/' == url) {
			fs.readFile('./index.html', 'UTF-8', function(err, data) {
				res.writeHead(200, {
					'Content-Type': 'text/html'
				});
				res.write(data);
				res.end();
			});
		} else if ('/tools.js' == url) {
    		fs.readFile('./tools.js', 'UTF-8', function (err, data) {
      			res.writeHead(200, {'Content-Type': 'text/javascript'});
      			res.write(data); 
      			res.end();
      		});
    	} else if ('/index.js' == url) {
    		fs.readFile('./index.js', 'UTF-8', function (err, data) {
      			res.writeHead(200, {'Content-Type': 'text/javascript'});
      			res.write(data); 
      			res.end();
      		});
    	} else if ('/service-worker.js' == url) {
    		fs.readFile('./service-worker.js', 'UTF-8', function (err, data) {
      			res.writeHead(200, {'Content-Type': 'text/javascript'});
      			res.write(data); 
      			res.end();
      		});
    	}
    });
server.listen(3000);
