var module = {
  name: "Internal IP Addresses",
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
    if (validateAddress(r[0]) && result.indexOf(r[0]) == -1) {
      ctx.addStringHighlight(r[0]);
      result.push(r[0]);
    }
  }

  if (result.length) {
    ctx.alert("vinfo-1918", request, response, {
      output: result.join(" "),
      resource: request.requestLine.uri,
      key: "vinfo-1918" + request.requestLine.uri + result.join(" ")
    });
  }
}
