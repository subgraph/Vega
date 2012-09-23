var module = {
  name : "Cookie Security Module",
  type: "response-processor",
};



function run(request, response, ctx) {

  var sessionSubStrings = new Array("ASP.NET_SessionId",
		  							"ASPSESSIONID",
		  							"sessionid",
		  							"_session",
		  							"JSESSIONID",
		  							"PHPSESSID",
		  							"symfony",
		  							"PD-H-SESSION-ID",
		  							"PD-S-SESSION-ID",
		  							"SITESERVER",
		  							"cfid",
		  							"cftoken",
		  							"jsessionid",
		  							"sessid",
		  							"sid",
		  							"viewstate",
		  							"zenid");
  
  var cookies = new Array();
  cookies=response.getHeaders("Set-Cookie");
  // FIXME: Test for SSL Missing!!!
  // so assume it is ssl until we can fix this
  var ssl=0;
  if(response.host.schemeName=="https"){
    ssl=1;
  }
  for(var i=0; i<cookies.length; i++) {
    var httponly=0;
    var secure=0;
    var params = new Array();
    params = cookies[i].getValue().split(";");
    for(var j=1; j<params.length; j++) {
      if(params[j].toLowerCase()==" secure"){
        secure=1;
      }
      if(params[j].toLowerCase()==" httponly"){
        httponly=1;
      }
    }	

    if(httponly!=1 || (secure!=1&&ssl==1)) { ctx.addStringHighlight(cookies[i].getValue()); }

    if(secure !=1 && ssl == 1) {
      var s; // session identifier substring
      var a = 0; // alerted
      for (s in sessionSubStrings) {
    	if (cookies[i].getValue().indexOf(sessionSubStrings[s]) >= 0) {
    	  ctx.debug(sessionSubStrings[s] + " matched " + cookies[i].getValue());
    	  if (a == 0) {
      	    ctx.alert("vinfo-sessioncookie-secure", request, response, {
    	              output: cookies[i].getValue(),
    	              key: "vinfo-cookie-secure:" + cookies[i].getValue(),
    	              resource: request.requestLine.uri
    	            }); 
    	    a = 1;
    	    }
    	  }
    	}
        if (a == 0) {
        	
    	  ctx.alert("vinfo-cookie-secure", request, response, {
          output: cookies[i].getValue(),
          key: "vinfo-cookie-secure:" + cookies[i].getValue(),
          resource: request.requestLine.uri
        });
      }
    }
    if(httponly!=1){
      java.lang.System.out.println("http-only");
      ctx.alert("vinfo-cookie-httponly", request, response, {
        output: cookies[i].getValue(),
        key: "vinfo-cookie-httponly:" + cookies[i].getValue(),
        resource: request.requestLine.uri
      });
    }
  }
}
