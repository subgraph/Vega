var module = {
  name: "Insecure Cross-Domain Policy",
  type: "response-processor"
};

function generateAlert(ctx, request, response, type, highlight) {
  var uristr = String(request.requestLine.uri);
  var uripart = uristr.replace(/\?.*/, "");
  var alertType = type;
  
  ctx.addStringHighlight(highlight);
  ctx.alert("vinfo-crossdomain-" + alertType, request, response, {
  	output: response.bodyAsString,
  	resource: uripart,
  	key: "vinfo-crossdomain-" + alertType + uripart
  });
}

function run(request, response, ctx) {
  var uristr = String(request.requestLine.uri);
  var uripart = uristr.replace(/\?.*/, "");
  var wildcardTldRegex = /^(\*.[\w]{2,6})$/;
  
  if (response.document && uripart.indexOf("crossdomain.xml") !== -1) {
    var siteControlElems = jQuery("site-control", response.document);
    var allowAccessFromElems = jQuery("allow-access-from", response.document);
    var allowHttpRequestHeadersElems = jQuery("allow-http-request-headers-from", response.document);
    
    for (var h = 0; h < siteControlElems.length; h++) {
      if (siteControlElems[h].getAttribute("permitted-cross-domain-policies") === "all") {
        var hlRegex0 = /<site-control[^<>]+permitted-cross-domain-policies\s*=\s*['"]?all['"]?[^<\/>]*\/>/im;
        var match0 = hlRegex0.exec(response.bodyAsString);
      	generateAlert(ctx, request, response, "sc-policies-all", match0[0]);
      }
    }
    for (var i = 0; i < allowAccessFromElems.length; i++) {  
      if (allowAccessFromElems[i].getAttribute("domain") === "*") {
      	var hlRegex1 = /<allow-access-from[^<>]+domain\s*=\s*['"]?\\*['"]?[^<\/>]*\/>/im;
      	var match1 = hlRegex1.exec(response.bodyAsString);
        generateAlert(ctx, request, response, "aafd-domain-wildcard", match1[0]);
      }
      
      if (wildcardTldRegex.test(allowAccessFromElems[i].getAttribute("domain"))) {
      	var hlRegex2 = /<allow-access-from[^<>]+domain\s*=\s*['"]?\*.[\w]{2,6}['"]?[^<\/>]*\/>/im;
      	var match2 = hlRegex2.exec(response.bodyAsString);
        generateAlert(ctx, request, response, "aafd-domain-wildcardtld", match2[0]);
      }
      
      if (allowAccessFromElems[i].getAttribute("secure") === "false") {
      	var hlRegex3 = /<allow-access-from[^<>]+secure\s*=\s*['"]?false['"]?[^<\/>]*\/>/im;
      	var match3 = hlRegex3.exec(response.bodyAsString);
        generateAlert(ctx, request, response, "aafd-secure-false", match3[0]);
      }      
    }
    
    for (var j = 0; j < allowHttpRequestHeadersElems.length; j++) {
      if (allowHttpRequestHeadersElems[j].getAttribute("domain") === "*") {
      	var hlRegex4 = /<allow-http-request-headers-from[^<>]+domain\s*=\s*['"]?\\*['"]?[^<\/>]*\/>/im;
      	var match4 = hlRegex4.exec(response.bodyAsString);     	
        generateAlert(ctx, request, response, "ahrf-domain-wildcard", match4[0]);
      }
      
      if (wildcardTldRegex.test(allowHttpRequestHeadersElems[j].getAttribute("domain"))) {      	
      	var hlRegex5 = /<allow-http-request-headers-from[^<>]+domain\s*=\s*['"]?\*.[\w]{2,6}['"]?[^<\/>]*\/>/im;
        var match5 = hlRegex5.exec(response.bodyAsString);
      	generateAlert(ctx, request, response, "ahrf-domain-wildcardtld", match5[0]);
      }
      
      if (allowHttpRequestHeadersElems[j].getAttribute("headers") === "*") {
      	var hlRegex6 = /<allow-http-request-headers-from[^<>]+headers\s*=\s*['"]?\\*['"]?[^<\/>]*\/>/im;
      	var match6 = hlRegex6.exec(response.bodyAsString);
        generateAlert(ctx, request, response, "ahrf-headers-wildcard", match6[0]);
      }
      
      if (allowHttpRequestHeadersElems[j].getAttribute("secure") === "false") {
      	var hlRegex7 = /<allow-http-request-headers-from[^<>]+secure\s*=\s*['"]?false['"]?[^<\/>]*\/>/im;
      	var match7 = hlRegex7.exec(response.bodyAsString);
        generateAlert(ctx, request, response, "ahrf-secure-false", match7[0]);
      }
    }
  }
}

