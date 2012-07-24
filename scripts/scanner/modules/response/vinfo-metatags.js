var module = {
  name: "Interesting Meta Tag Detection",
  type: "response-processor"
};

var res;
var x = new Array();
var output = "";

function run(request, response, ctx) {
  var keywords = ["user", "password", "source", "author", "microsoft", "visual", "linux", "release", "version"];
  for (i = 0; i <= keywords.length - 1; i += 1) {
    var current = new RegExp("(<meta(?:(?!>)[\\s\\S])*" + keywords[i] + "[\\s\\S]*?>)", "ig");
    x = current.exec(response.bodyAsString);
    if (x) {
      ctx.addStringHighlight(x[1]);
      output += x[1];
      output += "\n";
      res = 1;
    }
  }
  if (res) {
    ctx.alert("vinfo-metatags", request, response, {
      "output": output,
      "resource": request.requestLine.uri,
      key: "vinfo-metatags" + request.requestLine.uri + output
    });
  }
}
