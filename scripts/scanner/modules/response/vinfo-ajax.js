var module = {
  name: "AJAX Detector",
  type: "response-processor"
};

function run(request, response, ctx) {
  if (response.document) {

    var resp = response.bodyAsString;
    var regexp = /<script\b[^>]*?>([\s\S]*?)<\/script>/gim;
    var output = null;
    while (res = regexp.exec(resp)) {
      matchr = /(eval\(|microsoft\.xmlhttp|activexobject|msxml2\.xmlhttp|xmlhttprequest)/gim;
      if (res[1].match(matchr)) {
        ctx.addStringHighlight(res[1]);
        output = res[1];
      }
    }

  }
  if (output) {

    var uri = String(request.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");

    if (output.length > 200) {
      output = output.substr(0, 199) + "...";
    }

    ctx.alert("vinfo-ajax", request, response, {
      output: output,
      resource: uripart,
      key: "vinfo-ajax:" + uripart
    });
  }

}
