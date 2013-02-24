var module = {
  name: "Directory Listing Detection",
  type: "response-processor"
};

function run(request, response, ctx) {
  var patterns = [/\bIndex of\b/gm, /\bName\b/gm, /\bSize\b/gm, /\bParent Directory\b/gm, /\bFolder Listing\b/gm, /\bDirectory Listing\b/gm];
  var res = 0;
  var url = String(request.requestLine.uri);
  var uripart = url.replace(/\?.*/, "");


  for (i = 0; i <= patterns.length - 1; i += 1) {
    // First match, good enough.
    if (patterns[i].exec(response.bodyAsString)) {
      res += 1;
    }
  }

  if (url.search("C=") <= 0 && res >= 2) {
    ctx.alert("vdirlist", request, response, {
      output: response.bodyAsString,
      key: "vdirlist:" + uripart,
      resource: uripart
    });
  }
}
