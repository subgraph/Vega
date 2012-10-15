var module = {
  name: "HTML Interesting Comments Detection",
  type: "response-processor",
  disabled: true
};

var res;
var x = new Array();
var output = "";

function run(request, response, ctx) {
  var comments = ["user", "pass", "bug", "fix", "hack", "caution", "account", "bypass", "login", "todo", "warning", "note", "admin", "backdoor", "config"];
  for (i = 0; i <= comments.length - 1; i += 1) {
    var current = new RegExp("(<!--(?:(?!-->)[\\s\\S])*" + comments[i] + "[\\s\\S]*?-->)", "ig");
    x = current.exec(response.bodyAsString);
    if (x) {
      ctx.addStringHighlight(x[1]);
      output += x[1];
      output += "\n";
      res = 1;
    }
  }
  if (res) {
    ctx.alert("vinfo-comments", request, response, {
      "output": output,
      "resource": request.requestLine.uri,
      key: "vinfo-comments" + request.requestLine.uri + output
    });
  }
}
