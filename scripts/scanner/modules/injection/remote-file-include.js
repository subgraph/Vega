var module = {
  name: "Remote File Include Checks",
  category: "Injection Modules"
};

/* 
 * Temporary. 
 * This module won't work unless the server can reach the web. 
 * A better solution would be to nominate a random page on the same server 
 * This will also prevent requests being sent to 'example.org' 
 * Also this module won't detect anything if the hostname is example.org or www.example.org.
 * I feel dirty.
 * */
	
function initialize(ctx) {
  var injectables = createInjectables(ctx);
  ctx.submitMultipleAlteredRequests(handler, injectables);
}

function createInjectables(ctx) {
  var ps = ctx.getPathState();
  var injectables = ["http://www.example.org", 
                     "htTp://www.example.org", 
                     "hthttpttp://www.example.org",
                     "hthttp://tp://www.example.org",
                     "www.example.org"];
  
  var ret = [];
 
  for (var i = 0; i < injectables.length; i++)
    ret.push(injectables[i]);
  
  return ret;
}


function handler(req, res, ctx) {
	
  var content = "for documentation purposes. These domains may be used as illustrative";
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();	
  
  
  if (res.bodyAsString.indexOf(content) >= 0) {
	  var uri = String(req.requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  var ps = ctx.getPathState();
	  if (uri.host != "example.org" && uri.host != "www.example.org")
	  	ctx.alert("vinfo-rfi", req, res, {
	  			output: res.bodyAsString,
	  			key: "vinfo-rfi:" + uripart + ps.getFuzzableParameter().name,
	  			resource: uripart
	  	});
  	}
}
