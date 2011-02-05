var module = {
	name: "Authentication failure/credential detection",
	// type: "response-processor"
        type: "disabled"
};

function run() {
	var url = this.httpRequest.getRequestLine().getUri();
	var x = response.code.toString();
	var regex = new RegExp("://.*:.*@", "ig");

	if (x.indexOf("401")>=0) {
		model.set(url+".basicauthhttp", url);
		model.alert("vhttp-basicauthhttp", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri} );
	}
	if (regex.exec(response.bodyAsString)) {
		model.set(url+".authurl", url);
		model.alert("vhttp-basicauthcreds", {"output": url, "resource": httpRequest.requestLine.uri} );
	}
}
