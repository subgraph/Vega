var module = {
  name: "Interesting Meta Tag Detection",
  type: "response-processor"
};

function run(request, response, ctx) {
	var res = false;
	var x = new Array();
	var output = "";
  var keywords = ["user", "password", "source", "author", "microsoft", "visual", "linux", "release", "version"];
  for (var i = 0; i <= keywords.length - 1; i += 1) {
    var current = new RegExp("(<meta(?:(?!>)[\\s\\S])*" + keywords[i] + "[\\s\\S]*?>)", "ig");
    x = current.exec(response.bodyAsString);
    if (x) {
      ctx.addStringHighlight(x[1]);
      output += x[1];
      output += "\n";
      res = true;
    }
  }
  if (res) {
   var uri = String(request.requestLine.uri);
   var uripart = uri.replace(/\?.*/, "");
    ctx.alert("vinfo-metatags", request, response, {
      "output": output,
      "resource": uripart,
      key: "vinfo-metatags" + uripart + output
    });
  }
}
