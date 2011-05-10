var module = {
	name: "WSDL Detector",
	type: "response-processor"
};

function run(request, response, ctx) {

	wsdl = "<wsdl:";

	if(response.bodyAsString.indexOf(wsdl)>=0) {
		ctx.alert("vinfo-wsdl",request, response, {"output": response.bodyAsString, "resource": request.requestLine.uri, response: response } );
	}
}
