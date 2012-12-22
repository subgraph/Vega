var module = {
  name: "Directory Listing and Traversal Checks",
  category: "Injection Modules",
  differential: true
};


function initialize(ctx) {
  var injectables = createInjectables(ctx);
  ctx.submitMultipleAlteredRequests(handler, injectables);
}

function createInjectables(ctx) {
  var ps = ctx.pathState;
  if (!ps.isParametric()) {
    return ["/./", "/.vega/", "\\.\\", "\\.vega\\"];
  }
  var fuzzable = ps.getFuzzableParameter();
  var injectables = [".../", "./", "...\\", ".\\"];
  var ret = [];
  for (var i = 0; i < injectables.length; i++)
  ret.push(injectables[i] + fuzzable.value);
  return ret;
}

function handler(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;

  if (res.fetchFail) {
    ctx.setModuleFailed();
    return;
  }

  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 4) return;

  var ps = ctx.getPathState();
  var fp = ps.getPathFingerprint();

  if (!ps.isParametric()) {
    if (ctx.getSavedResponse(0).code < 300 && !ctx.isFingerprintMatch(0, fp) && !ctx.isFingerprintMatch(0, 1)) {
      publishAlert(ctx, "Unique response for /./", 0, req, res);
      ctx.responseChecks(ps.createRequest(), ctx.getSavedResponse(0));
    }
    if (ctx.getSavedResponse(2).code < 300 && !ctx.isFingerprintMatch(2, fp) && !ctx.isFingerprintMatch(2, 3)) {
      publishAlert(ctx, "Unique response for \\.\\", 2, req, res);
      ctx.responseChecks(2);
    }
  } else {
    if (!ctx.isFingerprintMatch(0, 1)) {
      publishAlert(ctx, "Responses for ./val and .../val look different", 1, req, res);
      ctx.responseChecks(0);
    }
    if (!ctx.isFingerprintMatch(2, 3)) {
      publishAlert(ctx, "Responses for .\\val and ...\\val look different", 3, req, res);
      ctx.responseChecks(2);
    }
  }
}

function publishAlert(ctx, msg, idx) {

  ctx.alert("vinfo-directory-traversal", ctx.getSavedRequest(idx), ctx.getSavedResponse(idx), {
    output: ctx.getSavedResponse(idx).bodyAsString,
    key: "vinfo-1918:" + ctx.getSavedRequest(idx).requestLine.uri,
    resource: ctx.getSavedRequest(idx).requestLine.uri
  });
}
