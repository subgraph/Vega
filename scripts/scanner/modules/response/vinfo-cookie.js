var module = {
  name : "Cookie Security Module",
  type: "response-processor"
};

function run(request, response, ctx) {

  var sessionSubStrings = ["ASP.NET_SessionId",
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
    "zenid"];
  
  var uri = String(request.requestLine.uri);
  var uripart = uri.replace(/\?.*/, "");
  var ssl = false;
  var cookies = [];
  cookies = response.cookies;
  var httpOnlyRegex = /;(\s)*HttpOnly;*/i;

  if (response.host.schemeName === "https") {
    ssl = true;
  }

  // Parse cookies array and generate alerts
  for (var i = 0; i < cookies.length; i++) {
  
  	HttpOnly = false;
  	if (httpOnlyRegex.test(cookies[i].getHeader())) {
  	  HttpOnly = true;
  	}    
    // vinfo-sessioncookie-secure and vinfo-cookie-secure alerts
    if(!cookies[i].isSecure() && ssl) {
      ctx.addStringHighlight(cookies[i].getHeader());
      var s; // session identifier substring
      var alerted = false; // alerted
      for (s in sessionSubStrings) {
        if (cookies[i].raw.indexOf(sessionSubStrings[s]) >= 0) {
          ctx.debug(sessionSubStrings[s] + " matched " + cookies[i].raw);
          if (alerted) {
            ctx.addStringHighlight(cookies[i].getHeader());
            ctx.alert("vinfo-sessioncookie-secure", request, response, {
              output: cookies[i].getHeader(),
              key: "vinfo-cookie-secure:" + uri.host + uripart + cookies[i].getHeader(),
              resource: request.requestLine.uri
            }); 
            alerted = true;
          }
        }   
      }

      if (!alerted) {
        ctx.alert("vinfo-cookie-secure", request, response, {
          output: cookies[i].getHeader(),
          key: "vinfo-cookie-secure:" + uri.host + uripart + cookies[i].getHeader(),
          resource: request.requestLine.uri
        });
      }
    }

    // vinfo-cookie-httponly alert
    
    if(!HttpOnly) {
      ctx.addStringHighlight(cookies[i].getHeader());
      ctx.alert("vinfo-cookie-httponly", request, response, {
        output: cookies[i].getHeader(),
        key: "vinfo-cookie-httponly:" + uri.host + uripart + cookies[i].getHeader(),
        resource: request.requestLine.uri
      });
    }

	
    // vinfo-securecookie-insecurechannel alert
    if(cookies[i].isSecure() && !ssl) {
      ctx.alert("vinfo-securecookie-insecurechannel", request, response, {
        output: cookies[i].getHeader(),
        key: "vinfo-securecookie-insecurechannel:" + uri.host + uripart + cookies[i].getHeader(),
        resource: request.requestLine.uri
      });        
    }
  }
}  