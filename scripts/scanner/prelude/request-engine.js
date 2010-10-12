importPackage(org.apache.http.client.methods);

var requestEngine = null;

function sendRequest(req) {
	if(!requestEngine)
		throw new Error("Cannot send request because request engine is not available.");
	var httpResponse = requestEngine.sendRequest(req);
	return (httpResponse && new Response(httpResponse));
}

function sendGet(url) {
	sendRequest(new HttpGet(url));
}
 
