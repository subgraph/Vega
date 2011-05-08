var module = {
  name : "Version Control String Detection",
  type: "response-processor"
};

function run() {
  var regexp = /\$.{1,12}: .*? .*? \d{4}[-\/]\d{1,2}[-\/]\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}.* (.*?) (Exp )?\$/;
  var res;

  res = regexp.test(response.bodyAsString);
  if (res) {
    model.alert("vvcs-users", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri, response: response} );
  }
}
