var module = {
  name: "Version Control String Detection",
  type: "response-processor"
};

function run(request, response, ctx) {
  var regexp = /\$.{1,12}: .*? .*? \d{4}[-\/]\d{1,2}[-\/]\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}.* (.*?) (Exp )?\$/;
  var res;

  res = regexp.test(response.bodyAsString);
  if (res) {
   var uri = String(request.requestLine.uri);
   var uripart = uri.replace(/\?.*/, "");

    ctx.alert("vvcs-users", request, response, {
      "output": response.bodyAsString,
      "resource": uripart,
      key: "vvcs-users:" + uripart
    });
  }
}
