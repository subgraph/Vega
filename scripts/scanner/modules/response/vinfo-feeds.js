var module = {
  name: "RSS/Atom/OPL Feed Detector",
  type: "response-processor"
};

function run(request, response, ctx) {
  var output = "";
  var res = 0;
  var rss = new RegExp("<link.*rel=.*rss.*?>", "ig");
  var atom = new RegExp("<link.*rel=.*atom.*?>", "ig");
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
     var uri = String(request.requestLine.uri);
     var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vinfo-feeds", request, response, {
      "output": output,
      "resource": uripart,
      key: "vinfo-feeds" + output
    });
  }
}
