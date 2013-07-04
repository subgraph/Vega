var module = {
  name: "Blind SQL Text Injection Differential Checks",
  category: "Injection Modules",
  differential: true
};

function initialize(ctx) {
  
  var ps = ctx.getPathState();

  if (ps.isParametric()) {

    var uri = String(ps.getPath().getUri());
    var uripart = uri.replace(/\?.*/, "");
    var param = ps.getFuzzableParameter().name;
    var pathkey;

    if (ps.getPath().isPostTarget() == true) {
      pathkey = "vinfo-sql-inject:" + uripart + "?" + "post" + "?" + param;
    }
    else
    {
      pathkey = "vinfo-sql-inject:" + uripart + "?" + "get" + "?" + param;
    }

    var k= pathkey;

    if (ctx.alertExists(k)) {
      return;
    }


    ctx.submitAlteredRequest(process, "\\'\\\"", true, 0);
    ctx.submitAlteredRequest(process, "'\"", true, 1);
    ctx.submitAlteredRequest(process, "\\\\'\\\\\"", true, 2);
    ctx.submitAlteredRequest(process, "''''\"\"\"\"", true, 3);
    ctx.submitAlteredRequest(process, "'\"'\"'\"'\"", true, 4);

    ctx.submitAlteredRequest(process, "' AND 1=1 -- ", true, 5);
    ctx.submitAlteredRequest(process, "' AND 1=2 -- ", true, 6);

    ctx.submitAlteredRequest(process, " AND 1=1 -- ", true, 7);
    ctx.submitAlteredRequest(process, " AND 1=2 -- ", true, 8);

    ctx.submitAlteredRequest(process, "\" AND 1=1 -- ", true, 9);
    ctx.submitAlteredRequest(process, "\" AND 1=2 -- ", true, 10);
       
    ctx.submitAlteredRequest(process, "'", true, 11);
    ctx.submitAlteredRequest(process, "\\'", true, 12);    
    ctx.submitAlteredRequest(process, "''", true, 13);
    
    ctx.submitAlteredRequest(process, "' UNION SELECT 8, table_name, 'vega' FROM information_schema.taables WHERE taable_name like'%", true, 14);   
    ctx.submitAlteredRequest(process, "' UNION SELECT 8, table_name, 'vega' FROM information_schema.tables WHERE table_name like'%", true, 15);
    ctx.submitAlteredRequest(process, "\" UNION SELECT 8, table_name, 'vega' FROM information_schema.taables WHERE taable_name like'%", true, 16);
    ctx.submitAlteredRequest(process, "\" UNION SELECT 8, table_name, 'vega' FROM information_schema.tables WHERE table_name like'%", true, 17);

    ctx.submitAlteredRequest(process, "1 AND 1=1 -- ", false, 18);
    ctx.submitAlteredRequest(process, "1 AND 1=2 -- ", false, 19);

    ctx.submitAlteredRequest(process, "' AND 1=1 -- ", false, 20);
    ctx.submitAlteredRequest(process, "' AND 1=2 -- ", false, 21);

    ctx.submitAlteredRequest(process, "\" AND 1=1 -- ", false, 22);
    ctx.submitAlteredRequest(process, "\" AND 1=2 -- ", false, 23);

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
  
  if (n < 24) return;
  
  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();

  var uri = String(ps.getPath().getUri());
  var uripart = uri.replace(/\?.*/, "");
  var param = ps.getFuzzableParameter().name;
  var pathkey;

  if (ps.getPath().isPostTarget() == true) {
    pathkey = "vinfo-sql-inject:" + uripart + "?" + "post" + "?" + param;
  }
  else
  {
    pathkey = "vinfo-sql-inject:" + uripart + "?" + "get" + "?" + param;
  }

  
  if (!ctx.isFingerprintMatch(0, 1) && !ctx.isFingerprintMatch(1, 2)) {
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });

    ctx.responseChecks(0);
    ctx.responseChecks(2);
  }
  
  if (ctx.isFingerprintMatch(1, 4) && !ctx.isFingerprintMatch(3, 4)) {
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(3);
    ctx.responseChecks(4);
  }

  if (ctx.isFingerprintMatch(5, fp) && !ctx.isFingerprintMatch(5, 6)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(6), ctx.getSavedResponse(6), {
      output: ctx.getSavedResponse(6).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(6);
  }

  if (ctx.isFingerprintMatch(7, fp) && !ctx.isFingerprintMatch(7, 8)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(8), ctx.getSavedResponse(8), {
      output: ctx.getSavedResponse(8).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(8);
  }

  if (ctx.isFingerprintMatch(9, fp) && !ctx.isFingerprintMatch(9, 10)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(10), ctx.getSavedResponse(10), {
      output: ctx.getSavedResponse(10).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(10);
  }
 
 
  if (ctx.isFingerprintMatch(19, fp) && !ctx.isFingerprintMatch(18, 19)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(19), ctx.getSavedResponse(19), {
      output: ctx.getSavedResponse(19).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(19);
  }

  if (!ctx.isFingerprintMatch(20, 21)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(21), ctx.getSavedResponse(21), {
      output: ctx.getSavedResponse(21).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(21);
  }

  if (!ctx.isFingerprintMatch(22, 23)) {
          ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(23), ctx.getSavedResponse(23), {
      output: ctx.getSavedResponse(19).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(23);
  }

  if (ctx.isFingerprintMatch(12, 13) && !ctx.isFingerprintMatch(11, 12)) {
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(1), ctx.getSavedResponse(1), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(12);
    ctx.responseChecks(13);
  }  

  if (ctx.isFingerprintMatch(14, fp) && !ctx.isFingerprintMatch(14, 15)) {
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(15), ctx.getSavedResponse(15), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(15);
  }  
  
  if (ctx.isFingerprintMatch(16, fp) && !ctx.isFingerprintMatch(17, 16)) {
	  ctx.alert("vinfo-sql-inject", ctx.getSavedRequest(16), ctx.getSavedResponse(16), {
      output: ctx.getSavedResponse(1).bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Text Injection Differential"
    });
    ctx.responseChecks(16);
  }  
}
