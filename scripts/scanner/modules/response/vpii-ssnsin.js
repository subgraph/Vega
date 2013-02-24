var module = {
  name: "Social Security/Social Insurance Number Detector",
  type: "response-processor",
  defaultDisabled: true
};

var resssn;
var resultssn;
var resultsin;
var outputssn = [];
var outputsin = [];
var ssn = /\b\d{3}([- ]?)(\d{3}\1\d{3}|\d{2}\1\d{4})\b/gm;

function run(request, response, ctx) {

  function isValidSSN(value) {
    var re = /^([0-6]\d{2}|7[0-6]\d|77[0-2])([ \-]?)(\d{2})\2(\d{4})$/;
    if (!re.test(value)) {
      return false;
    }
    var temp = value;
    if (value.indexOf("-") != -1) {
      temp = (value.split("-")).join("");
    }
    if (value.indexOf(" ") != -1) {
      temp = (value.split(" ")).join("");
    }
    if (temp.substring(0, 3) == "666") {
      return false;
    }
    if (temp.substring(0, 3) == "000") {
      return false;
    }
    if (temp.substring(3, 5) == "00") {
      return false;
    }
    if (temp.substring(5, 9) == "0000") {
      return false;
    }
    return true;
  }

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

  while (resssn = ssn.exec(response.bodyAsString)) {
    if (isValidSSN(resssn[0])) {
      ctx.addStringHighlight(resssn[0]);
      outputssn.push(resssn[0]);
      resultssn = 1;
    }
    if (luhncheck(resssn[0])) {
      ctx.addStringHighlight(resssn[0]);
      outputsin.push(resssn[0]);
      resultsin = 1;
    }
  }

  if (resultssn) {
    ctx.alert("vpii-ssnsin-ssn", request, response, {
      "output": outputssn.join("\n"),
      "resource": uripart,
      key: "vpii-ssnsin-ssn" + uripart,
    });
  }
  if (resultsin) {
    var uri = String(request.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vpii-ssnsin-sin", request, response, {
      "output": outputsin.join("\n"),
      "resource": uripart,
      key: "vpii-ssnsin-sin" + uripart
    });
  }
}
