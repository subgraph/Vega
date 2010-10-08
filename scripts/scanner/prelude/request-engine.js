importPackage(org.apache.http.client.methods)

var requestEngine = null;

function sendGet(url) {
  if(!requestEngine)
    throw new Error("Cannot send GET request because request engine is not available.");
  var httpResponse = requestEngine.sendRequest(new HttpGet(url));
  return (httpResponse && new Response(httpResponse));
}
