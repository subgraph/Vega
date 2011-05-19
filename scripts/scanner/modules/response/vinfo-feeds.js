var module = {
	name : "RSS/Atom/OPL Feed Detector",
	type: "response-processor"
};

function run(request, response, ctx) {
	var output = " ";
	var res = 0;
	var rss = new RegExp("<link.*rel=.*rss.*?>", "ig");
	var atom = new RegExp("<link.*rel=.*xml.*?>", "ig");
	var opml = new RegExp("<opml.*version.*?>", "ig");

	var x = rss.exec(response.bodyAsString);
	if (x) { output += x[0]; output += "\n"; res = 1; }
	var y = atom.exec(response.bodyAsString);
	if (y) { output += y[0]; output += "\n"; res = 1; }
	var z = opml.exec(response.bodyAsString);
	if (z) { output += z[0]; output += "\n"; res = 1; }

	if (res) {
		ctx.alert("vinfo-feeds",request, response, {
			"output": output, 
			"resource": request.requestLine.uri, 
			key: "vinfo-feeds" + request.requestLine.uri,
			response: response 
			} );
	}
}
