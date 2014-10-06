var module = {
  name: "Internal IP Addressess",
  type: "response-processor"
};

function validateAddress(ip) {
  var ps = ip.split(".");
  if (ps.length != 4) return false;
  for (var i = 0; i < 4; i++) {
    var n = parseInt(ps[i]);
    if (isNaN(n) || n < 0 || n > 255) return false;
  }
  return true;
}

function run(request, response, ctx) {
  var regex = /1(?:92|0|72)(?:\.[12]?\d{1,2}){3}/g,
  body = response.bodyAsString,
  result = [];

  while (r = regex.exec(body)) {
    if (String(response.host.hostName) !== r[0]) {
      if (validateAddress(r[0]) && result.indexOf(r[0]) == -1) {
        var regex2 = /(^127\.0\.0\.1)|(^10\.)|(^172\.1[6-9]\.)|(^172\.2[0-9]\.)|(^172\.3[0-1]\.)|(^192\.168\.)/;
        if (regex2.exec(r[0])) {
          ctx.addStringHighlight(r[0]);
          result.push(r[0]);
        }
      }

      if (result.length) {
        var url = String(request.requestLine.uri);
        var uripart = url.replace(/\?.*/, "");

        ctx.alert("vinfo-1918", request, response, {
          output: result.join(" "),
          resource: uripart,
          key: "vinfo-1918" + uripart + result.join(" ")
        });
      }
    }
  }
}