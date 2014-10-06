var module = {
  name: "Format String Injection Checks",
  category: "Injection Modules",
  differential: true,
  defaultDisabled: true
};

function initialize(ctx) {
  var ps = ctx.getPathState();

  if (ps.isParametric()) {
    ctx.submitMultipleAlteredRequests(process, ["vega%dn%dn%dn%dn%dn%dn%dn%dn", "vega%nd%nd%nd%nd%nd%nd%nd%nd"]);
  }
}

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;
  if (res.fetchFail) {
    ctx.error(req, res, "During format string injection checks");
    ctx.setModuleFailed();
    return;
  }
  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 2) return;

  if (!ctx.isFingerprintMatch(0, 1)) {
    ctx.alert("vinfo-format-string", "Response to %dn%dn%dn... different than to %nd%nd%nd...", ctx.getSavedRequest(1), ctx.getSavedResponse(1));
    ctx.responseChecks(1);
  }
}
