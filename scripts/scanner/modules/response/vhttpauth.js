var module = {
  name: "HTTP Authentication Over Unencrypted HTTP",
  type: "response-processor"
};

function run(request, response, ctx) {
  
  if (response.code == 401 && response.host.schemeName != "https") {
      ctx.alert("vhttpauth", request, response, {
      output: response.bodyAsString,
      key: "vhttpauth:" + request.requestLine.uri,
      resource: request.requestLine.uri
    });

  }

}
