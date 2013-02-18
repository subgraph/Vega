var module = {
  name: "Blind SQL Injection Timing Analysis Checks",
  category: "Injection Modules",
  defaultDisabled: true
};

function initialize(ctx) {
  
  var ps = ctx.getPathState();
  var rand1 = Math.floor(Math.random()*8888) + 200;
  var rand2 = Math.floor(Math.random()*200);
  
  if (ps.isParametric()) {
	  
	// MySQL 
	  
    ctx.submitAlteredRequest(process, "1 AND SLEEP(20) -- ", false, 0);
    ctx.submitAlteredRequest(process, "1 OR (SELECT SLEEP(20)) -- ", false, 1);
    ctx.submitAlteredRequest(process, "\" AND SLEEP(20) -- ", true, 2);
    ctx.submitAlteredRequest(process, "' AND SLEEP(20) -- ", true, 3);
    ctx.submitAlteredRequest(process, "\" OR (SELECT SLEEP(20)) -- ", true, 4);
    ctx.submitAlteredRequest(process, "\' OR (SELECT SLEEP(20)) -- ", true, 5);
    ctx.submitAlteredRequest(process, " OR EXISTS(SELECT SLEEP(20)) -- ", true, 6);
    ctx.submitAlteredRequest(process, "' OR EXISTS(SELECT SLEEP(20)) -- ", true, 7);
    ctx.submitAlteredRequest(process, "\" OR EXISTS(SELECT SLEEP(20)) -- ", true, 8);
    ctx.submitAlteredRequest(process, " UNION SELECT SLEEP(20) -- ", true, 9);
    ctx.submitAlteredRequest(process, "' UNION SELECT SLEEP(20) -- ", true, 10);
    ctx.submitAlteredRequest(process, "\" UNION SELECT SLEEP(20) -- ", true, 11);
    ctx.submitAlteredRequest(process, "(1 AND (SELECT SLEEP(20)))", false, 12);
  }  

}

function process(req, res, ctx) {
	if (res.milliseconds > 20000) {
		var ps = ctx.getPathState();
		
		ctx.debug("Request:"+req.requestLine.uri);
		ctx.debug("Milliseconds:"+res.milliseconds);
	
		var uri = String(req.requestLine.uri);
		var uripart = uri.replace(/\?.*/, "");

	    ctx.alert("vinfo-sql-inject", req, res, {
			      output: res.bodyAsString,
			      key: "vinfo-sql-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
			      resource: uripart,
			      detectiontype: "Blind Timing Analysis Checks"
			    });	
	}
}
