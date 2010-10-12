var module = {
  name : "Source Code Disclosure Module",
  type: "response-processor"
};

function run() {
  var banner = response.header("Server");
  var host = response.header("Host");

  var regexpasp = new Array(new RegExp("(<!--[^]*%[^]*?%(--)?>)","ig"), new RegExp("<%[^]*?%>","ig"));
  var regexpphp = new Array(new RegExp("<\\?(?! *xml).*\\?>","ig"), new RegExp("<!--[^]*\\?[^]*?\\?(--)?>","ig"));
  var regexpjsp = new Array(new RegExp("<jsp:[^]*?>","gi"), new RegExp("<!--[^]*jsp:[^]?(--)?>","ig"));
  var i = 0;
  var output = "";
  var found = 0;
  var tmp = "";

  if(!banner)
	  return;


  for (i = 0; i < regexpasp.length; i++)
  {
    var res = regexpasp[i].exec(response.bodyAsString);
    if (res)
    {
      tmp += res.join("\n");
      found = 1;
    }
  }

  if (found)
  {
    output += "Possible ASP or JSP code: \n" + tmp;
    found = 0;
    tmp = "";
  }

  for (i = 0; i < regexpasp.length; i++)
  {
    var res = regexpphp[i].exec(response.bodyAsString);
    if (res)
    {
      tmp += res.join("\n");
      found = 1;
    }
  }

  if (found)
  {
    output += "Possible PHP code: \n" + tmp;
    found = 0;
    tmp = "";
  }

  for (i = 0; i < regexpjsp.length; i++)
  {
    var res = regexpjsp[i].exec(response.bodyAsString);
    if (res)
    {
      tmp += res.join("\n");
      found = 1;
    }
  }

  if (found)
  {
    output += "Possible JSP code: \n" + tmp;
  }

  found = 0;
  tmp = "";

  if (output != "") {
    model.alert("vinfo-source", {"output": output, "resource": httpRequest.requestLine.uri} );
  }
}
