var module = {
  name: "Remote File Include Checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  var ps = ctx.getPathState();

  if (ps.isParametric()) {

    var injectables = createInjectables(ctx);
    ctx.submitMultipleAlteredRequests(handler, injectables);
  }
}

function createInjectables(ctx) {
  var ps = ctx.getPathState();
  var injectables = ["http://www.google.com/humans.txt", 
                     "htTp://www.google.com/humans.txt", 
                     "hthttpttp://www.google.com/humans.txt",
                     "hthttp://tp://www.google.com/humans.txt",
                     "www.google.com/humans.txt"];
  
  var ret = [];

    for (var i = 0; i < injectables.length; i++)
      ret.push(injectables[i]);
   
  return ret;
}


function handler(req, res, ctx) {
	
  var content = "Google is built by a large team of engineers, designers, researchers, robots";
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();	
  
  
  if (res.bodyAsString.indexOf(content) >= 0) {
	  var uri = String(req.requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  var ps = ctx.getPathState();
	  if (uri.host != "www.google.com")
		ctx.addStringHighlight(content);
	  	ctx.alert("vinfo-rfi", req, res, {
	  			output: res.bodyAsString,
	  			key: "vinfo-rfi:" + uripart + ps.getFuzzableParameter().name,
	  			resource: uripart
	  	});
  	}
}
