var module = {
  name: "Blind SQL Injection Timing",
  category: "Injection Modules",
  differential: false,
  defaultDisabled: true
};

var requests = [["1 AND SLEEP(30) -- ", false],
               ["1 OR (SELECT SLEEP(30)) -- ", false],
               ["\" AND SLEEP(30) -- ", true],
               ["' AND SLEEP(30) -- ", true],
               ["\" OR (SELECT SLEEP(30)) -- ", true],
               ["\' OR (SELECT SLEEP(30)) -- ", true],
               ["' OR EXISTS(SELECT SLEEP(30)) -- ", true],
               [" OR EXISTS(SELECT SLEEP(30)) -- ", true],
               ["\" OR EEXISTS(SELECT SLEEP(30)) -- ", true],
               [" UNION SELECT SLEEP(30) -- ", true],
               ["' UNION SELECT SLEEP(30) -- ", true],
               ["\" UNION SELECT SLEEP(30) -- ", true],
               ["(1 AND (SELECT SLEEP(30))) -- ", false],
	       ["1' AND SLEEP(30) -- ", false],
               ["1\" AND SLEEP(30) -- ", false],

               /* SQL Server */ 

               [" 1 AND (SELECT COUNT(*) from sysusers AS v1, sysusers AS v2, sysusers AS v3, sysusers AS v3, sysusers AS v4, sysusers AS v5, sysusers AS v6, sysusers AS v7, sysusers AS v8, sysusers AS v9, sysusers AS v10, sysusers AS v11, sysusers AS v12, sysusers AS v13, sysusers AS v14, sysusers AS v15, sysusers AS v16, sysusers AS v17, sysusers AS v18, sysusers AS v19, sysusers AS v30) -- ", false],
               ["\" WAITFOR DELAY '00:00:30' -- ", true],
               ["' WAITFOR DELAY '00:00:30' -- ", true],
               ["1 WAITFOR DELAY '00:00:30' --", false],
         
               /* PostgreSQL */

               ["1 AND 81223=(SELECT 32233 FROM PG_SLEEP(35)) -- ",false],
               ["\" AND 81223=(SELECT 32233 FROM PG_SLEEP(35)) -- ",true],
               ["' AND 81223=(SELECT 32233 FROM PG_SLEEP(35)) -- ",true],
               ["\" AND 81223=(SELECT 32233 FROM PG_SLEEP(35)) --",false],
               ["' AND 81223=(SELECT 32233 FROM PG_SLEEP(35)) --", false]];


function pathkey(ps) {

  var uri = ps.getPath().getUri();
  var param = ps.getFuzzableParameter().name;
  if (ps.getPath().isPostTarget() == true) {
    return uri + "?" + "post" + "?" + param;
  }
  else
  {
    return uri + "?" + "get" + "?" + param;
  }

}

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
     
    ctx.setIntegerProperty(pathkey + "?detected", 0);
    ctx.setIntegerProperty(pathkey + "?alerted", 0);

    ctx.submitMultipleAlteredRequests(checkTiming, ["bad1", "bad2"], false);    
    ps.incrementFuzzCounter();
  }
}

function checkTiming(req, res, ctx) {
  var ps = ctx.getPathState();

  var uri = String(ps.getPath().getUri());
  var uripart = uri.replace(/\?.*/, "");
  var pathkey;
  var param = ps.getFuzzableParameter().name;

  if (ps.getPath().isPostTarget() == true) {
    pathkey = "vinfo-sql-inject:" + uripart + "?" + "post" + "?" + param;
  }
  else
  {
    pathkey = "vinfo-sql-inject:" + uripart + "?" + "get" + "?" + param;
  }

  if (res.fetchFail) {
    ctx.error(req, res, "During command injection checks");
    ctx.setModuleFailed();
    return;
  }
  
  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 2) return;
  var first = ctx.getSavedResponse(0).milliseconds;
  var second = ctx.getSavedResponse(1).milliseconds;
  ctx.setIntegerProperty(pathkey + "?average", (first + second / 2));
  
  ctx.submitAlteredRequest(process, requests[0][0], requests[0][1], 0);
}




function process(req2, res2, ctx2) {  
  var ps = ctx2.getPathState();

  var currentIndex = ctx2.getCurrentIndex();

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

  var average = ctx2.getIntegerProperty(pathkey + "?average");

  ctx2.addRequestResponse(req2, res2);
  ctx2.incrementResponseCount();

  if ((average < 30000) && (ctx2.getSavedResponse(currentIndex).milliseconds >= 30000)) {
    ctx2.setIntegerProperty(pathkey + "?detected", 1);
  }

   var detected = ctx2.getIntegerProperty(pathkey + "?detected");
   var alerted = ctx2.getIntegerProperty(pathkey + "?alerted");

  if ((detected == 1) && (alerted == 0)) {

    ctx2.setIntegerProperty(pathkey + "?alerted", 1);

    ctx2.alert("vinfo-sql-inject", ctx2.getSavedRequest(currentIndex), ctx2.getSavedResponse(currentIndex), {
      output: res2.bodyAsString,
      key: pathkey,
      resource: uripart,
      detectiontype: "Blind Timing Analysis Checks",
      param: ps.getFuzzableParameter().name
    }); 
  } else {
    if ((currentIndex + 1) < requests.length) {
    var k= pathkey;

    if (ctx2.alertExists(k)) {
      return;
    }
      ctx2.submitAlteredRequest(process, requests[currentIndex + 1][0], requests[currentIndex + 1][1], currentIndex + 1);
    }
  }

  if (ctx2.allResponsesReceived()) {
    ps.decrementFuzzCounter();
  }
}


