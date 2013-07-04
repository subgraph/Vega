var module = {	
  name: "Directory Listing and Traversal Checks",
  category: "Injection Modules",
  differential: true
};


function initialize(ctx) {
  var injectables = createInjectables(ctx);
  ctx.submitMultipleAlteredRequests(handler, injectables);
}

function createInjectables(ctx) {
  var ps = ctx.getPathState();

  if (!ps.isParametric()) {
    return ["/./", "/.vega/", 
            "\\.\\", "\\.vega\\"];
  }
  var fuzzable = ps.getFuzzableParameter();

  var injectables = [".../", "./", 
                     "...\\", ".\\", 
                     "/../", "/.../", 
                     "\\..\\", "\\...\\",
                     "/./", "/././", "/.../",
                     "/../../../../../../../../../../../etc/passwd\0",
                     "../../../../../../../../../../../etc/passwd\0"];
  

  var ret = [];
 
  for (var i = 0; i < injectables.length; i++)
    ret.push(injectables[i] + fuzzable.value);
  
  // UNIX 
  
  ret.push("/../../../../../../../../../../../etc/passwd");
  ret.push("../../../../../../../../../../../../etc/passwd");
  ret.push("file:/../../../../../../../../../../../../etc/passwd");
  
  // JAVA
  
  ret.push("WEB-INF/web.xml");
  ret.push("/WEB-INF/web.xml");
  ret.push("../../WEB-INF/web.xml");
  ret.push("/../../WEB-INF/web.xml");

  // WINDOWS
  
  ret.push("\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\boot.ini");
  ret.push("..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\..\\boot.ini");
  ret.push("C:\\boot.ini");
  ret.push("file:/../../../../../../../../../../../boot.ini");
  
  return ret;
}


function handler(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;

  if (res.fetchFail) {
    ctx.setModuleFailed();
    return;
  }
  
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();	
  
  ctx.addRequestResponse(req, res);
  
  if (!ps.isParametric()) {
	  if (ctx.incrementResponseCount() < 4) return;
  }
  else {
	  if (ctx.incrementResponseCount() < 24) return;
  }
  

  if (!ps.isParametric()) {
    if (ctx.getSavedResponse(0).code < 300 && !ctx.isFingerprintMatch(0, fp) && !ctx.isFingerprintMatch(0, 1)) {
      publishAlert(ctx, "Unique response for /./", 0, ctx.getSavedRequest(0), ctx.getSavedResponse(0));
      ctx.responseChecks(ps.createRequest(), ctx.getSavedResponse(0));
    }
    if (ctx.getSavedResponse(2).code < 300 && !ctx.isFingerprintMatch(2, fp) && !ctx.isFingerprintMatch(2, 3)) {
      publishAlert(ctx, "Unique response for \\.\\", 2, ctx.getSavedRequest(2), ctx.getSavedResponse(2));
      ctx.responseChecks(2);
    }
    return;
  }
  else {
    if (!ctx.isFingerprintMatch(0, 1)) {
      publishAlert(ctx, "Responses for ./val and .../val look different", 1);
      ctx.responseChecks(0);
    }
    if (!ctx.isFingerprintMatch(2, 3)) {
      publishAlert(ctx, "Responses for .\\val and ...\\val look different", 3);
      ctx.responseChecks(2);
    }
    if (!ctx.isFingerprintMatch(4, 5)) {
        publishAlert(ctx, "Responses for /../val and /.../val look different", 5);
        ctx.responseChecks(4);
      }
    if (!ctx.isFingerprintMatch(6, 7)) {
        publishAlert(ctx, "Responses for \..\val and \...\val look different", 7);
        ctx.responseChecks(6);
      }
    if (ctx.isFingerprintMatch(8, 9) && (!ctx.isFingerprintMatch(8,10))) {
    	publishAlert(ctx, "Responses for /./, /././ and /.../ look different", 10);
    	ctx.responseChecks(8);
    }
  }  
    // Passwd file content matches
    
    var pwre = /root:.:0:0/;
    var i;
    var res = null;
     
    for (i=11; i<=15; i++) {
    	res = pwre.exec(ctx.getSavedResponse(i).bodyAsString);
    	if (res) {
		ctx.addRegexCaseInsensitiveHighlight("root:.:0:0");
    		publishAlert(ctx, res[0], i);
    	}
    }

    // web.xml content matches

    var content = "<web-app";
    for (i = 16; i<= 19; i++) {
    	if (ctx.getSavedResponse(i).bodyAsString.indexOf(content) >= 0) {
		ctx.addStringHighlight(content);
    		publishAlert(ctx, content, i);
    	}
    }
    
    content = "[boot loader]";
    for (i = 20; i <= 23; i++) {
    	if (ctx.getSavedResponse(i).bodyAsString.indexOf(content) >= 0) {
                ctx.addStringHighlight(content);
    		publishAlert(ctx, content, i);
    	}
    }
}

  
  
function publishAlert(ctx, msg, idx) {

	var uri = String(ctx.getSavedRequest(idx).requestLine.uri);
	var uripart = uri.replace(/\?.*/, "");
	var ps = ctx.getPathState();
	
	ctx.alert("vinfo-directory-traversal", ctx.getSavedRequest(idx), ctx.getSavedResponse(idx), {
		output: ctx.getSavedResponse(idx).bodyAsString,
		key: "vinfo-directory-traversal:" + uripart + ps.getFuzzableParameter().name,
		resource: uripart
	});
}

	
	




