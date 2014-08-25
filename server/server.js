// Server watches for posts to the designated location
// MIT License (c) 2014 Ben Brittain

// TODO: Friends don't let friends use HTTP for personal data
var http = require('http');
var fs = require('fs');
var program = require('commander');

dataDir = "data/"

//var options = {
//    key: fs.readFileSync('config/server.key'),
//    cert: fs.readFileSync('config/server.crt')
//};

var server = http.createServer(function (req, res) {
    var body = '';
    req.setEncoding('utf8');
    if (req.url != settings['path']) {
        res.statusCode = 400;
        return res.end();
    }
    req.on('data', function (chunk) {
        body += chunk;
    })

    req.on('end', function () {
        try {
            res.statusCode = 200;
            var data = JSON.parse(body);
        } catch (error) {
            res.statusCode = 400;
            return res.end('error: ' + error.message);
        }
        var now = new Date();
        fs.writeFile(dataDir + "gps-" + now.toISOString(),
            JSON.stringify(data, null, 2), function(err) {
                if(err) {
                    console.log(err);
                }
                console.log("Upload Successful");
            });
        res.writeHead(200);
        res.end("Success");
    })
})

fs.readdir(dataDir, function(err, files) {
    if (err) {
        fs.mkdir(dataDir, function(err){});
    }
});

var settings = JSON.parse(fs.readFileSync('config/config.json', 'utf8'));
server.listen(settings['port'], settings['host']);
console.log("Started server listening on " +
        settings['host'] + ":" + settings['port'] + settings['path']);
