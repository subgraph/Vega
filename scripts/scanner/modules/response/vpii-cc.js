var module = {
  name: "Credit Card Identification",
  type: "response-processor",
  defaultDisabled: true
};

function luhncheck(str) {
  str = (str + '').replace(/\D+/g, '').split('').reverse();
  if (!str.length) return false;
  var t = 0,
      i;
  for (i = 0; i < str.length; i++) {
    str[i] = parseInt(str[i]);
    t += i % 2 ? 2 * str[i] - (str[i] > 4 ? 9 : 0) : str[i];
  }
  return (t % 10) == 0;
}

function run(request, response, ctx) {
  // Regex compatible with Visa (16&13 numbers), MC, Amex, Discover, JCB, Diner's Club
  var regexp = /\b(([453]\d{3}|6011)([- ]?)\d{4}\3\d{4}\3\d{4}|3\d{3}([- ]?)\d{6}\4\d{4,5}|4\d{12})\b/gm;
  var cards = [];
  var res;

  while (res = regexp.exec(response.bodyAsString)) {
    if (luhncheck(res[0])) {
      ctx.addStringHighlight(res[0]);
      cards.push(res[0]);
    }
  }
  if (cards.length) {
	
   var uri = String(request.requestLine.uri);
   var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vpii-cc", request, response, {
      "output": cards.join("\n"),
      "resource": uripart,
      key: "vpii-cc:" + uripart + cards.join("\n")
    });
  }
}
