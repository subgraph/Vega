var module = {
	name : "RSS/Atom/OPL Feed Detector",
	type: "response-processor"
};

function run() {
	var output = " ";
	var res = 0;
	var rss = new RegExp("<link.*rel=.*rss.*?>", "ig");
	var atom = new RegExp("<link.*rel=.*xml.*?>", "ig");
	var opml = new RegExp("<opml.*version.*?>", "ig");

	var x = rss.exec(response.bodyAsString);
	if (x) { output += x[1]; output += "\n"; res = 1; }
	var y = atom.exec(response.bodyAsString);
	if (y) { output += y[1]; output += "\n"; res = 1; }
	var z = opml.exec(response.bodyAsString);
	if (z) { output += z[1]; output += "\n"; res = 1; }

	if (res) {
		model.alert("vinfo-feeds", {"output": output, "resource": httpRequest.requestLine.uri} );
	}
}
