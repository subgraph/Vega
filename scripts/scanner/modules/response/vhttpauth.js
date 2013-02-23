var module = {
  name: "HTTP Authentication Over Unencrypted HTTP",
  type: "response-processor"
};

function run(request, response, ctx) {

  
  if (response.code == 401 && response.host.schemeName != "https") {
      var uri = String(request.requestLine.uri);
      var uripart = uri.replace(/\?.*/, "");

      ctx.alert("vhttpauth", request, response, {
      output: response.bodyAsString,
      key: "vhttpauth:" + uripart,
      resource: uripart
    });

  }

}
