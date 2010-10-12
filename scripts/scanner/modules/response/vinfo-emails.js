var module = {
  name : "E-Mail Finder Module",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var host = response.header("Host");
  var regexp = /[^\s]+@[^\s]+/gi;
  var res;
  var emails = [];

  if(!banner)
	  return;

  while (res = regexp.exec(response.bodyAsString)) {
    var match;
    var regexp2 = /([-a-z0-9~!$%^&*_=+}{\'?]+(\.[-a-z0-9~!$%^&*_=+}{\'?]+)*@([a-z0-9_][-a-z0-9_]*(\.[-a-z0-9_]+)*\.(aero|arpa|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|mobi|[a-z][a-z])|([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}))(:[0-9]{1,5})?)/;
    match = regexp2.exec(res[0]); 
    if (emails.indexOf(match[0]) < 0)
    {
      emails.push(match[0]);
    }
  }
  if (emails.length) {
    model.alert("vinfo-emails", {"output": emails.join(" "), "resource": httpRequest.requestLine.uri} );
  }
}
