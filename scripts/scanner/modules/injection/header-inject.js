var module = {
  name: "HTTP Header Injection checks",
  category: "Injection Modules"
};

function initialize(ctx) {
  ctx.submitMultipleAlteredRequests(process, ["bogus\nVega-Inject:bogus", "bogus\rVega-Inject:bogus"], true);
}

function process(req, res, ctx) {
  if (res.hasHeader("Vega-Inject")) {
    ctx.alert("vinfo-header-inject", request, response, {
      message: "Injected Vega-Inject header into response",
      resource: request.requestLine.uri
    });
  }
}
