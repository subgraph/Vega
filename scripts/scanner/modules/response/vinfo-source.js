var module = {
  name: "Source Code Disclosure Module",
  type: "response-processor"
};


var xmlReject = /<?\s*xml/i;

function runTest(test, response) {
  var output = "";
  for (var i in test.regex) {
    var regex = test.regex[i];
    var res = regex.exec(response.bodyAsString);
    if (res) {
      if (!xmlReject.test(res[0])) {
        output += res[0];
      }
    }
  }
  if (output.length > 0) return output;
  else return null;
}

function run(request, response, ctx) {
  var testData = [{
    type: "ASP or JSP",
    regex: [/<!--\s*%[\s\S]+%\s*(--)?>/g, /<%(?:(?!%>)[\s\S])+%>/g]
  }, {
    type: "PHP",
    regex: [/<!--\s*\?[\s\S]+\?\s*(--)?>/g, /<\?(?:(?!\?>)[\s\S])+\?>/g]
  }, {
    type: "JSP Tag",
    regex: [/<jsp:.+\s+(?:(?!\/>)[\s\S])+\/>/g, /<jsp:.+>(?:(:!<\/jsp:)[\s\S])<\/jsp:[^>]*>/g]
  }];

  var output = "";

  for (var idx in testData) {
    var result = runTest(testData[idx], response);
    if (result) {
      ctx.addStringHighlight(result);
      output += ("Possible " + testData[idx].type + " code: \n" + result);
    }
  }

  if (output.length > 0) {
    var uri = String(request.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vinfo-source", request, response, {
      "output": output,
      "resource": uripart,
      key: "vinfo-source:" + uripart
    });
  }
}
