var module = {
	name : "Directory Listing Detection",
	type: "response-processor"
};

function run() {
	var keywords = ["Index of", "Name", "Size", "Parent Directory", "Folder Listing", "Directory Listing"];
	var res = 0;
	var url = this.httpRequest.getRequestLine().getUri();

	for (i=0;i<=keywords.length-1;i+=1) {
		x = response.bodyAsString.indexOf(keywords[i]);
		if (x>=0) {
			res += 1;
		}
	}

	if(url.search("C=") <=0 && res>=2) {
		model.alertWith("vdirlist", null, response, {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri} );
	}
}
