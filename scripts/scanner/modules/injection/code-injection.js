var module = {
  name: "Eval Code Injection",
  category: "Injection Modules",
  differential: false
};

var alteredRequests = [];

alteredRequests.push("phpinfo();");
alteredRequests.push("echo(str_repeat('vega',5));");

function initialize(ctx) {
  var ps = ctx.getPathState();
  
  var phpRegex = /\.php$/;
  var isPHP = phpRegex.test(ps.getPath().getUri());
  if (isPHP) {
    if (ps.isParametric) {
      ctx.submitAlteredRequest(process, alteredRequests[0], false, 0);
      ps.incrementFuzzCounter();
    }
  } 
}

function checkContent(res, ctx, index) {
  var phpInfoRegex = /<h1 class="p">PHP Version [0-9.]+<\/h1>/;
  var echoStringRegex = /vegavegavegavegavega/;
 
  if (phpInfoRegex.test(ctx.getSavedResponse(index).bodyAsString)) {
    return true;
  } else if (echoStringRegex.test(ctx.getSavedResponse(index).bodyAsString)) {
    return true;
  } else {
    return false;
  }
};

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;
  if (res.fetchFail) {
    ctx.error(req, res, "During eval code injection checks");
    ctx.setModuleFailed();
    return;
  }
  
  var ps = ctx.getPathState();
  var currentIndex = ctx.getCurrentIndex();
  ctx.addRequestResponse(req, res);
  ctx.incrementResponseCount();
  var detected = checkContent(res, ctx, currentIndex);
  
  if (detected) {
    var uri = String(req.requestLine.uri);
    var uripart = uri.replace(/\?.*/, "");
    ctx.alert("vinfo-code-inject", ctx.getSavedRequest(currentIndex), ctx.getSavedResponse(currentIndex), {
      output: res.bodyAsString,
      key: "vinfo-code-inject:" + uripart + ":" + ps.getFuzzableParameter().name,
      resource: uripart,
      detectiontype: "Response Checks",
      param: ps.getFuzzableParameter().name
    }); 
  } else {
    if (currentIndex + 1 < alteredRequests.length) {
      ctx.submitAlteredRequest(process, alteredRequests[currentIndex + 1].commandString, false, currentIndex + 1);
    }
  };

  if (ctx.allResponsesReceived()) {
    ps.decrementFuzzCounter();
  }
};
