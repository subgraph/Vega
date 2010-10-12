var module = {
  name : "VCS Module",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var host = response.header("Host");
  var regexp = /\$.{1,12}: .*? .*? \d{4}[-\/]\d{1,2}[-\/]\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}.* (.*?) (Exp )?\$/;
  var res;

  if(!banner)
	  return;

  res = regexp.test(response.bodyAsString);
  if (res) {
    model.alert("vvcs-users", {"output": response.bodyAsString, "resource": httpRequest.requestLine.uri} );
  }
}
