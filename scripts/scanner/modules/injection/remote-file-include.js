var module = {
  name: "Remote File Include Checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  var injectables = createInjectables(ctx);
  ctx.submitMultipleAlteredRequests(handler, injectables);
}

function createInjectables(ctx) {
  var ps = ctx.getPathState();
  var injectables = ["http://example.iana.org", 
                     "htTp://example.iana.org", 
                     "hthttpttp://example.iana.org",
                     "hthttp://tp://example.iana.org",
                     "example.iana.org"];
  
  var ret = [];
 
  for (var i = 0; i < injectables.length; i++)
    ret.push(injectables[i]);
  
  return ret;
}


function handler(req, res, ctx) {
	
  var content = "This domain is established to be used for illustrative examples in documents.";
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();	
  
  
  if (res.bodyAsString.indexOf(content) >= 0) {
	  var uri = String(req.requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  var ps = ctx.getPathState();
	  if (uri.host != "example.iana.org")
		ctx.addStringHighlight(content);
	  	ctx.alert("vinfo-rfi", req, res, {
	  			output: res.bodyAsString,
	  			key: "vinfo-rfi:" + uripart + ps.getFuzzableParameter().name,
	  			resource: uripart
	  	});
  	}
}
