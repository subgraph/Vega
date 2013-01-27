var module = {
  name: "Blind OS Command Injection",
  category: "Injection Modules",
  differential: false
};

function initialize(ctx) {
  
  var ps = ctx.getPathState();

  if (ps.isParametric()) {
	  	  
    ctx.submitAlteredRequest(process, "; /bin/sleep 5 ; ", false, 0);
    ctx.submitAlteredRequest(process, "; /bin/sleeep 5 ; ", false, 1);
    
    ctx.submitAlteredRequest(process, "\" ; /bin/sleep 5 ; ", false, 2);
    ctx.submitAlteredRequest(process, "\" ; /bin/sleeep 5 ; ", false, 3);

    ctx.submitAlteredRequest(process, "' ; /bin/sleep 5 ; ", false, 4);
    ctx.submitAlteredRequest(process, "' ; /bin/sleeep 5 ; ", false, 5);
    
    ctx.submitAlteredRequest(process, "| /bin/sleep 5 ; ", false, 6);
    ctx.submitAlteredRequest(process, "| /bin/sleeep 5 ;  ", false, 7);

    ctx.submitAlteredRequest(process, "\"| /bin/sleep 5 ; ", false, 8);
    ctx.submitAlteredRequest(process, "\"| /bin/sleeep 5 ;  ", false, 9);
    
    ctx.submitAlteredRequest(process, "'| /bin/sleep 5 ; ", false, 10);
    ctx.submitAlteredRequest(process, "'| /bin/sleeep 5 ;  ", false, 11);
    
  }  

}

function process(req, res, ctx) {
	
	  if (ctx.hasModuleFailed()) return;
	  var ps = ctx.getPathState();

	  if (res.fetchFail) {
	    ctx.error(req, res, "During command injection checks");
	    ctx.setModuleFailed();
	    return;
	  }

	  ctx.addRequestResponse(req, res);
	  if (ctx.incrementResponseCount() < 12) return;
	  
	  if ((ctx.getSavedResponse(0).milliseconds > 5000) && (ctx.getSavedResponse(1).milliseconds < 5000))
      {
			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(0), ctx.getSavedResponse(0), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }
	  
	  if ((ctx.getSavedResponse(2).milliseconds > 5000) && (ctx.getSavedResponse(3).milliseconds < 5000))
      {			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(2), ctx.getSavedResponse(2), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }

	  if ((ctx.getSavedResponse(4).milliseconds > 5000) && (ctx.getSavedResponse(5).milliseconds < 5000))
      {			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(4), ctx.getSavedResponse(4), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }
	  
	  if ((ctx.getSavedResponse(6).milliseconds > 5000) && (ctx.getSavedResponse(7).milliseconds < 5000))
      {			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(6), ctx.getSavedResponse(6), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }
	  

	  if ((ctx.getSavedResponse(8).milliseconds > 5000) && (ctx.getSavedResponse(9).milliseconds < 5000))
      {			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(8), ctx.getSavedResponse(8), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }

	  if ((ctx.getSavedResponse(10).milliseconds > 5000) && (ctx.getSavedResponse(11).milliseconds < 5000))
      {			
		  var uri = String(req.requestLine.uri);
		  var uripart = uri.replace(/\?.*/, "");

		  ctx.alert("vinfo-command-inject", ctx.getSavedRequest(10), ctx.getSavedResponse(10), {
				     output: res.bodyAsString,
				     key: "vinfo-command-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
				     resource: uripart,
				     detectiontype: "Blind Timing Analysis Checks",
				     param: ps.getFuzzableParameter().name
				  });	
      }
	
}
