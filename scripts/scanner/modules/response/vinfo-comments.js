var module = {
  name: "HTML Interesting Comments Detection",
  type: "response-processor",
  disabled: true
};

function run(request, response, ctx) {
	var res = false;
	var x = new Array();
	var output = "";

	var comments = ["user", "pass", "bug", "fix", "hack", "caution", "account", "bypass", "login", "todo", "warning", "note", "admin", "backdoor", "config"];
  for (var i = 0; i <= comments.length - 1; i += 1) {
    var current = new RegExp("(<!--(?:(?!-->)[\\s\\S])*" + comments[i] + "[\\s\\S]*?-->)", "ig");
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

    ctx.alert("vinfo-comments", request, response, {
      "output": output,
      "resource": uripart,
      key: "vinfo-comments" + uripart + output
    });
  }
}
