var module = {
  name: "XML Injection checks",
  category: "Injection Modules",
  differential: true
};

function initialize(ctx) {
  var ps = ctx.getPathState();

  if (ps.isParametric()) {
    ctx.submitMultipleAlteredRequests(process, ["vega>'>\"><vega></vega>", "vega>'>\"></vega><vega>"]);
  }
}

function process(req, res, ctx) {
  if (ctx.hasModuleFailed()) return;

  if (res.fetchFail) {
    ctx.setModuleFailed();
    return;
  }
  ctx.addRequestResponse(req, res);
  if (ctx.incrementResponseCount() < 2) return;
  if (!ctx.isFingerprintMatch(0, 1)) {
    ctx.alert("vinfo-xml-inject", ctx.getSavedRequest(0), ctx.getSavedResponse(0),{
      message: "responses for <vega></vega> and </vega><vega> look different",
      resource: ctx.getSavedRequest(0).requestLine.uri
    });
    ctx.responseChecks(1);
  }

}
