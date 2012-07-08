var module = {
  name: "RSS/Atom/OPL Feed Detector",
  type: "response-processor"
};

function run(request, response, ctx) {
  var output = "";
  var highlights = [];
  var res = 0;
  var rss = new RegExp("<link.*rel=.*rss.*?>", "ig");
  var atom = new RegExp("<link.*rel=.*xml.*?>", "ig");
  var opml = new RegExp("<opml.*version.*?>", "ig");

  var x = rss.exec(response.bodyAsString);
  if (x) {
    ctx.addStringHighlight(x[0]);
    output += x[0];
    output += "\n";
    res = 1;
  }
  var y = atom.exec(response.bodyAsString);
  if (y) {
    ctx.addStringHighlight(y[0]);
    output += y[0];
    output += "\n";
    res = 1;
  }
  var z = opml.exec(response.bodyAsString);
  if (z) {
    ctx.addStringHighlight(z[0]);
    output += z[0];
    output += "\n";
    res = 1;
  }

  if (res) {
    ctx.alert("vinfo-feeds", request, response, {
      "output": output,
      "resource": request.requestLine.uri,
      key: "vinfo-feeds" + output
    });
  }
}
