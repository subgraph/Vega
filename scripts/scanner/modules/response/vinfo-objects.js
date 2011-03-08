var module = {
  name : "Object/Applet Detection",
  type: "response-processor"
};

function run() {
	var keywords = ["<object", "<applet"];
	var res = 0;
	var url = this.httpRequest.getRequestLine().getUri();
	var output = " ";
	for (i=0;i<=keywords.length-1;i+=1) {
		var current = new RegExp(keywords[i] + ".*", "ig");
		x = current.exec(response.bodyAsString);
		if (x) {
			output += x[1];
			output += "\n";
			res = 1;
		}
	}

	if(res) {
		model.set(url +".object.applet", output);
		model.alert("vinfo-objects", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri} );
	}
}
