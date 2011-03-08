var module = {
	name: "MD5/SHA-1 hash detection",
	type: "response-processor"
};

function run() {
	var url = this.httpRequest.getRequestLine().getUri();
	var md5r = new RegExp("[^a-f0-9][a-f0-9]{32,32}[^a-f0-9]", "g");
	var sha1r = new RegExp("[a-f0-9]{40,40}", "g");

	var md5 = md5r.exec(response.bodyAsString);
	var sha1 = sha1r.exec(response.bodyAsString);
	
	if (md5) {
		for (i=0;i<=md5.length-1;i+=1) {
			md5[i] = md5[i].substring(1, 33);
		}
	}

	if (md5) {
		model.set(url+".md5", md5.join("\n"));
		model.alert("vinfo-hash-md5", {"output": md5.join("\n"), "resource": httpRequest.requestLine.uri} );
	}
	if (sha1) {
		model.set(url+".sha1", md5.join("\n"));
		model.alert("vinfo-hash-sha1", {"output": sha1.join("\n"), "resource": httpRequest.requestLine.uri} );
	}
}
