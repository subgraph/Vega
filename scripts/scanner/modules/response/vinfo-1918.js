var module = {
  name : "RFC 1918 IP Address Finder",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var host = response.header("Host");

  var regexp = /(\d+\.\d+\.\d+\.\d+)/;

  var regexp1 = /((10)\.([0-9]|[1-9][0-9]|[1-2][0-5][0-5])\.([0-9]|[1-9][0-9]|[1-2][0-5][0-5])\.([0-9]|[1-9][0-9]|[1-2][0-5][0-5]))/g;
  var regexp2 = /((172)\.(1[6-9]|2[0-9]|3[0-1])\.([0-9]|[1-9][0-9]|[1-2][0-5][0-5])\.([0-9]|[1-9][0-9]|[1-2][0-5][0-5]))/g;
  var regexp3 = /((192\.168)(\.)([0-9]|[1-9][0-9]|[1-2][0-5][0-5])(\.)([0-9]|[1-9][0-9]|[1-2][0-5][0-5]))/g;

  var res;
  var ips = [];
  if(!banner)
	  return;

  while(res = regexp1.exec(response.bodyAsString)) {
    if (ips.indexOf(res[0]) < 0)
    {
      ips.push(res[0]);
    }
  }

  while(res = regexp2.exec(response.bodyAsString)) {
    if (ips.indexOf(res[0]) < 0)
    {
      ips.push(res[0]);
    }
  }

  while(res = regexp3.exec(response.bodyAsString)) {
    if (ips.indexOf(res[0]) < 0)
    {
      ips.push(res[0]);
    }
  }

  if (ips.length) {
    model.alert("vinfo-1918", {"output": ips.join(" "), "resource": httpRequest.requestLine.uri} );
  }
}
