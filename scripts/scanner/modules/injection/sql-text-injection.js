var module = {
  name: "Blind SQL Text Injection Differential Checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  
  var ps = ctx.getPathState();

  if (ps.isParametric()) {
    ctx.submitAlteredRequest(process, "\\'\\\"", true, 0);
    ctx.submitAlteredRequest(process, "'\"", true, 1);
    ctx.submitAlteredRequest(process, "\\\\'\\\\\"", true, 2);
    ctx.submitAlteredRequest(process, "''''\"\"\"\"", true, 3);
    ctx.submitAlteredRequest(process, "'\"'\"'\"'\"", true, 4);

    ctx.submitAlteredRequest(process, "' OR 1 = 1 -- ", true, 5);
    ctx.submitAlteredRequest(process, " OR 1=1 -- ", true, 6);
    ctx.submitAlteredRequest(process, "\" OR 1=1 -- ", true, 7);
    
    ctx.submitAlteredRequest(process, "' OR 1=2 -- ", true, 8);
    ctx.submitAlteredRequest(process, " OR 1=2 -- ", true, 9);
    ctx.submitAlteredRequest(process, "\" OR 1=2 -- ", true, 10);

    ctx.submitAlteredRequest(process, "'", true, 11);
    ctx.submitAlteredRequest(process, "\\'", true, 12);    
    ctx.submitAlteredRequest(process, "''", true, 13);
    
    ctx.submitAlteredRequest(process, "vega32432", true, 14);   
    
  }
  

}

function process(req, res, ctx) {

  if (ctx.hasModuleFailed()) return;


  if (res.fetchFail) {	
    ctx.error(req, res, "During SQL injection checks");
    ctx.setModuleFailed();
    return;
  }

  ctx.addRequestResponse(req, res);
  
  var n = ctx.incrementResponseCount();
  
  if (n < 15) return;
  
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();
  
  if (!ctx.isFingerprintMatch(5,8)){
	  ctx.debug("HIT:");
	  ctx.debug(ctx.getSavedRequest(5).requestLine.uri)
  }

  if (!ctx.isFingerprintMatch(0, 1) && !ctx.isFingerprintMatch(1, 2)) {
	  var uri = String(ctx.getSavedRequest(1).requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });

    ctx.responseChecks(0);
    ctx.responseChecks(2);
  }
  
  if (ctx.isFingerprintMatch(1, 4) && !ctx.isFingerprintMatch(3, 4)) {
	  var uri = String(ctx.getSavedRequest(1).requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(3);
    ctx.responseChecks(4);
  }
  
  if (ctx.isFingerprintMatch(12, 13) && !ctx.isFingerprintMatch(11, 12)) {
	  var uri = String(ctx.getSavedRequest(1).requestLine.uri);
	  var uripart = uri.replace(/\?.*/, "");
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(12);
    ctx.responseChecks(13);
  }  

}
