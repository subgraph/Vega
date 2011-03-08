var module = {
	name: "WSDL Detector",
	type: "response-processor"
};

function run() {

	wsdl = "<wsdl:";

	if(response.bodyAsString.indexOf(wsdl)>=0) {
		model.alert("vinfo-wsdl", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri} );
	}
}
