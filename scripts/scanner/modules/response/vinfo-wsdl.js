var module = {
  name: "WSDL Detector",
  type: "response-processor"
};

function run(request, response, ctx) {

  wsdl = "<wsdl:";

  if (response.bodyAsString.indexOf(wsdl) >= 0) {

   var uri = String(request.requestLine.uri);
   var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vinfo-wsdl", request, response, {
      "output": response.bodyAsString,
      key: "vinfo-wsdl:" + uripart,
      "resource": uripart
    });
  }
}
